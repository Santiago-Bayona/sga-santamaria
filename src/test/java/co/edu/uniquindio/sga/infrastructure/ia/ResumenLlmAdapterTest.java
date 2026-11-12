package co.edu.uniquindio.sga.infrastructure.ia;

import co.edu.uniquindio.sga.domain.exception.GeneradorResumenNoDisponibleException;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.DatosResumenDiario;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.MotivoDegradacionIa;
import co.edu.uniquindio.sga.domain.valueobject.MovimientoDiario;
import co.edu.uniquindio.sga.domain.valueobject.ResumenRedactado;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Adaptador real de IA contra un servidor HTTP local de mentira (I-08): ninguna prueba toca un servicio externo.
 * Se verifica lo que sale (sin datos personales, con la clave solo en el encabezado) y cada forma de fallar.
 */
class ResumenLlmAdapterTest {

    private static final DatosResumenDiario DATOS = new DatosResumenDiario(LocalDate.of(2026, 11, 12),
            List.of(new MovimientoDiario(new CodigoReserva("RES-1"), new IdentificacionApartamento("APT-01"),
                    LocalTime.of(15, 0), 3)),
            List.of());

    /** Servidor local que responde con el estado y el cuerpo dados, tras una demora opcional. */
    private static HttpServer servidor(int estado, String cuerpo, long demoraMs,
                                       AtomicReference<String> peticion, AtomicReference<String> autorizacion)
            throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/v1/chat/completions", intercambio -> {
            if (peticion != null) {
                peticion.set(new String(intercambio.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            }
            if (autorizacion != null) {
                autorizacion.set(intercambio.getRequestHeaders().getFirst("Authorization"));
            }
            try {
                Thread.sleep(demoraMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            byte[] bytes = cuerpo.getBytes(StandardCharsets.UTF_8);
            try {
                intercambio.sendResponseHeaders(estado, bytes.length);
                intercambio.getResponseBody().write(bytes);
            } catch (IOException cerradoPorElCliente) {
                // el cliente ya se rindió por tiempo: es lo que se quiere probar
            } finally {
                intercambio.close();
            }
        });
        server.start();
        return server;
    }

    private static String urlBase(HttpServer server) {
        return "http://127.0.0.1:" + server.getAddress().getPort() + "/v1";
    }

    private static GeneradorResumenNoDisponibleException falla(ResumenLlmAdapter adapter) {
        return assertThrows(GeneradorResumenNoDisponibleException.class, () -> adapter.generar(DATOS));
    }

    @Test
    @DisplayName("Respuesta correcta: extrae el texto, lo marca como generado por modelo y no filtra datos personales")
    void respuestaCorrecta() throws Exception {
        AtomicReference<String> peticion = new AtomicReference<>();
        AtomicReference<String> autorizacion = new AtomicReference<>();
        HttpServer server = servidor(200,
                "{\"choices\":[{\"message\":{\"role\":\"assistant\",\"content\":\"Hoy llega 1 grupo.\"}}]}",
                0, peticion, autorizacion);
        try {
            ResumenRedactado resumen = new ResumenLlmAdapter(urlBase(server), "modelo-x", "clave-secreta",
                    Duration.ofSeconds(2)).generar(DATOS);

            assertEquals("Hoy llega 1 grupo.", resumen.texto());
            assertTrue(resumen.generadoPorModelo());
            assertEquals("Bearer clave-secreta", autorizacion.get());
            assertTrue(peticion.get().contains("\"model\":\"modelo-x\""));
            assertTrue(peticion.get().contains("RES-1 (APT-01, 3 personas, hacia las 15:00)"));
            assertFalse(peticion.get().contains("clave-secreta"), "la clave solo va en el encabezado");
        } finally {
            server.stop(0);
        }
    }

    @Test
    @DisplayName("Entiende los escapes de JSON en el texto devuelto")
    void escapesDeJson() throws Exception {
        HttpServer server = servidor(200,
                "{\"choices\":[{\"message\":{\"content\":\"Línea 1\\nDijo \\\"hola\\\" \\u00e1\"}}]}", 0, null, null);
        try {
            ResumenRedactado resumen = new ResumenLlmAdapter(urlBase(server), "m", "k", Duration.ofSeconds(2))
                    .generar(DATOS);

            assertEquals("Línea 1\nDijo \"hola\" á", resumen.texto());
        } finally {
            server.stop(0);
        }
    }

    @Test
    @DisplayName("I-03: si el modelo tarda más del tiempo máximo, el motivo es TIEMPO_AGOTADO")
    void tiempoAgotado() throws Exception {
        HttpServer server = servidor(200, "{\"choices\":[{\"message\":{\"content\":\"tarde\"}}]}", 1500, null, null);
        try {
            long inicio = System.nanoTime();
            GeneradorResumenNoDisponibleException e = falla(
                    new ResumenLlmAdapter(urlBase(server), "m", "k", Duration.ofMillis(300)));

            assertEquals(MotivoDegradacionIa.TIEMPO_AGOTADO, e.getMotivo());
            assertTrue(Duration.ofNanos(System.nanoTime() - inicio).toMillis() < 1400, "no esperó la respuesta completa");
        } finally {
            server.stop(0);
        }
    }

    @Test
    @DisplayName("Un error HTTP del proveedor es ERROR_PROVEEDOR y no revela la clave")
    void errorHttp() throws Exception {
        HttpServer server = servidor(500, "{\"error\":\"boom\"}", 0, null, null);
        try {
            GeneradorResumenNoDisponibleException e = falla(
                    new ResumenLlmAdapter(urlBase(server), "m", "clave-secreta", Duration.ofSeconds(2)));

            assertEquals(MotivoDegradacionIa.ERROR_PROVEEDOR, e.getMotivo());
            assertFalse(e.getMessage().contains("clave-secreta"));
        } finally {
            server.stop(0);
        }
    }

    @Test
    @DisplayName("Una respuesta sin texto o con JSON roto es ERROR_PROVEEDOR")
    void respuestaInservible() throws Exception {
        for (String cuerpo : List.of("{\"choices\":[]}", "{\"choices\":[{\"message\":{\"content\":null}}]}",
                "no es json", "{\"choices\":[{\"message\":{\"content\":\"   \"}}]}")) {
            HttpServer server = servidor(200, cuerpo, 0, null, null);
            try {
                GeneradorResumenNoDisponibleException e = falla(
                        new ResumenLlmAdapter(urlBase(server), "m", "k", Duration.ofSeconds(2)));

                assertEquals(MotivoDegradacionIa.ERROR_PROVEEDOR, e.getMotivo());
            } finally {
                server.stop(0);
            }
        }
    }

    @Test
    @DisplayName("Un proveedor caído (nadie escucha) es ERROR_PROVEEDOR")
    void proveedorCaido() throws Exception {
        HttpServer server = servidor(200, "{}", 0, null, null);
        String url = urlBase(server);
        server.stop(0);

        GeneradorResumenNoDisponibleException e = falla(new ResumenLlmAdapter(url, "m", "k", Duration.ofSeconds(2)));

        assertEquals(MotivoDegradacionIa.ERROR_PROVEEDOR, e.getMotivo());
    }

    @Test
    @DisplayName("Sin URL, modelo o clave no se llama a nadie y el motivo es ERROR_PROVEEDOR")
    void sinConfiguracion() {
        assertEquals(MotivoDegradacionIa.ERROR_PROVEEDOR,
                falla(new ResumenLlmAdapter("", "m", "k", Duration.ofSeconds(1))).getMotivo());
        assertEquals(MotivoDegradacionIa.ERROR_PROVEEDOR,
                falla(new ResumenLlmAdapter("http://localhost:1/v1", "", "k", Duration.ofSeconds(1))).getMotivo());
        assertEquals(MotivoDegradacionIa.ERROR_PROVEEDOR,
                falla(new ResumenLlmAdapter("http://localhost:1/v1", "m", "", Duration.ofSeconds(1))).getMotivo());
        assertEquals(MotivoDegradacionIa.ERROR_PROVEEDOR,
                falla(new ResumenLlmAdapter(null, null, null, Duration.ofSeconds(1))).getMotivo());
    }

    @Test
    @DisplayName("Una URL sin https:// o con espacios es ERROR_PROVEEDOR y el mensaje habla de la URL")
    void urlInvalida() {
        for (String url : List.of("api.groq.com/openai/v1", "https://api groq.com/v1", "<https://x.com/v1>")) {
            GeneradorResumenNoDisponibleException e = falla(new ResumenLlmAdapter(url, "m", "k", Duration.ofSeconds(1)));

            assertEquals(MotivoDegradacionIa.ERROR_PROVEEDOR, e.getMotivo());
            assertTrue(e.getMessage().contains("URL base"), url);
        }
    }

    @Test
    @DisplayName("Una clave con caracteres no permitidos en un encabezado se señala sin revelarla")
    void claveConCaracteresRaros() {
        for (String clave : List.of("gsk_ab\ncd", "gsk_ab…cd", "gsk_ab\rcd")) {
            GeneradorResumenNoDisponibleException e = falla(
                    new ResumenLlmAdapter("https://api.groq.com/openai/v1", "m", clave, Duration.ofSeconds(1)));

            assertEquals(MotivoDegradacionIa.ERROR_PROVEEDOR, e.getMotivo());
            assertTrue(e.getMessage().contains("clave"));
            assertFalse(e.getMessage().contains("gsk_ab"), "el mensaje no debe contener la clave");
        }
    }

    @Test
    @DisplayName("Lectura del texto: toma el primer content de tipo cadena y devuelve nulo si no hay")
    void lecturaDelContenido() {
        assertEquals("a", ResumenLlmAdapter.primerContenido("{\"x\":1,\"content\" : \"a\"}"));
        assertNull(ResumenLlmAdapter.primerContenido("{\"content\":null}"));
        assertNull(ResumenLlmAdapter.primerContenido("{\"otra\":\"cosa\"}"));
        assertNull(ResumenLlmAdapter.primerContenido("{\"content\":\"sin cerrar"));
        assertNull(ResumenLlmAdapter.primerContenido(null));
    }

    @Test
    @DisplayName("Escritura de cadenas JSON: comillas, barras y saltos de línea")
    void escrituraDeCadenas() {
        assertEquals("\"a\\\"b\\\\c\\nd\"", ResumenLlmAdapter.cadena("a\"b\\c\nd"));
    }
}

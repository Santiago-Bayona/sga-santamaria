package co.edu.uniquindio.sga.infrastructure.ia;

import co.edu.uniquindio.sga.domain.exception.GeneradorResumenNoDisponibleException;
import co.edu.uniquindio.sga.domain.port.GeneradorResumenPort;
import co.edu.uniquindio.sga.domain.service.ResumenBasicoDiario;
import co.edu.uniquindio.sga.domain.valueobject.DatosResumenDiario;
import co.edu.uniquindio.sga.domain.valueobject.MotivoDegradacionIa;
import co.edu.uniquindio.sga.domain.valueobject.ResumenRedactado;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Adaptador real del modelo de IA (10.2): habla con cualquier proveedor que exponga la API de chat de OpenAI
 * ({@code POST {urlBase}/chat/completions}), como Groq, Gemini, OpenRouter u Ollama. Cambiar de proveedor es
 * cambiar configuración, no código. Usa solo el cliente HTTP del JDK.
 *
 * <p>Tiempo máximo explícito (I-03): vence a los {@code tiempoEsperaMs}. Cualquier fallo se traduce a
 * {@link GeneradorResumenNoDisponibleException} con el motivo que corresponde (I-04); quien lo recibe degrada el
 * resumen y sigue operando (I-05). La clave nunca se escribe en un mensaje de error ni en el log (I-06).
 */
public class ResumenLlmAdapter implements GeneradorResumenPort {

    private static final String INSTRUCCION = "Eres el asistente de operación de un alojamiento turístico. "
            + "Redacta en español, en un solo párrafo de máximo cuatro frases, un resumen claro del día para "
            + "recepción a partir de los datos que recibes. Usa únicamente esos datos: no inventes nombres, "
            + "horas ni cifras.";

    private final String urlCompletado;
    private final String modelo;
    private final String clave;
    private final Duration tiempoEspera;
    private final HttpClient cliente;

    public ResumenLlmAdapter(String urlBase, String modelo, String clave, Duration tiempoEspera) {
        this.urlCompletado = urlBase == null ? "" : quitarBarraFinal(urlBase.trim()) + "/chat/completions";
        this.modelo = modelo == null ? "" : modelo.trim();
        this.clave = clave == null ? "" : clave.trim();
        this.tiempoEspera = tiempoEspera;
        this.cliente = HttpClient.newBuilder().connectTimeout(tiempoEspera).build();
    }

    @Override
    public ResumenRedactado generar(DatosResumenDiario datos) {
        if (urlCompletado.equals("/chat/completions") || modelo.isEmpty() || clave.isEmpty()) {
            throw new GeneradorResumenNoDisponibleException(MotivoDegradacionIa.ERROR_PROVEEDOR,
                    "Faltan sga.ia.url-base, sga.ia.modelo o la clave del proveedor.");
        }
        URI destino;
        try {
            destino = URI.create(urlCompletado);
            if (destino.getScheme() == null || destino.getHost() == null) {
                throw new IllegalArgumentException("sin esquema o sin servidor");
            }
        } catch (IllegalArgumentException e) {
            throw new GeneradorResumenNoDisponibleException(MotivoDegradacionIa.ERROR_PROVEEDOR,
                    "La URL base del proveedor de IA no es válida: debe empezar por https:// y no llevar espacios.", e);
        }
        HttpRequest peticion;
        try {
            peticion = HttpRequest.newBuilder(destino)
                    .timeout(tiempoEspera)
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + clave)
                    .POST(HttpRequest.BodyPublishers.ofString(cuerpo(datos), StandardCharsets.UTF_8))
                    .build();
        } catch (IllegalArgumentException e) {
            // Casi siempre es la clave con un carácter no permitido en un encabezado (salto de línea, tilde, "…").
            // El mensaje no incluye la clave (I-06).
            throw new GeneradorResumenNoDisponibleException(MotivoDegradacionIa.ERROR_PROVEEDOR,
                    "No se pudo armar la petición al proveedor de IA: revise que la clave no tenga espacios, "
                            + "comillas ni caracteres raros.");
        }
        try {
            HttpResponse<String> respuesta = cliente.send(peticion,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (respuesta.statusCode() / 100 != 2) {
                throw new GeneradorResumenNoDisponibleException(MotivoDegradacionIa.ERROR_PROVEEDOR,
                        "El proveedor de IA respondió con estado " + respuesta.statusCode() + ".");
            }
            String texto = primerContenido(respuesta.body());
            if (texto == null || texto.isBlank()) {
                throw new GeneradorResumenNoDisponibleException(MotivoDegradacionIa.ERROR_PROVEEDOR,
                        "El proveedor de IA no devolvió texto.");
            }
            return new ResumenRedactado(texto.trim(), true);
        } catch (HttpTimeoutException e) {   // incluye el tiempo agotado al conectar
            throw new GeneradorResumenNoDisponibleException(MotivoDegradacionIa.TIEMPO_AGOTADO,
                    "El proveedor de IA no respondió en " + tiempoEspera.toMillis() + " ms.", e);
        } catch (IOException e) {
            throw new GeneradorResumenNoDisponibleException(MotivoDegradacionIa.ERROR_PROVEEDOR,
                    "No se pudo hablar con el proveedor de IA: " + e.getClass().getSimpleName() + ".", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new GeneradorResumenNoDisponibleException(MotivoDegradacionIa.ERROR_PROVEEDOR,
                    "La llamada al proveedor de IA fue interrumpida.", e);
        }
    }

    /** Cuerpo de la petición. Solo lleva el resumen básico del día: códigos, apartamentos, horas y personas. */
    private String cuerpo(DatosResumenDiario datos) {
        return "{\"model\":" + cadena(modelo) + ",\"messages\":["
                + "{\"role\":\"system\",\"content\":" + cadena(INSTRUCCION) + "},"
                + "{\"role\":\"user\",\"content\":" + cadena(ResumenBasicoDiario.redactar(datos)) + "}]}";
    }

    private static String quitarBarraFinal(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    /** Literal de cadena JSON, con comillas y escapes. */
    static String cadena(String valor) {
        StringBuilder json = new StringBuilder("\"");
        for (int i = 0; i < valor.length(); i++) {
            char c = valor.charAt(i);
            switch (c) {
                case '"' -> json.append("\\\"");
                case '\\' -> json.append("\\\\");
                case '\n' -> json.append("\\n");
                case '\r' -> json.append("\\r");
                case '\t' -> json.append("\\t");
                default -> {
                    if (c < 0x20) {
                        json.append(String.format("\\u%04x", (int) c));
                    } else {
                        json.append(c);
                    }
                }
            }
        }
        return json.append('"').toString();
    }

    /**
     * Texto de la primera clave {@code "content"} con valor de cadena. En la API de chat de OpenAI es
     * {@code choices[0].message.content}. Devuelve nulo si no hay.
     */
    static String primerContenido(String json) {
        if (json == null) {
            return null;
        }
        int desde = 0;
        while (true) {
            int clave = json.indexOf("\"content\"", desde);
            if (clave < 0) {
                return null;
            }
            int i = clave + "\"content\"".length();
            while (i < json.length() && Character.isWhitespace(json.charAt(i))) {
                i++;
            }
            if (i < json.length() && json.charAt(i) == ':') {
                i++;
                while (i < json.length() && Character.isWhitespace(json.charAt(i))) {
                    i++;
                }
                if (i < json.length() && json.charAt(i) == '"') {
                    return leerCadena(json, i + 1);
                }
                return null;   // "content": null u otro tipo
            }
            desde = clave + 1;
        }
    }

    private static String leerCadena(String json, int inicio) {
        StringBuilder texto = new StringBuilder();
        for (int i = inicio; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '"') {
                return texto.toString();
            }
            if (c != '\\') {
                texto.append(c);
                continue;
            }
            if (++i >= json.length()) {
                return null;
            }
            char e = json.charAt(i);
            switch (e) {
                case 'n' -> texto.append('\n');
                case 'r' -> texto.append('\r');
                case 't' -> texto.append('\t');
                case 'b' -> texto.append('\b');
                case 'f' -> texto.append('\f');
                case 'u' -> {
                    if (i + 4 >= json.length()) {
                        return null;
                    }
                    try {
                        texto.append((char) Integer.parseInt(json.substring(i + 1, i + 5), 16));
                    } catch (NumberFormatException ex) {
                        return null;
                    }
                    i += 4;
                }
                default -> texto.append(e);   // \" \\ \/
            }
        }
        return null;   // cadena sin cerrar
    }
}

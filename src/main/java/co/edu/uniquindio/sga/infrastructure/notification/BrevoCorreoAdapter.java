package co.edu.uniquindio.sga.infrastructure.notification;

import co.edu.uniquindio.sga.infrastructure.exception.IntegracionExternaException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Correo real con la API transaccional de Brevo ({@code POST https://api.brevo.com/v3/smtp/email}).
 * El remitente debe estar verificado en Brevo (Senders &amp; IP → Senders); puede ser una cuenta de Gmail.
 * Usa solo el cliente HTTP del JDK. La clave nunca se escribe en un mensaje de error ni en el log.
 */
public class BrevoCorreoAdapter implements CorreoNotificacionPort {

    private static final URI API = URI.create("https://api.brevo.com/v3/smtp/email");

    private final String clave;
    private final String remitente;
    private final String nombreRemitente;
    private final Duration tiempoEspera;
    private final HttpClient cliente;

    public BrevoCorreoAdapter(String clave, String remitente, String nombreRemitente, Duration tiempoEspera) {
        this.clave = clave == null ? "" : clave.trim();
        this.remitente = remitente == null ? "" : remitente.trim();
        this.nombreRemitente = nombreRemitente;
        this.tiempoEspera = tiempoEspera;
        this.cliente = HttpClient.newBuilder().connectTimeout(tiempoEspera).build();
    }

    @Override
    public void enviar(String destinatario, String asunto, String cuerpo) {
        enviarHtml(destinatario, asunto, cuerpo, null);
    }

    @Override
    public void enviarHtml(String destinatario, String asunto, String texto, String html) {
        if (clave.isEmpty() || remitente.isEmpty()) {
            throw new IntegracionExternaException("Faltan sga.correo.remitente o la clave de Brevo (SGA_BREVO_CLAVE).");
        }
        if (destinatario == null || !destinatario.contains("@")) {
            throw new IntegracionExternaException("El destinatario no es un correo válido.");
        }
        HttpRequest peticion;
        try {
            peticion = HttpRequest.newBuilder(API)
                    .timeout(tiempoEspera)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("api-key", clave)
                    .POST(HttpRequest.BodyPublishers.ofString(cuerpo(destinatario, asunto, texto, html),
                            StandardCharsets.UTF_8))
                    .build();
        } catch (IllegalArgumentException e) {
            throw new IntegracionExternaException("La clave de Brevo tiene caracteres no permitidos (espacios, comillas, saltos de línea).");
        }
        try {
            HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (respuesta.statusCode() / 100 != 2) {
                // El cuerpo de error de Brevo dice el motivo (remitente sin verificar, clave inválida…) y no trae la clave.
                throw new IntegracionExternaException("Brevo respondió " + respuesta.statusCode() + ": " + respuesta.body());
            }
        } catch (IOException e) {
            throw new IntegracionExternaException("No se pudo hablar con Brevo: " + e.getClass().getSimpleName() + ".", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IntegracionExternaException("El envío a Brevo fue interrumpido.", e);
        }
    }

    String cuerpo(String destinatario, String asunto, String texto, String html) {
        StringBuilder json = new StringBuilder("{")
                .append("\"sender\":{\"name\":").append(cadena(nombreRemitente))
                .append(",\"email\":").append(cadena(remitente)).append("},")
                .append("\"to\":[{\"email\":").append(cadena(destinatario.trim())).append("}],")
                .append("\"subject\":").append(cadena(asunto)).append(',')
                .append("\"textContent\":").append(cadena(texto == null ? "" : texto));
        if (html != null && !html.isBlank()) {
            json.append(",\"htmlContent\":").append(cadena(html));
        }
        return json.append('}').toString();
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
}

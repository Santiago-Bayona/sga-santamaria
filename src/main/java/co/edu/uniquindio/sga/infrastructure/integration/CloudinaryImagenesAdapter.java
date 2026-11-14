package co.edu.uniquindio.sga.infrastructure.integration;

import co.edu.uniquindio.sga.domain.port.AlmacenamientoImagenesPort;
import co.edu.uniquindio.sga.infrastructure.exception.IntegracionExternaException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Galería en Cloudinary (7.3): subida firmada a {@code POST https://api.cloudinary.com/v1_1/{cloud}/image/upload}
 * con el cliente HTTP del JDK. Devuelve la {@code secure_url} (https). El secreto nunca se envía ni se registra:
 * solo viaja la firma SHA-1 de los parámetros.
 */
public class CloudinaryImagenesAdapter implements AlmacenamientoImagenesPort {

    private static final Pattern SECURE_URL = Pattern.compile("\"secure_url\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern MENSAJE_ERROR = Pattern.compile("\"message\"\\s*:\\s*\"([^\"]+)\"");

    private final String nubeNombre;
    private final String apiKey;
    private final String apiSecret;
    private final String carpeta;
    private final Duration tiempoEspera;
    private final HttpClient cliente;

    public CloudinaryImagenesAdapter(String nubeNombre, String apiKey, String apiSecret, String carpeta,
                                     Duration tiempoEspera) {
        this.nubeNombre = nubeNombre.trim();
        this.apiKey = apiKey.trim();
        this.apiSecret = apiSecret.trim();
        this.carpeta = carpeta == null || carpeta.isBlank() ? "sga" : carpeta.trim();
        this.tiempoEspera = tiempoEspera;
        this.cliente = HttpClient.newBuilder().connectTimeout(tiempoEspera).build();
    }

    @Override
    public String guardar(String nombre, String tipoContenido, byte[] contenido) {
        String idPublico = nombre.contains(".") ? nombre.substring(0, nombre.lastIndexOf('.')) : nombre;
        String marca = String.valueOf(Instant.now().getEpochSecond());
        // Parámetros firmados en orden alfabético, sin file, api_key ni cloud_name (documentación de Cloudinary)
        String firma = sha1("folder=" + carpeta + "&public_id=" + idPublico + "&timestamp=" + marca + apiSecret);

        String limite = "----sga" + UUID.randomUUID().toString().replace("-", "");
        ByteArrayOutputStream cuerpo = new ByteArrayOutputStream();
        campo(cuerpo, limite, "api_key", apiKey);
        campo(cuerpo, limite, "timestamp", marca);
        campo(cuerpo, limite, "folder", carpeta);
        campo(cuerpo, limite, "public_id", idPublico);
        campo(cuerpo, limite, "signature", firma);
        escribir(cuerpo, "--" + limite + "\r\nContent-Disposition: form-data; name=\"file\"; filename=\"" + nombre
                + "\"\r\nContent-Type: " + tipoContenido + "\r\n\r\n");
        cuerpo.writeBytes(contenido);
        escribir(cuerpo, "\r\n--" + limite + "--\r\n");

        HttpRequest peticion = HttpRequest.newBuilder(URI.create("https://api.cloudinary.com/v1_1/" + nubeNombre + "/image/upload"))
                .timeout(tiempoEspera)
                .header("Content-Type", "multipart/form-data; boundary=" + limite)
                .POST(HttpRequest.BodyPublishers.ofByteArray(cuerpo.toByteArray()))
                .build();
        try {
            HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (respuesta.statusCode() / 100 != 2) {
                Matcher m = MENSAJE_ERROR.matcher(respuesta.body());
                throw new IntegracionExternaException("Cloudinary respondió " + respuesta.statusCode()
                        + (m.find() ? ": " + m.group(1) : "."));
            }
            Matcher url = SECURE_URL.matcher(respuesta.body());
            if (!url.find()) {
                throw new IntegracionExternaException("Cloudinary no devolvió la URL de la imagen.");
            }
            return url.group(1).replace("\\/", "/");
        } catch (IOException e) {
            throw new IntegracionExternaException("No se pudo hablar con Cloudinary: " + e.getClass().getSimpleName() + ".", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IntegracionExternaException("La subida a Cloudinary fue interrumpida.", e);
        }
    }

    private static void campo(ByteArrayOutputStream cuerpo, String limite, String nombre, String valor) {
        escribir(cuerpo, "--" + limite + "\r\nContent-Disposition: form-data; name=\"" + nombre + "\"\r\n\r\n" + valor + "\r\n");
    }

    private static void escribir(ByteArrayOutputStream cuerpo, String texto) {
        cuerpo.writeBytes(texto.getBytes(StandardCharsets.UTF_8));
    }

    static String sha1(String texto) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-1").digest(texto.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-1 no disponible", e);
        }
    }
}

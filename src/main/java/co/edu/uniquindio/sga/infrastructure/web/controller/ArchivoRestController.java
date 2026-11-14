package co.edu.uniquindio.sga.infrastructure.web.controller;

import co.edu.uniquindio.sga.infrastructure.filesystem.AlmacenamientoArchivosPort;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.regex.Pattern;

/**
 * Sirve las imágenes guardadas en el respaldo local (I-02). Es público: son las fotos de los apartamentos.
 * Solo acepta nombres generados por el sistema, así que no se puede pedir otro archivo del disco.
 */
@RestController
@RequestMapping("/api/archivos/imagenes")
public class ArchivoRestController {

    private static final Pattern NOMBRE_VALIDO = Pattern.compile("^[a-z0-9-]{1,120}\\.(jpg|png|webp)$");

    private final AlmacenamientoArchivosPort archivos;

    public ArchivoRestController(AlmacenamientoArchivosPort archivos) {
        this.archivos = archivos;
    }

    @GetMapping("/{nombre:.+}")
    public ResponseEntity<byte[]> imagen(@PathVariable String nombre) {
        if (!NOMBRE_VALIDO.matcher(nombre).matches()) {
            return ResponseEntity.notFound().build();
        }
        byte[] contenido;
        try {
            contenido = archivos.leer(nombre);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
        MediaType tipo = nombre.endsWith(".png") ? MediaType.IMAGE_PNG
                : nombre.endsWith(".webp") ? MediaType.parseMediaType("image/webp") : MediaType.IMAGE_JPEG;
        return ResponseEntity.ok()
                .contentType(tipo)
                .cacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePublic())
                .header("X-Content-Type-Options", "nosniff")
                .body(contenido);
    }
}

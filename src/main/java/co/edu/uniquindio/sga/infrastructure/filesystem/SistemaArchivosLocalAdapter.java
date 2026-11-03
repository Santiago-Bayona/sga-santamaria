package co.edu.uniquindio.sga.infrastructure.filesystem;

import co.edu.uniquindio.sga.infrastructure.exception.IntegracionExternaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Guarda y lee archivos bajo sga.archivos.directorio. Rechaza rutas que se salgan de esa raiz.
 */

@Component
public class SistemaArchivosLocalAdapter implements AlmacenamientoArchivosPort {

    private final Path raiz;

    public SistemaArchivosLocalAdapter(@Value("${sga.archivos.directorio:data/galeria}") String directorio) {
        this.raiz = Path.of(directorio).toAbsolutePath().normalize();
    }

    @Override
    public String guardar(String nombreLogico, byte[] contenido) {
        try {
            Files.createDirectories(raiz);
            Path destino = raiz.resolve(nombreLogico).normalize();
            if (!destino.startsWith(raiz)) {
                throw new IntegracionExternaException("Ruta de archivo fuera del directorio permitido.");
            }
            Files.write(destino, contenido);
            return destino.toUri().toString();
        } catch (IOException e) {
            throw new IntegracionExternaException("No se pudo guardar el archivo " + nombreLogico, e);
        }
    }

    @Override
    public byte[] leer(String rutaRelativa) {
        try {
            Path origen = raiz.resolve(rutaRelativa).normalize();
            if (!origen.startsWith(raiz)) {
                throw new IntegracionExternaException("Ruta de archivo fuera del directorio permitido.");
            }
            return Files.readAllBytes(origen);
        } catch (IOException e) {
            throw new IntegracionExternaException("No se pudo leer el archivo " + rutaRelativa, e);
        }
    }
}

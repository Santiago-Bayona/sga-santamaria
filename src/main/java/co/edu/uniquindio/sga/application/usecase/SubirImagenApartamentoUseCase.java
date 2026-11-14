package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.ApartamentoNoEncontradoException;
import co.edu.uniquindio.sga.application.exception.SolicitudAplicacionException;
import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.port.AlmacenamientoImagenesPort;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.valueobject.GaleriaImagenes;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Sube una imagen a la galería de un apartamento (7.3): la guarda en el servicio de imágenes y agrega su URL al
 * final de la galería, sin cambiar la principal. Acepta JPG, PNG y WebP de hasta 5 MB, y revisa el contenido real
 * del archivo, no solo el tipo que declara el navegador.
 */
@Service
public class SubirImagenApartamentoUseCase {

    public static final long TAMANO_MAXIMO = 5L * 1024 * 1024;
    private static final Map<String, String> EXTENSIONES = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp");

    private final ApartamentoRepository apartamentoRepository;
    private final AlmacenamientoImagenesPort almacenamiento;

    public SubirImagenApartamentoUseCase(ApartamentoRepository apartamentoRepository,
                                         AlmacenamientoImagenesPort almacenamiento) {
        this.apartamentoRepository = apartamentoRepository;
        this.almacenamiento = almacenamiento;
    }

    @Transactional
    public Apartamento ejecutar(IdentificacionApartamento identificacion, String tipoContenido, byte[] contenido) {
        String tipo = tipoContenido == null ? "" : tipoContenido.toLowerCase(Locale.ROOT);
        String extension = EXTENSIONES.get(tipo);
        if (contenido == null || contenido.length == 0) {
            throw new SolicitudAplicacionException("Selecciona una imagen.");
        }
        if (extension == null || !coincideConElTipo(tipo, contenido)) {
            throw new SolicitudAplicacionException("La imagen debe ser JPG, PNG o WebP.");
        }
        if (contenido.length > TAMANO_MAXIMO) {
            throw new SolicitudAplicacionException("La imagen supera los 5 MB.");
        }

        Apartamento apartamento = apartamentoRepository.obtenerPorIdentificacion(identificacion)
                .orElseThrow(() -> new ApartamentoNoEncontradoException(identificacion));
        GaleriaImagenes actual = apartamento.getGaleria();
        if (actual.urls().size() >= GaleriaImagenes.MAXIMO) {
            throw new ReglaDominioException("La galería ya tiene " + GaleriaImagenes.MAXIMO + " imágenes: quita una antes de subir otra.");
        }

        String nombre = identificacion.valor().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9-]", "-")
                + "-" + UUID.randomUUID() + "." + extension;
        String url = almacenamiento.guardar(nombre, tipo, contenido);

        List<String> urls = new ArrayList<>(actual.urls());
        urls.add(url);
        apartamento.reemplazarGaleria(new GaleriaImagenes(urls, actual.indicePrincipal()));
        apartamentoRepository.guardar(apartamento);
        return apartamento;
    }

    /** Firma de los primeros bytes: evita que un archivo cualquiera entre renombrado como imagen. */
    static boolean coincideConElTipo(String tipo, byte[] b) {
        return switch (tipo) {
            case "image/jpeg" -> b.length > 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF;
            case "image/png" -> b.length > 8 && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G';
            case "image/webp" -> b.length > 12 && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                    && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P';
            default -> false;
        };
    }
}

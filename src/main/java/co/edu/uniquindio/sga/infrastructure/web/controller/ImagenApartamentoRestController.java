package co.edu.uniquindio.sga.infrastructure.web.controller;

import co.edu.uniquindio.sga.application.dto.response.ApartamentoResponse;
import co.edu.uniquindio.sga.application.exception.SolicitudAplicacionException;
import co.edu.uniquindio.sga.application.usecase.SubirImagenApartamentoUseCase;
import co.edu.uniquindio.sga.infrastructure.web.mapper.RespuestaApiMapper;
import co.edu.uniquindio.sga.infrastructure.web.mapper.SolicitudApiMapper;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Sube una imagen a la galería del apartamento (7.3). Multipart con el campo {@code imagen}. Quitar imágenes o
 * cambiar la principal se hace con {@code PUT /api/apartamentos/{identificacion}}.
 */
@RestController
@RequestMapping("/api/apartamentos/{identificacion}/imagenes")
public class ImagenApartamentoRestController {

    private final SubirImagenApartamentoUseCase subir;
    private final RespuestaApiMapper respuestas;
    private final SolicitudApiMapper solicitudes;

    public ImagenApartamentoRestController(SubirImagenApartamentoUseCase subir, RespuestaApiMapper respuestas,
                                           SolicitudApiMapper solicitudes) {
        this.subir = subir;
        this.respuestas = respuestas;
        this.solicitudes = solicitudes;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ApartamentoResponse subir(@PathVariable String identificacion, @RequestParam("imagen") MultipartFile imagen) {
        try {
            return respuestas.apartamento(subir.ejecutar(solicitudes.identificacion(identificacion),
                    imagen.getContentType(), imagen.getBytes()));
        } catch (IOException e) {
            throw new SolicitudAplicacionException("No se pudo leer la imagen enviada.");
        }
    }
}

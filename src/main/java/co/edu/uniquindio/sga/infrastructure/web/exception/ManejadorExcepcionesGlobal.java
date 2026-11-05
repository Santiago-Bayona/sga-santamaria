package co.edu.uniquindio.sga.infrastructure.web.exception;

import co.edu.uniquindio.sga.application.exception.ApartamentoNoEncontradoException;
import co.edu.uniquindio.sga.application.exception.ApartamentoYaRegistradoException;
import co.edu.uniquindio.sga.application.exception.CanalExternoNoEncontradoException;
import co.edu.uniquindio.sga.application.exception.CanalExternoYaRegistradoException;
import co.edu.uniquindio.sga.application.exception.FolioNoEncontradoException;
import co.edu.uniquindio.sga.application.exception.NovedadYaRegistradaException;
import co.edu.uniquindio.sga.application.exception.ReservaNoEncontradaException;
import co.edu.uniquindio.sga.application.exception.SolicitudAplicacionException;
import co.edu.uniquindio.sga.application.exception.TitularNoEncontradoException;
import co.edu.uniquindio.sga.application.exception.TitularYaRegistradoException;
import co.edu.uniquindio.sga.application.exception.EntidadNoEncontradaException;
import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.infrastructure.exception.InfraestructuraException;
import co.edu.uniquindio.sga.infrastructure.security.UsuarioYaRegistradoException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Adaptador de excepciones de cada capa a respuestas HTTP.
 * Dominio → 409, aplicación (no encontrado) → 404, conflicto → 409, validación → 400, infra → 500.
 */
@RestControllerAdvice
public class ManejadorExcepcionesGlobal {

    private static final Logger LOG = LoggerFactory.getLogger(ManejadorExcepcionesGlobal.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validacionCuerpo(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<String> detalles = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        return respuesta(HttpStatus.BAD_REQUEST, "Solicitud inválida", "Bean Validation rechazó el cuerpo.",
                req, detalles);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> validacionParametros(ConstraintViolationException ex, HttpServletRequest req) {
        List<String> detalles = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();
        return respuesta(HttpStatus.BAD_REQUEST, "Parámetros inválidos", "Bean Validation rechazó los parámetros.",
                req, detalles);
    }

    @ExceptionHandler(SolicitudAplicacionException.class)
    public ResponseEntity<ErrorResponse> solicitud(SolicitudAplicacionException ex, HttpServletRequest req) {
        return respuesta(HttpStatus.BAD_REQUEST, "Solicitud de aplicación", ex.getMessage(), req, List.of());
    }

    @ExceptionHandler({
            ReservaNoEncontradaException.class,
            TitularNoEncontradoException.class,
            ApartamentoNoEncontradoException.class,
            CanalExternoNoEncontradoException.class,
            FolioNoEncontradoException.class,
            EntidadNoEncontradaException.class
    })
    public ResponseEntity<ErrorResponse> noEncontrado(RuntimeException ex, HttpServletRequest req) {
        return respuesta(HttpStatus.NOT_FOUND, "Recurso no encontrado", ex.getMessage(), req, List.of());
    }

    @ExceptionHandler({
            TitularYaRegistradoException.class,
            ApartamentoYaRegistradoException.class,
            CanalExternoYaRegistradoException.class,
            NovedadYaRegistradaException.class,
            UsuarioYaRegistradoException.class
    })
    public ResponseEntity<ErrorResponse> conflicto(RuntimeException ex, HttpServletRequest req) {
        return respuesta(HttpStatus.CONFLICT, "Conflicto", ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(ReglaDominioException.class)
    public ResponseEntity<ErrorResponse> dominio(ReglaDominioException ex, HttpServletRequest req) {
        return respuesta(HttpStatus.CONFLICT, "Regla de dominio", ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> prohibido(AccessDeniedException ex, HttpServletRequest req) {
        return respuesta(HttpStatus.FORBIDDEN, "Prohibido", "No tiene permiso para este recurso.", req, List.of());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> noAutenticado(AuthenticationException ex, HttpServletRequest req) {
        // Mensaje genérico en el login fallido: no revela si el usuario existe (Guía 12, 8.6)
        String mensaje = ex instanceof BadCredentialsException ? "Credenciales inválidas." : "Debe iniciar sesión (JWT).";
        return respuesta(HttpStatus.UNAUTHORIZED, "No autenticado", mensaje, req, List.of());
    }

    @ExceptionHandler(InfraestructuraException.class)
    public ResponseEntity<ErrorResponse> infraestructura(InfraestructuraException ex, HttpServletRequest req) {
        return respuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Fallo de infraestructura", ex.getMessage(), req, List.of());
    }

    /** No devuelve el mensaje de la excepción: puede filtrar detalle interno. El stack queda en el log. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> generico(Exception ex, HttpServletRequest req) {
        LOG.error("Error no controlado en {}", req.getRequestURI(), ex);
        return respuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno",
                "Ocurrió un error interno. Intente de nuevo más tarde.", req, List.of());
    }

    private static ResponseEntity<ErrorResponse> respuesta(HttpStatus status, String error, String mensaje,
                                                           HttpServletRequest req, List<String> detalles) {
        return ResponseEntity.status(status).body(new ErrorResponse(
                LocalDateTime.now(), status.value(), error, mensaje, req.getRequestURI(), detalles));
    }
}

package co.edu.uniquindio.sga.infrastructure.web.controller;

import co.edu.uniquindio.sga.application.dto.response.UsuarioResponse;
import co.edu.uniquindio.sga.application.exception.SolicitudAplicacionException;
import co.edu.uniquindio.sga.application.usecase.ActualizarPerfilUseCase;
import co.edu.uniquindio.sga.application.usecase.CambiarContrasenaUseCase;
import co.edu.uniquindio.sga.application.usecase.ConsultarUsuarioUseCase;
import co.edu.uniquindio.sga.application.usecase.SubirFotoPerfilUseCase;
import co.edu.uniquindio.sga.domain.port.CuentaUsuario;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/** Perfil de la cuenta autenticada: datos, foto y cambio voluntario de contraseña. */
@RestController
@Validated
@RequestMapping("/api/usuarios/me")
public class UsuarioRestController {

    private final ConsultarUsuarioUseCase consultar;
    private final ActualizarPerfilUseCase actualizar;
    private final CambiarContrasenaUseCase cambiarContrasena;
    private final SubirFotoPerfilUseCase subirFoto;

    public UsuarioRestController(ConsultarUsuarioUseCase consultar, ActualizarPerfilUseCase actualizar,
                                 CambiarContrasenaUseCase cambiarContrasena, SubirFotoPerfilUseCase subirFoto) {
        this.consultar = consultar;
        this.actualizar = actualizar;
        this.cambiarContrasena = cambiarContrasena;
        this.subirFoto = subirFoto;
    }

    @GetMapping
    public UsuarioResponse yo(Authentication authentication) {
        return respuesta(consultar.ejecutar(authentication.getName()));
    }

    @PutMapping
    public UsuarioResponse actualizar(@Valid @RequestBody PerfilRequest request, Authentication authentication) {
        return respuesta(actualizar.ejecutar(authentication.getName(), request.nombre(), request.telefono()));
    }

    @PostMapping("/contrasena")
    public ResponseEntity<Void> contrasena(@Valid @RequestBody ContrasenaRequest request, Authentication authentication) {
        cambiarContrasena.ejecutar(authentication.getName(), request.contrasenaActual(), request.contrasenaNueva());
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UsuarioResponse foto(@RequestParam("imagen") MultipartFile imagen, Authentication authentication) {
        try {
            return respuesta(subirFoto.ejecutar(authentication.getName(), imagen.getContentType(), imagen.getBytes()));
        } catch (IOException e) {
            throw new SolicitudAplicacionException("No se pudo leer la imagen enviada.");
        }
    }

    static UsuarioResponse respuesta(CuentaUsuario cuenta) {
        return new UsuarioResponse(cuenta.username(), cuenta.rol(), cuenta.activo(), cuenta.codigoTitular(),
                cuenta.nombreCompleto(), cuenta.telefono(), cuenta.fotoUrl());
    }

    public record PerfilRequest(
            @NotBlank(message = "El nombre es obligatorio") @Size(max = 255) String nombre,
            @NotBlank(message = "El teléfono es obligatorio")
            @Pattern(regexp = "^[0-9+\\s()-]{7,30}$", message = "El teléfono debe tener entre 7 y 30 dígitos o separadores")
            String telefono) {
    }

    public record ContrasenaRequest(
            @NotBlank(message = "La contraseña actual es obligatoria") String contrasenaActual,
            @NotBlank(message = "La contraseña nueva es obligatoria")
            @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$",
                    message = "La contraseña debe tener mínimo 8 caracteres, al menos una mayúscula y un número")
            String contrasenaNueva) {
    }
}

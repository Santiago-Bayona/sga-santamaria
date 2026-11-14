package co.edu.uniquindio.sga.infrastructure.web.controller;

import co.edu.uniquindio.sga.application.dto.response.PaginaResponse;
import co.edu.uniquindio.sga.application.dto.response.UsuarioResponse;
import co.edu.uniquindio.sga.application.exception.SolicitudAplicacionException;
import co.edu.uniquindio.sga.application.usecase.CambiarRolUsuarioUseCase;
import co.edu.uniquindio.sga.application.usecase.ConsultarUsuarioUseCase;
import co.edu.uniquindio.sga.application.usecase.DesactivarUsuarioUseCase;
import co.edu.uniquindio.sga.application.usecase.ListarUsuariosUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Gestión de cuentas (7.2). La baja es lógica: no hay DELETE, y un usuario inactivo no puede entrar. */
@RestController
@Validated
@RequestMapping("/api/admin/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUsuarioRestController {

    private final ListarUsuariosUseCase listar;
    private final ConsultarUsuarioUseCase consultar;
    private final CambiarRolUsuarioUseCase cambiarRol;
    private final DesactivarUsuarioUseCase desactivar;

    public AdminUsuarioRestController(ListarUsuariosUseCase listar, ConsultarUsuarioUseCase consultar,
                                      CambiarRolUsuarioUseCase cambiarRol, DesactivarUsuarioUseCase desactivar) {
        this.listar = listar;
        this.consultar = consultar;
        this.cambiarRol = cambiarRol;
        this.desactivar = desactivar;
    }

    @GetMapping
    public PaginaResponse<UsuarioResponse> listar(@RequestParam(required = false) String rol,
                                                  @RequestParam(required = false) Boolean activo,
                                                  @RequestParam(defaultValue = "0") int pagina) {
        return PaginaResponse.de(listar.ejecutar(rol, activo).stream().map(UsuarioRestController::respuesta).toList(), pagina);
    }

    @GetMapping("/{username}")
    public UsuarioResponse detalle(@PathVariable String username) {
        return UsuarioRestController.respuesta(consultar.ejecutar(username));
    }

    @PutMapping("/{username}/rol")
    public UsuarioResponse rol(@PathVariable String username, @Valid @RequestBody RolRequest request) {
        return UsuarioRestController.respuesta(cambiarRol.ejecutar(username, request.rol()));
    }

    @PostMapping("/{username}/desactivacion")
    public ResponseEntity<UsuarioResponse> desactivar(@PathVariable String username, Authentication authentication) {
        if (authentication.getName().equalsIgnoreCase(username)) {
            throw new SolicitudAplicacionException("No puedes desactivar tu propia cuenta.");
        }
        return ResponseEntity.ok(UsuarioRestController.respuesta(desactivar.ejecutar(username)));
    }

    public record RolRequest(@NotBlank(message = "El rol es obligatorio") String rol) {
    }
}

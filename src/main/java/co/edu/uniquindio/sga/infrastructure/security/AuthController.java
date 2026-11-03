package co.edu.uniquindio.sga.infrastructure.security;

import co.edu.uniquindio.sga.application.exception.SolicitudAplicacionException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Login en POST /api/auth/login. Devuelve el token, el usuario, el rol y el nombre.
 */

@RestController
@Validated
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioJpaRepository usuarios;
    private final PasswordHasherPort hasher;
    private final JwtService jwtService;

    public AuthController(UsuarioJpaRepository usuarios, PasswordHasherPort hasher, JwtService jwtService) {
        this.usuarios = usuarios;
        this.hasher = hasher;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        UsuarioJpaEntity usuario = usuarios.findById(request.username())
                .filter(UsuarioJpaEntity::isActivo)
                .orElseThrow(() -> new SolicitudAplicacionException("Credenciales inválidas."));
        if (!hasher.coincide(request.password(), usuario.getPasswordHash())) {
            throw new SolicitudAplicacionException("Credenciales inválidas.");
        }
        return new TokenResponse(jwtService.emitir(usuario.getUsername(), usuario.getRol()),
                usuario.getUsername(), usuario.getRol().name(), usuario.getNombreCompleto());
    }

    @GetMapping("/me")
    public ResponseEntity<TokenResponse> me(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        return usuarios.findById(authentication.getName())
                .map(u -> ResponseEntity.ok(new TokenResponse(null, u.getUsername(),
                        u.getRol().name(), u.getNombreCompleto())))
                .orElseGet(() -> ResponseEntity.status(401).build());
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {
    }

    public record TokenResponse(String token, String username, String rol, String nombre) {
    }
}

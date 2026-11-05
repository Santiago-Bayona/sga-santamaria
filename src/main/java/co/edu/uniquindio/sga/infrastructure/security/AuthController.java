package co.edu.uniquindio.sga.infrastructure.security;

import co.edu.uniquindio.sga.application.dto.request.RegistroHuespedRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * Autenticación (Guía 12): inicio de sesión que emite el JWT y registro público de huéspedes (7.2).
 * Todo /api/auth/** es público en SecurityConfig.
 */
@RestController
@Validated
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioJpaRepository usuarios;
    private final PasswordHasherPort hasher;
    private final JwtService jwtService;
    private final RegistroHuespedService registro;

    public AuthController(UsuarioJpaRepository usuarios, PasswordHasherPort hasher, JwtService jwtService,
                          RegistroHuespedService registro) {
        this.usuarios = usuarios;
        this.hasher = hasher;
        this.jwtService = jwtService;
        this.registro = registro;
    }

    /** POST /api/auth/login → 200 con token, o 401 con mensaje genérico (no revela si el usuario existe). */
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        UsuarioJpaEntity usuario = usuarios.findById(request.username().trim())
                .filter(UsuarioJpaEntity::isActivo)
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas."));
        if (!hasher.coincide(request.password(), usuario.getPasswordHash())) {
            throw new BadCredentialsException("Credenciales inválidas.");
        }
        return new TokenResponse(jwtService.emitir(usuario.getUsername(), usuario.getRol()),
                usuario.getUsername(), usuario.getRol().name(), usuario.getNombreCompleto());
    }

    /** POST /api/auth/registro → 201; 400 si el formato falla; 409 si el correo o el documento ya existen. */
    @PostMapping("/registro")
    public ResponseEntity<RegistroResponse> registrar(@Valid @RequestBody RegistroHuespedRequest request) {
        UsuarioJpaEntity usuario = registro.registrar(request);
        return ResponseEntity.created(URI.create("/api/auth/me"))
                .body(new RegistroResponse(usuario.getUsername(), usuario.getRol().name(),
                        usuario.getNombreCompleto(), usuario.getCodigoTitular()));
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

    public record RegistroResponse(String username, String rol, String nombre, String codigoTitular) {
    }
}

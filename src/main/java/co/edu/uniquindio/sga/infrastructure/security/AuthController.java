package co.edu.uniquindio.sga.infrastructure.security;

import co.edu.uniquindio.sga.application.dto.request.RegistroHuespedRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
 * Autenticación (Guía 12): inicio de sesión que emite el JWT, registro público de huéspedes (7.2) y
 * recuperación de contraseña por correo.
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
    private final RecuperacionContrasenaService recuperacion;

    public AuthController(UsuarioJpaRepository usuarios, PasswordHasherPort hasher, JwtService jwtService,
                          RegistroHuespedService registro, RecuperacionContrasenaService recuperacion) {
        this.usuarios = usuarios;
        this.hasher = hasher;
        this.jwtService = jwtService;
        this.registro = registro;
        this.recuperacion = recuperacion;
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
                usuario.getUsername(), usuario.getRol().name(), usuario.getNombreCompleto(), usuario.getCodigoTitular());
    }

    /** POST /api/auth/registro → 201; 400 si el formato falla; 409 si el correo o el documento ya existen. */
    @PostMapping("/registro")
    public ResponseEntity<RegistroResponse> registrar(@Valid @RequestBody RegistroHuespedRequest request) {
        UsuarioJpaEntity usuario = registro.registrar(request);
        return ResponseEntity.created(URI.create("/api/auth/me"))
                .body(new RegistroResponse(usuario.getUsername(), usuario.getRol().name(),
                        usuario.getNombreCompleto(), usuario.getCodigoTitular()));
    }

    /**
     * POST /api/auth/recuperar-contrasena → 202 siempre (exista o no el correo), para no revelar quién tiene cuenta.
     * Si el correo es de un usuario activo, le llega un enlace que vence y sirve una sola vez.
     */
    @PostMapping("/recuperar-contrasena")
    public ResponseEntity<MensajeResponse> recuperarContrasena(@Valid @RequestBody RecuperarContrasenaRequest request) {
        recuperacion.solicitar(request.correo());
        return ResponseEntity.accepted().body(new MensajeResponse(
                "Si el correo tiene una cuenta, te enviamos un enlace para restablecer la contraseña."));
    }

    /** POST /api/auth/restablecer-contrasena → 204; 400 si la contraseña no cumple o el enlace no sirve. */
    @PostMapping("/restablecer-contrasena")
    public ResponseEntity<Void> restablecerContrasena(@Valid @RequestBody RestablecerContrasenaRequest request) {
        recuperacion.restablecer(request.token(), request.contrasena());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<TokenResponse> me(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        return usuarios.findById(authentication.getName())
                .map(u -> ResponseEntity.ok(new TokenResponse(null, u.getUsername(),
                        u.getRol().name(), u.getNombreCompleto(), u.getCodigoTitular())))
                .orElseGet(() -> ResponseEntity.status(401).build());
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {
    }

    /** codigoTitular solo viene para huéspedes: el frontend lo usa para pedir "Mis reservas" y su folio (7.1). */
    public record TokenResponse(String token, String username, String rol, String nombre, String codigoTitular) {
    }

    public record RecuperarContrasenaRequest(
            @NotBlank(message = "El correo es obligatorio")
            @Email(message = "El correo no tiene un formato válido")
            @Size(max = 80, message = "El correo admite máximo 80 caracteres")
            String correo) {
    }

    public record RestablecerContrasenaRequest(
            @NotBlank(message = "El enlace no trae el token") @Size(max = 100) String token,
            @NotBlank(message = "La contraseña es obligatoria")
            @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$",
                    message = "La contraseña debe tener mínimo 8 caracteres, al menos una mayúscula y un número")
            String contrasena) {
    }

    public record MensajeResponse(String mensaje) {
    }

    public record RegistroResponse(String username, String rol, String nombre, String codigoTitular) {
    }
}

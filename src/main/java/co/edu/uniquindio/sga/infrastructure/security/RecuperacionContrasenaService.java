package co.edu.uniquindio.sga.infrastructure.security;

import co.edu.uniquindio.sga.infrastructure.notification.CorreoNotificacionPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;

/**
 * Recuperación de contraseña por correo (Guía 12). Dos pasos:
 * <ol>
 *   <li>{@link #solicitar}: si el correo es de un usuario activo, genera un token aleatorio de 256 bits,
 *       guarda solo su hash y envía el enlace {@code {frontend}/restablecer?token=...}.</li>
 *   <li>{@link #restablecer}: valida el token (existe, sin usar, sin vencer) y cambia la contraseña.</li>
 * </ol>
 * La respuesta del paso 1 es siempre la misma, exista o no el correo: no revela quién tiene cuenta.
 * El usuario de un huésped es su correo; el personal interno sin correo lo restablece el administrador.
 */
@Service
public class RecuperacionContrasenaService {

    private static final Logger LOG = LoggerFactory.getLogger(RecuperacionContrasenaService.class);
    private static final SecureRandom ALEATORIO = new SecureRandom();
    /** Evita que alguien inunde un buzón: un enlace nuevo por usuario cada minuto como máximo. */
    private static final long ESPERA_ENTRE_SOLICITUDES_SEGUNDOS = 60;

    private final UsuarioJpaRepository usuarios;
    private final TokenRecuperacionJpaRepository tokens;
    private final PasswordHasherPort hasher;
    private final CorreoNotificacionPort correo;
    private final Clock reloj;
    private final String urlFrontend;
    private final int vigenciaMinutos;

    public RecuperacionContrasenaService(UsuarioJpaRepository usuarios, TokenRecuperacionJpaRepository tokens,
                                         PasswordHasherPort hasher, CorreoNotificacionPort correo, Clock reloj,
                                         @Value("${sga.recuperacion.url-frontend}") String urlFrontend,
                                         @Value("${sga.recuperacion.vigencia-minutos:30}") int vigenciaMinutos) {
        this.usuarios = usuarios;
        this.tokens = tokens;
        this.hasher = hasher;
        this.correo = correo;
        this.reloj = reloj;
        this.urlFrontend = urlFrontend.endsWith("/") ? urlFrontend.substring(0, urlFrontend.length() - 1) : urlFrontend;
        this.vigenciaMinutos = vigenciaMinutos;
    }

    @Transactional
    public void solicitar(String correoIngresado) {
        String username = correoIngresado.trim().toLowerCase(Locale.ROOT);
        UsuarioJpaEntity usuario = usuarios.findById(username)
                .filter(UsuarioJpaEntity::isActivo)
                .filter(u -> u.getUsername().contains("@"))
                .orElse(null);
        if (usuario == null) {
            LOG.info("Recuperación pedida para un correo sin cuenta activa");
            return;
        }
        LocalDateTime ahora = LocalDateTime.now(reloj);
        var vigentes = tokens.findByUsernameAndUsadoFalse(username);
        boolean reciente = vigentes.stream()
                .anyMatch(t -> t.getCreadoEn().isAfter(ahora.minusSeconds(ESPERA_ENTRE_SOLICITUDES_SEGUNDOS)));
        if (reciente) {
            LOG.info("Recuperación ignorada para {}: ya se envió un enlace hace menos de un minuto", username);
            return;
        }
        // Solo el último enlace sirve
        vigentes.forEach(t -> t.setUsado(true));

        String token = nuevoToken();
        TokenRecuperacionJpaEntity fila = new TokenRecuperacionJpaEntity();
        fila.setTokenHash(hash(token));
        fila.setUsername(username);
        fila.setCreadoEn(ahora);
        fila.setExpiraEn(ahora.plusMinutes(vigenciaMinutos));
        fila.setUsado(false);
        tokens.save(fila);

        String enlace = urlFrontend + "/restablecer?token=" + token;
        try {
            correo.enviarHtml(username, "Restablece tu contraseña · Santamaría",
                    texto(usuario.getNombreCompleto(), enlace), html(usuario.getNombreCompleto(), enlace));
        } catch (RuntimeException e) {
            // No se le cuenta al cliente (no revela que la cuenta existe); queda en el log para revisarlo.
            LOG.error("No se pudo enviar el correo de recuperación a {}: {}", username, e.getMessage());
        }
    }

    @Transactional
    public void restablecer(String token, String nuevaContrasena) {
        TokenRecuperacionJpaEntity fila = tokens.findById(hash(token.trim()))
                .filter(t -> !t.isUsado())
                .filter(t -> t.getExpiraEn().isAfter(LocalDateTime.now(reloj)))
                .orElseThrow(TokenRecuperacionInvalidoException::new);
        UsuarioJpaEntity usuario = usuarios.findById(fila.getUsername())
                .filter(UsuarioJpaEntity::isActivo)
                .orElseThrow(TokenRecuperacionInvalidoException::new);
        usuario.setPasswordHash(hasher.cifrar(nuevaContrasena));
        usuarios.save(usuario);
        fila.setUsado(true);
        tokens.save(fila);
        LOG.info("Contraseña restablecida para {}", usuario.getUsername());
    }

    private static String nuevoToken() {
        byte[] bytes = new byte[32];
        ALEATORIO.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    static String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }

    private String texto(String nombre, String enlace) {
        return "Hola, " + nombre + ".\n\n"
                + "Recibimos una solicitud para restablecer tu contraseña en Santamaría. Abre este enlace para "
                + "elegir una nueva (vence en " + vigenciaMinutos + " minutos y sirve una sola vez):\n\n"
                + enlace + "\n\n"
                + "Si no fuiste tú, ignora este correo: tu contraseña sigue igual.";
    }

    private String html(String nombre, String enlace) {
        String seguro = escapar(nombre);
        String url = escapar(enlace);
        return """
                <div style="font-family:Poppins,Arial,sans-serif;max-width:520px;margin:0 auto;padding:24px;color:#1F2A24">
                  <h2 style="color:#1E8449;margin:0 0 12px">Santamaría</h2>
                  <p>Hola, %s.</p>
                  <p>Recibimos una solicitud para restablecer tu contraseña. Toca el botón para elegir una nueva.</p>
                  <p style="margin:28px 0">
                    <a href="%s" style="background:#1E8449;color:#fff;text-decoration:none;padding:12px 22px;border-radius:999px;font-weight:600">Restablecer contraseña</a>
                  </p>
                  <p style="font-size:13px;color:#5B6B61">El enlace vence en %d minutos y sirve una sola vez.
                  Si no fuiste tú, ignora este correo: tu contraseña sigue igual.</p>
                  <p style="font-size:12px;color:#8A988F;word-break:break-all">Si el botón no funciona, copia este enlace: %s</p>
                </div>
                """.formatted(seguro, url, vigenciaMinutos, url);
    }

    private static String escapar(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}

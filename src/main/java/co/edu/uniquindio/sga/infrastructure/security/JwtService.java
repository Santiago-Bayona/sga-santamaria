package co.edu.uniquindio.sga.infrastructure.security;

import co.edu.uniquindio.sga.infrastructure.exception.InfraestructuraException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

/**
 * JWT HMAC-SHA256 propio de infraestructura (sin librería extra, compatible con Jackson 3).
 */
@Component
public class JwtService {

    private final byte[] secreto;
    private final long expiracionSegundos;

    public JwtService(@Value("${sga.seguridad.jwt-secret}") String secreto,
                      @Value("${sga.seguridad.jwt-expiracion-minutos}") long minutos) {
        this.secreto = secreto.getBytes(StandardCharsets.UTF_8);
        this.expiracionSegundos = minutos * 60;
    }

    public String emitir(String username, RolUsuario rol) {
        long exp = Instant.now().getEpochSecond() + expiracionSegundos;
        String header = base64("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        String payload = base64("{\"sub\":\"" + escapar(username) + "\",\"rol\":\"" + rol.name()
                + "\",\"exp\":" + exp + "}");
        String cuerpo = header + "." + payload;
        return cuerpo + "." + firmar(cuerpo);
    }

    public Optional<JwtPrincipal> validar(String token) {
        try {
            String[] partes = token.split("\\.");
            if (partes.length != 3 || !firmar(partes[0] + "." + partes[1]).equals(partes[2])) {
                return Optional.empty();
            }
            String json = new String(Base64.getUrlDecoder().decode(partes[1]), StandardCharsets.UTF_8);
            String sub = extraer(json, "sub");
            String rol = extraer(json, "rol");
            long exp = Long.parseLong(extraer(json, "exp"));
            if (Instant.now().getEpochSecond() > exp || sub == null || rol == null) {
                return Optional.empty();
            }
            return Optional.of(new JwtPrincipal(sub, RolUsuario.valueOf(rol)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private String firmar(String cuerpo) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secreto, "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(cuerpo.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new InfraestructuraException("No se pudo firmar el JWT.", e);
        }
    }

    private static String base64(String texto) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(texto.getBytes(StandardCharsets.UTF_8));
    }

    private static String extraer(String json, String clave) {
        String marca = "\"" + clave + "\":";
        int i = json.indexOf(marca);
        if (i < 0) {
            return null;
        }
        int inicio = i + marca.length();
        if (json.charAt(inicio) == '"') {
            int fin = json.indexOf('"', inicio + 1);
            return json.substring(inicio + 1, fin);
        }
        int fin = inicio;
        while (fin < json.length() && Character.isDigit(json.charAt(fin))) {
            fin++;
        }
        return json.substring(inicio, fin);
    }

    private static String escapar(String valor) {
        return valor.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public record JwtPrincipal(String username, RolUsuario rol) {
    }
}

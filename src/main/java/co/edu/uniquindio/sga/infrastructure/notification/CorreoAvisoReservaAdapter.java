package co.edu.uniquindio.sga.infrastructure.notification;

import co.edu.uniquindio.sga.domain.port.NotificacionReservaPort;
import co.edu.uniquindio.sga.domain.valueobject.AvisoReserva;
import co.edu.uniquindio.sga.domain.valueobject.Dinero;
import co.edu.uniquindio.sga.infrastructure.integration.SmsNotificacionPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Avisa al titular por correo (Brevo o simulado, según sga.correo.proveedor) y, si tiene teléfono, por SMS.
 * Envía después del commit de la reserva: si la operación se deshace, no sale nada. Un fallo del envío queda en
 * el log y no afecta la reserva (contrato de {@link NotificacionReservaPort}).
 */
@Component
public class CorreoAvisoReservaAdapter implements NotificacionReservaPort {

    private static final Logger LOG = LoggerFactory.getLogger(CorreoAvisoReservaAdapter.class);
    private static final Locale ES = Locale.of("es", "CO");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy", ES);
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("d 'de' MMMM 'a las' HH:mm", ES);

    private final CorreoNotificacionPort correo;
    private final SmsNotificacionPort sms;
    private final String urlFrontend;

    public CorreoAvisoReservaAdapter(CorreoNotificacionPort correo, SmsNotificacionPort sms,
                                     @Value("${sga.recuperacion.url-frontend:http://localhost:5173}") String urlFrontend) {
        this.correo = correo;
        this.sms = sms;
        this.urlFrontend = urlFrontend.endsWith("/") ? urlFrontend.substring(0, urlFrontend.length() - 1) : urlFrontend;
    }

    @Override
    public void notificar(AvisoReserva aviso) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    enviar(aviso);
                }
            });
        } else {
            enviar(aviso);
        }
    }

    private void enviar(AvisoReserva aviso) {
        String asunto = asunto(aviso);
        if (aviso.correo() != null && !aviso.correo().isBlank()) {
            try {
                correo.enviarHtml(aviso.correo(), asunto, texto(aviso), html(aviso));
                LOG.info("Aviso {} de la reserva {} enviado al titular", aviso.tipo(), aviso.reserva().valor());
            } catch (RuntimeException e) {
                LOG.error("No se pudo enviar el aviso {} de la reserva {}: {}", aviso.tipo(), aviso.reserva().valor(), e.getMessage());
            }
        }
        if (aviso.telefono() != null && !aviso.telefono().isBlank()) {
            try {
                sms.enviar(aviso.telefono(), asunto + ". Código " + aviso.reserva().valor() + ".");
            } catch (RuntimeException e) {
                LOG.error("No se pudo enviar el SMS de la reserva {}: {}", aviso.reserva().valor(), e.getMessage());
            }
        }
    }

    // ---- Redacción

    static String asunto(AvisoReserva aviso) {
        return switch (aviso.tipo()) {
            case CREADA -> "Recibimos tu reserva " + aviso.reserva().valor() + " · Santamaría";
            case CONFIRMADA -> "Tu reserva " + aviso.reserva().valor() + " está confirmada · Santamaría";
            case CANCELADA -> "Tu reserva " + aviso.reserva().valor() + " fue cancelada · Santamaría";
        };
    }

    /** Párrafos del cuerpo, iguales en texto plano y en HTML. */
    private String[] parrafos(AvisoReserva aviso) {
        String estancia = aviso.apartamento() + ", del " + FECHA.format(aviso.estancia().entrada())
                + " al " + FECHA.format(aviso.estancia().salida()) + " (" + noches(aviso) + ").";
        return switch (aviso.tipo()) {
            case CREADA -> new String[]{
                    "Recibimos tu reserva " + aviso.reserva().valor() + ": " + estancia,
                    "Valor total: " + pesos(aviso.valorTotal()) + ".",
                    aviso.anticipo() == null
                            ? "Te avisaremos cuando quede confirmada."
                            : "Para confirmarla, paga el anticipo de " + pesos(aviso.anticipo()) + " antes del "
                            + fechaHora(aviso.limiteConfirmacion()) + ". Si no se paga a tiempo, la reserva se vence sola."
            };
            case CONFIRMADA -> new String[]{
                    "¡Listo! Tu reserva " + aviso.reserva().valor() + " quedó confirmada: " + estancia,
                    "Valor total: " + pesos(aviso.valorTotal()) + ". Te esperamos."
            };
            case CANCELADA -> new String[]{
                    "Tu reserva " + aviso.reserva().valor() + " fue cancelada: " + estancia,
                    aviso.retencion() == null || aviso.retencion().esCero()
                            ? "Según la política de cancelación no se retiene ningún valor del anticipo."
                            : "Según la política de cancelación se retienen " + pesos(aviso.retencion()) + " del anticipo."
            };
        };
    }

    String texto(AvisoReserva aviso) {
        StringBuilder texto = new StringBuilder("Hola, ").append(aviso.nombreTitular()).append(".\n\n");
        for (String p : parrafos(aviso)) {
            texto.append(p).append("\n\n");
        }
        return texto.append("Puedes ver tus reservas en ").append(urlFrontend).append("/huesped\n\nSantamaría · Montenegro, Quindío").toString();
    }

    String html(AvisoReserva aviso) {
        StringBuilder cuerpo = new StringBuilder();
        for (String p : parrafos(aviso)) {
            cuerpo.append("<p>").append(escapar(p)).append("</p>");
        }
        String color = switch (aviso.tipo()) {
            case CREADA -> "#B9770E";
            case CONFIRMADA -> "#1E8449";
            case CANCELADA -> "#C0392B";
        };
        String etiqueta = switch (aviso.tipo()) {
            case CREADA -> "Pendiente de confirmación";
            case CONFIRMADA -> "Confirmada";
            case CANCELADA -> "Cancelada";
        };
        return """
                <div style="font-family:Poppins,Arial,sans-serif;max-width:520px;margin:0 auto;padding:24px;color:#1F2A24">
                  <h2 style="color:#1E8449;margin:0 0 12px">Santamaría</h2>
                  <p style="display:inline-block;margin:0 0 16px;padding:4px 12px;border-radius:999px;background:%s;color:#fff;font-size:13px;font-weight:600">%s</p>
                  <p>Hola, %s.</p>
                  %s
                  <p style="margin:28px 0">
                    <a href="%s/huesped" style="background:#1E8449;color:#fff;text-decoration:none;padding:12px 22px;border-radius:999px;font-weight:600">Ver mis reservas</a>
                  </p>
                  <p style="font-size:12px;color:#8A988F">Santamaría · Montenegro, Quindío</p>
                </div>
                """.formatted(color, etiqueta, escapar(aviso.nombreTitular()), cuerpo, escapar(urlFrontend));
    }

    private static String noches(AvisoReserva aviso) {
        int n = aviso.estancia().numeroNoches();
        return n + (n == 1 ? " noche" : " noches");
    }

    static String pesos(Dinero dinero) {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols(ES);
        simbolos.setGroupingSeparator('.');
        BigDecimal valor = dinero == null ? BigDecimal.ZERO : dinero.valor();
        return "$" + new DecimalFormat("#,##0", simbolos).format(valor);
    }

    private static String fechaHora(LocalDateTime momento) {
        return momento == null ? "" : FECHA_HORA.format(momento);
    }

    private static String escapar(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}

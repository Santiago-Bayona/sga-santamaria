package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.entity.Alojamiento;
import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.entity.Folio;
import co.edu.uniquindio.sga.domain.entity.Temporada;
import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.valueobject.Dinero;
import co.edu.uniquindio.sga.domain.valueobject.Estancia;
import co.edu.uniquindio.sga.domain.valueobject.IdTemporada;
import co.edu.uniquindio.sga.domain.valueobject.Mascotas;
import co.edu.uniquindio.sga.domain.valueobject.Ocupante;
import co.edu.uniquindio.sga.domain.valueobject.ServicioAdicional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Reglas compartidas por creación y modificación de reservas, y por la liquidación de la política. Uso interno del paquete. */
final class ValidacionesReserva {

    private ValidacionesReserva() {
    }

    /** 3.2: el grupo es siempre titular + acompañantes (mismo criterio que Reserva). */
    static List<Ocupante> componerGrupo(Ocupante titular, List<Ocupante> acompanantes) {
        if (titular == null) {
            throw new ReglaDominioException("El titular es obligatorio.");
        }
        if (acompanantes == null || acompanantes.stream().anyMatch(o -> o == null)) {
            throw new ReglaDominioException("La lista de acompañantes no puede ser nula ni contener ocupantes nulos.");
        }
        List<Ocupante> grupo = new ArrayList<>(acompanantes.size() + 1);
        grupo.add(titular);
        grupo.addAll(acompanantes);
        return List.copyOf(grupo);
    }

    /**
     * 7.5, condiciones 2, 3 y 4 en el orden del enunciado. La 1 la garantiza Estancia;
     * la 5 y la 6 las evalúa DisponibilidadApartamentoService.
     */
    static void validarCondicionesDeVenta(Apartamento apartamento, Alojamiento alojamiento, Estancia estancia,
                                          int totalOcupantes, LocalDate hoy) {
        // 2. RN-04
        if (estancia.iniciaAntesDe(hoy)) {
            throw new ReglaDominioException("La fecha de entrada no puede ser anterior a hoy.");
        }
        // 3. Apartamento activo y con tarifas completas (7.4)
        apartamento.verificarQueAceptaReservas();
        if (!apartamento.tieneTarifasCompletas(idsTemporadas(alojamiento))) {
            throw new ReglaDominioException("El apartamento no tiene tarifa en todas las temporadas.");
        }
        // 4. RN-02
        if (!apartamento.admite(totalOcupantes)) {
            throw new ReglaDominioException("El total de ocupantes excede la capacidad del apartamento.");
        }
    }

    /**
     * L-16: el cargo por mascotas sale del servicio adicional configurado en el alojamiento (7.3). Si el
     * alojamiento no lo ofrece, no se pueden registrar mascotas.
     */
    static ServicioAdicional servicioMascotas(Alojamiento alojamiento) {
        return alojamiento.getServiciosAdicionales().stream()
                .filter(s -> s.nombre().equalsIgnoreCase(Mascotas.SERVICIO_ADICIONAL))
                .findFirst()
                .orElseThrow(() -> new ReglaDominioException("El alojamiento no tiene configurado el servicio adicional \""
                        + Mascotas.SERVICIO_ADICIONAL + "\"; no se pueden registrar mascotas."));
    }

    static List<IdTemporada> idsTemporadas(Alojamiento alojamiento) {
        return alojamiento.getTemporadas().stream().map(Temporada::getId).toList();
    }

    /**
     * Base sobre la que la política calcula la retención: el anticipo exigido, sin superar lo efectivamente
     * pagado. Así no se cobra una penalidad sobre un anticipo que el huésped nunca abonó.
     */
    static Dinero baseRetencion(Dinero anticipoExigido, Folio folio) {
        Dinero pagado = folio.totalPagos();
        return anticipoExigido.esMayorQue(pagado) ? pagado : anticipoExigido;
    }
}

package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.entity.Alojamiento;
import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.entity.Temporada;
import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.repository.AlojamientoRepository;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.valueobject.Cotizacion;
import co.edu.uniquindio.sga.domain.valueobject.DesgloseNoche;
import co.edu.uniquindio.sga.domain.valueobject.Estancia;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.Noche;
import co.edu.uniquindio.sga.domain.valueobject.Ocupante;
import co.edu.uniquindio.sga.domain.valueobject.Tarifa;

import java.util.ArrayList;
import java.util.List;

/**
 * RN-05 y RN-06: valor de la estancia noche por noche. La temporada de cada noche la decide el Alojamiento
 * y la tarifa de esa temporada la tiene el Apartamento; por eso ninguno de los dos puede cotizar solo.
 * Devuelve la Cotizacion que la Reserva congela (RN-22).
 */
public class CotizadorEstanciaService {

    private final ApartamentoRepository apartamentoRepository;
    private final AlojamientoRepository alojamientoRepository;

    public CotizadorEstanciaService(ApartamentoRepository apartamentoRepository,
                                    AlojamientoRepository alojamientoRepository) {
        this.apartamentoRepository = apartamentoRepository;
        this.alojamientoRepository = alojamientoRepository;
    }

    /** 7.4: cotizar antes de reservar. @param grupo titular + acompañantes. */
    public Cotizacion cotizar(IdentificacionApartamento apartamento, Estancia estancia, List<Ocupante> grupo) {
        Apartamento encontrado = apartamentoRepository.obtenerPorIdentificacion(apartamento)
                .orElseThrow(() -> new ReglaDominioException("No existe el apartamento " + apartamento.valor()));
        return cotizar(encontrado, alojamientoRepository.obtener(), estancia, grupo);
    }

    /** Versión sin repositorios: la usan creación y modificación con los agregados ya cargados. */
    public Cotizacion cotizar(Apartamento apartamento, Alojamiento alojamiento, Estancia estancia, List<Ocupante> grupo) {
        if (apartamento == null || alojamiento == null || estancia == null) {
            throw new ReglaDominioException("La cotización requiere apartamento, alojamiento y estancia.");
        }
        if (grupo == null || grupo.isEmpty()) {
            throw new ReglaDominioException("La cotización requiere la composición del grupo.");
        }
        // RN-06 / 3.2: la condición de facturable se fija a la fecha de entrada y no cambia durante la estancia
        int facturables = (int) grupo.stream()
                .filter(ocupante -> alojamiento.esFacturable(ocupante, estancia.entrada()))
                .count();
        if (facturables == 0) {
            throw new ReglaDominioException("El grupo debe tener al menos un ocupante facturable.");
        }

        List<DesgloseNoche> desglose = new ArrayList<>();
        for (Noche noche : estancia.noches()) {
            Temporada temporada = alojamiento.obtenerTemporada(noche.fecha());
            Tarifa tarifa = apartamento.tarifaEn(temporada.getId())
                    .orElseThrow(() -> new ReglaDominioException("El apartamento no tiene tarifa para la temporada "
                            + temporada.getNombre() + " (noche del " + noche.fecha() + ")."));
            desglose.add(DesgloseNoche.calcular(noche, tarifa, facturables));
        }
        return new Cotizacion(desglose);
    }
}

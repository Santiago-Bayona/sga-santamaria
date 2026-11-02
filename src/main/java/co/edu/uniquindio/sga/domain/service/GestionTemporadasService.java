package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.entity.Alojamiento;
import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.entity.Temporada;
import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.repository.AlojamientoRepository;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.valueobject.Dinero;
import co.edu.uniquindio.sga.domain.valueobject.IdTemporada;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.Tarifa;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 7.4: todo apartamento activo debe tener tarifa en todas las temporadas, y no se activa un apartamento sin
 * tarifas completas. Las temporadas viven en el Alojamiento y las tarifas en cada Apartamento: agregar una
 * temporada sin tarifas rompería la invariante en otros agregados.
 */
public class GestionTemporadasService {

    private final AlojamientoRepository alojamientoRepository;
    private final ApartamentoRepository apartamentoRepository;

    public GestionTemporadasService(AlojamientoRepository alojamientoRepository,
                                    ApartamentoRepository apartamentoRepository) {
        this.alojamientoRepository = alojamientoRepository;
        this.apartamentoRepository = apartamentoRepository;
    }

    /** @param tarifas valor por ocupante facturable por noche de cada apartamento en la nueva temporada */
    public void agregarTemporada(Temporada nueva, Map<IdentificacionApartamento, Dinero> tarifas,
                                 LocalDateTime ahora) {
        if (nueva == null || tarifas == null) {
            throw new ReglaDominioException("La temporada y sus tarifas son obligatorias.");
        }
        Alojamiento alojamiento = alojamientoRepository.obtener();
        List<String> sinTarifa = apartamentoRepository.buscarActivos().stream()
                .map(Apartamento::getIdentificacion)
                .filter(id -> !tarifas.containsKey(id))
                .map(IdentificacionApartamento::valor)
                .toList();
        if (!sinTarifa.isEmpty()) {
            throw new ReglaDominioException("Faltan tarifas de la nueva temporada para los apartamentos activos: " + sinTarifa);
        }
        alojamiento.agregarTemporada(nueva); // valida base única, solapamientos e ids

        List<Apartamento> actualizados = new ArrayList<>();
        tarifas.forEach((id, valor) -> {
            Apartamento apartamento = apartamento(id);
            apartamento.definirTarifa(new Tarifa(nueva.getId(), valor), ahora);
            actualizados.add(apartamento);
        });

        alojamientoRepository.guardar(alojamiento);
        actualizados.forEach(apartamentoRepository::guardar);
    }

    /** Las tarifas de esa temporada quedan en el histórico de cada apartamento. */
    public void retirarTemporada(IdTemporada id) {
        Alojamiento alojamiento = alojamientoRepository.obtener();
        alojamiento.retirarTemporada(id);
        alojamientoRepository.guardar(alojamiento);
    }

    /** 7.4: definir o cambiar una tarifa; la temporada debe existir en el alojamiento. */
    public void definirTarifa(IdentificacionApartamento idApartamento, IdTemporada temporada, Dinero valor,
                              LocalDateTime ahora) {
        boolean existe = ValidacionesReserva.idsTemporadas(alojamientoRepository.obtener()).contains(temporada);
        if (!existe) {
            throw new ReglaDominioException("No existe la temporada " + temporada.valor() + " en el alojamiento.");
        }
        Apartamento apartamento = apartamento(idApartamento);
        apartamento.definirTarifa(new Tarifa(temporada, valor), ahora);
        apartamentoRepository.guardar(apartamento);
    }

    /** 7.4: el sistema impide activar un apartamento sin tarifa en todas las temporadas. */
    public void activarApartamento(IdentificacionApartamento idApartamento) {
        Apartamento apartamento = apartamento(idApartamento);
        apartamento.activar(ValidacionesReserva.idsTemporadas(alojamientoRepository.obtener()));
        apartamentoRepository.guardar(apartamento);
    }

    private Apartamento apartamento(IdentificacionApartamento id) {
        return apartamentoRepository.obtenerPorIdentificacion(id)
                .orElseThrow(() -> new ReglaDominioException("No existe el apartamento " + id.valor()));
    }
}

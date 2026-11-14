package co.edu.uniquindio.sga.infrastructure.persistence.adapter;

import co.edu.uniquindio.sga.domain.entity.EventoBitacoraCanal;
import co.edu.uniquindio.sga.domain.repository.EventoBitacoraCanalRepository;
import co.edu.uniquindio.sga.domain.valueobject.Pagina;
import co.edu.uniquindio.sga.domain.valueobject.ResultadoMensajeCanal;
import co.edu.uniquindio.sga.infrastructure.persistence.entity.EventoBitacoraCanalJpaEntity;
import co.edu.uniquindio.sga.infrastructure.persistence.jpa.EventoBitacoraCanalJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class EventoBitacoraCanalRepositoryAdapter implements EventoBitacoraCanalRepository {

    private final EventoBitacoraCanalJpaRepository jpa;

    public EventoBitacoraCanalRepositoryAdapter(EventoBitacoraCanalJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    @Transactional
    public void guardar(EventoBitacoraCanal evento) {
        EventoBitacoraCanalJpaEntity entidad = new EventoBitacoraCanalJpaEntity();
        entidad.setCodigo(evento.getCodigo());
        entidad.setCodigoCanal(evento.getCodigoCanal());
        entidad.setIdentificadorExterno(evento.getIdentificadorExterno());
        entidad.setOcurridoEn(evento.getOcurridoEn());
        entidad.setPayload(evento.getPayload());
        entidad.setResultado(evento.getResultado().name());
        entidad.setMotivo(evento.getMotivo());
        jpa.save(entidad);
    }

    @Override
    @Transactional(readOnly = true)
    public Pagina<EventoBitacoraCanal> listar(int pagina) {
        var resultado = jpa.findAllByOrderByOcurridoEnDesc(PageRequest.of(Math.max(pagina, 0), Pagina.TAMANO));
        return new Pagina<>(resultado.getContent().stream().map(this::aDominio).toList(),
                resultado.getNumber(), resultado.getTotalElements());
    }

    private EventoBitacoraCanal aDominio(EventoBitacoraCanalJpaEntity entidad) {
        return new EventoBitacoraCanal(
                entidad.getCodigo(),
                entidad.getCodigoCanal(),
                entidad.getIdentificadorExterno(),
                entidad.getOcurridoEn(),
                entidad.getPayload(),
                ResultadoMensajeCanal.valueOf(entidad.getResultado()),
                entidad.getMotivo());
    }
}

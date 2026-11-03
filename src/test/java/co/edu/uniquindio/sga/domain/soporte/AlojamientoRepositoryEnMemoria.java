package co.edu.uniquindio.sga.domain.soporte;

import co.edu.uniquindio.sga.domain.entity.Alojamiento;
import co.edu.uniquindio.sga.domain.repository.AlojamientoRepository;

public class AlojamientoRepositoryEnMemoria implements AlojamientoRepository {

    private Alojamiento alojamiento;

    public AlojamientoRepositoryEnMemoria(Alojamiento alojamiento) {
        this.alojamiento = alojamiento;
    }

    @Override
    public Alojamiento obtener() {
        return alojamiento;
    }

    @Override
    public void guardar(Alojamiento alojamiento) {
        this.alojamiento = alojamiento;
    }
}

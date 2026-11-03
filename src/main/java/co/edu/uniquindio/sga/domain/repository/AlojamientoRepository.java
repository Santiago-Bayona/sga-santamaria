package co.edu.uniquindio.sga.domain.repository;

import co.edu.uniquindio.sga.domain.entity.Alojamiento;

/** El sistema administra un único alojamiento (F-13); su configuración y temporadas viven dentro de él. */
public interface AlojamientoRepository {

    Alojamiento obtener();

    void guardar(Alojamiento alojamiento);
}

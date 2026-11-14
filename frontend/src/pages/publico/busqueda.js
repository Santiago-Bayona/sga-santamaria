import { hoyIso, sumarDias } from '../../utils/formato';

/** Lee la búsqueda (fechas y grupo) de los parámetros de la URL. */
export function leerBusqueda(parametros) {
  return {
    entrada: parametros.get('entrada') || sumarDias(hoyIso(), 7),
    salida: parametros.get('salida') || sumarDias(hoyIso(), 9),
    grupo: { adultos: Math.max(1, Number(parametros.get('adultos') || 2)), ninos: parametros.getAll('nino') },
  };
}

/** Parámetros de búsqueda como texto, para llevarlos de una pantalla a otra. */
export function parametrosBusqueda({ entrada, salida, grupo }) {
  const p = new URLSearchParams({ entrada, salida, adultos: grupo.adultos });
  grupo.ninos.filter(Boolean).forEach((n) => p.append('nino', n));
  return p.toString();
}

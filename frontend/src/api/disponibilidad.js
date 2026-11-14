import { api } from './cliente';

/** Búsqueda pública (7.5): fechas + fechas de nacimiento del grupo, con filtros opcionales. */
export const consultarDisponibilidad = ({ fechaEntrada, fechaSalida, fechasNacimiento, capacidadMinima, precioMinimo, precioMaximo }, pagina = 0) =>
  api.get('/api/disponibilidad', { fechaEntrada, fechaSalida, fechasNacimiento, capacidadMinima, precioMinimo, precioMaximo, pagina });

/** Cotización con desglose noche por noche (7.4). Es pública. */
export const cotizarEstancia = (datos) => api.post('/api/cotizaciones', datos);

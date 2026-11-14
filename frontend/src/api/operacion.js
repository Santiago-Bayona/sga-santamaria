import { api } from './cliente';

/** Resumen diario de llegadas y salidas; la IA lo redacta si está habilitada (10.2). */
export const obtenerResumenDiario = (fecha) => api.get('/api/operacion/resumen-diario', { fecha });

export const consultarCalendario = (identificacion, fechaDesde, fechaHasta) =>
  api.get('/api/calendario', { identificacion, fechaDesde, fechaHasta });

export const listarBloqueos = (identificacion, pagina = 0) => api.get('/api/bloqueos', { identificacion, pagina });
export const registrarBloqueo = (datos) => api.post('/api/bloqueos', datos);
export const reprogramarBloqueo = (codigo, nuevaFechaInicio, nuevaFechaFin) =>
  api.post(`/api/bloqueos/${encodeURIComponent(codigo)}/reprogramacion`, { nuevaFechaInicio, nuevaFechaFin });
export const levantarBloqueo = (codigo) => api.post(`/api/bloqueos/${encodeURIComponent(codigo)}/levantamiento`);

export const listarNovedades = (identificacion, pagina = 0) => api.get('/api/novedades', { identificacion, pagina });
export const registrarNovedad = (datos, autor) => api.post('/api/novedades', datos, { encabezados: { 'X-Autor': autor } });

export const reporteOcupacion = (fechaDesde, fechaHasta) => api.get('/api/reportes/ocupacion', { fechaDesde, fechaHasta });
export const reporteIngresos = (fechaDesde, fechaHasta) => api.get('/api/reportes/ingresos', { fechaDesde, fechaHasta });
export const reporteCanales = (fechaDesde, fechaHasta) => api.get('/api/reportes/canales', { fechaDesde, fechaHasta });

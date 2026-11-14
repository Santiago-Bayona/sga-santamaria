import { api } from './cliente';

const ruta = (codigo) => `/api/reservas/${encodeURIComponent(codigo)}`;

/** Listado paginado (10 por página) de la más reciente a la más antigua, con filtros opcionales. */
export const listarReservas = (filtros = {}, pagina = 0) => api.get('/api/reservas', { ...filtros, pagina });
export const consultarReserva = (codigo) => api.get(ruta(codigo));
export const crearReserva = (datos) => api.post('/api/reservas', datos);

export const confirmarReserva = (codigo) => api.post(`${ruta(codigo)}/confirmacion`);
export const cancelarReserva = (codigo, motivo) => api.post(`${ruta(codigo)}/cancelacion`, { motivo });
export const registrarLlegada = (codigo) => api.post(`${ruta(codigo)}/llegada`);
export const declararNoShow = (codigo) => api.post(`${ruta(codigo)}/no-show`);
export const registrarSalida = (codigo, motivoAutorizacion, autor) =>
  api.post(`${ruta(codigo)}/salida`, { motivoAutorizacion: motivoAutorizacion || null }, { encabezados: { 'X-Autor': autor } });

/** Historial de un titular; el huésped solo puede pedir el suyo (verificación por propietario, 7.2). */
export const historialTitular = (codigoTitular, pagina = 0) =>
  api.get(`/api/reservas/titular/${encodeURIComponent(codigoTitular)}`, { pagina });

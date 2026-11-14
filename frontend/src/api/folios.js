import { api } from './cliente';

const ruta = (codigoReserva) => `/api/folios/${encodeURIComponent(codigoReserva)}`;

export const consultarFolio = (codigoReserva) => api.get(ruta(codigoReserva));
export const registrarPago = (codigoReserva, { monto, medioPago, fecha }) =>
  api.post(`${ruta(codigoReserva)}/pagos`, { monto, medioPago, fecha });
export const registrarServicio = (codigoReserva, nombreServicio, fecha) =>
  api.post(`${ruta(codigoReserva)}/servicios`, { nombreServicio, fecha });
export const revertirCargo = (codigoReserva, numero, fecha) =>
  api.post(`${ruta(codigoReserva)}/cargos/${numero}/reverso`, undefined, { consulta: { fecha } });
export const revertirPago = (codigoReserva, numero, fecha) =>
  api.post(`${ruta(codigoReserva)}/pagos/${numero}/reverso`, undefined, { consulta: { fecha } });
export const cerrarFolio = (codigoReserva, motivoAutorizacion, autor) =>
  api.post(`${ruta(codigoReserva)}/cierre`, { motivoAutorizacion: motivoAutorizacion || null }, { encabezados: { 'X-Autor': autor } });

/** Folio de una reserva propia del huésped. */
export const consultarFolioPropio = (codigoReserva, codigoTitular) =>
  api.get(`${ruta(codigoReserva)}/propio`, { codigoTitular });

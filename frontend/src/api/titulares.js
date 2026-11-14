import { api } from './cliente';

/** Titular sin cuenta, registrado por recepción (3.6). */
export const registrarTitular = (datos) => api.post('/api/titulares', datos);

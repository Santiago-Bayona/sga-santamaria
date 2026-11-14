import { api } from './cliente';

export const consultarAlojamiento = () => api.get('/api/alojamiento');
export const actualizarConfiguracion = (datos) => api.put('/api/alojamiento/configuracion', datos);
export const actualizarNormas = (normas) => api.put('/api/alojamiento/normas', { normas });
export const agregarServicio = (datos) => api.post('/api/alojamiento/servicios', datos);

export const listarTemporadas = () => api.get('/api/alojamiento/temporadas');
export const crearTemporada = (datos) => api.post('/api/alojamiento/temporadas', datos);
export const retirarTemporada = (id) => api.post(`/api/alojamiento/temporadas/${encodeURIComponent(id)}/retiro`);

export const consultarPoliticaVigente = () => api.get('/api/politicas/vigente');
export const definirPolitica = (datos) => api.post('/api/politicas', datos);

/** Solo lista los apartamentos activos (los retirados no se muestran, 7.3). */
export const listarApartamentos = (pagina = 0) => api.get('/api/apartamentos', { pagina });

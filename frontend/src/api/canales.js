import { api } from './cliente';

export const listarBitacora = (pagina = 0) => api.get('/api/canales/bitacora', { pagina });
export const listarConflictosCanal = (pagina = 0) => api.get('/api/canales/conflictos', { pagina });

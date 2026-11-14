import { api } from './cliente';

const ruta = (id) => `/api/apartamentos/${encodeURIComponent(id)}`;

export const consultarApartamento = (id) => api.get(ruta(id));
export const crearApartamento = (datos) => api.post('/api/apartamentos', datos);
export const actualizarApartamento = (id, datos) => api.put(ruta(id), datos);
export const consultarTarifas = (id) => api.get(`${ruta(id)}/tarifas`);
export const definirTarifa = (id, idTemporada, valorPorOcupante) =>
  api.post(`${ruta(id)}/tarifas`, { identificacionApartamento: id, idTemporada, valorPorOcupante });
export const activarApartamento = (id) => api.post(`${ruta(id)}/activacion`);
export const retirarApartamento = (id) => api.post(`${ruta(id)}/retiro`);
/** Devuelve las reservas que quedan por encima de la nueva capacidad (7.3: se advierten, no se cambian). */
export const cambiarCapacidad = (id, nuevaCapacidad) =>
  api.post(`${ruta(id)}/capacidad`, { identificacionApartamento: id, nuevaCapacidad });
export const declararFueraDeServicio = (id) => api.post(`${ruta(id)}/fuera-de-servicio`);
export const reintegrarApartamento = (id) => api.post(`${ruta(id)}/reintegracion`);

/** Sube una foto a la galería (JPG, PNG o WebP de hasta 5 MB) y devuelve el apartamento actualizado. */
export function subirImagen(id, archivo) {
  const formulario = new FormData();
  formulario.append('imagen', archivo);
  return api.post(`${ruta(id)}/imagenes`, formulario);
}

/** Guarda la galería (orden y principal) con el resto de datos editables del apartamento. */
export const guardarGaleria = (apartamento, imagenes, indiceImagenPrincipal) => actualizarApartamento(apartamento.identificacion, {
  identificacionApartamento: apartamento.identificacion,
  nombre: apartamento.nombre,
  descripcion: apartamento.descripcion,
  dormitorios: apartamento.dormitorios,
  dotacion: apartamento.dotacion ?? [],
  imagenes,
  indiceImagenPrincipal,
});

export const listarPendientesPreparacion = (pagina = 0) => api.get('/api/apartamentos/pendientes-preparacion', { pagina });
export const iniciarPreparacion = (id) => api.post(`${ruta(id)}/preparacion/inicio`);
export const terminarPreparacion = (id) => api.post(`${ruta(id)}/preparacion/fin`);

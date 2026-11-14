import { api } from './cliente';

export const miPerfil = () => api.get('/api/usuarios/me');
export const actualizarPerfil = (nombre, telefono) => api.put('/api/usuarios/me', { nombre, telefono });
export const cambiarContrasena = (contrasenaActual, contrasenaNueva) =>
  api.post('/api/usuarios/me/contrasena', { contrasenaActual, contrasenaNueva });

export const subirFotoPerfil = (archivo) => {
  const cuerpo = new FormData();
  cuerpo.append('imagen', archivo);
  return api.post('/api/usuarios/me/foto', cuerpo);
};

export const listarUsuarios = ({ rol, activo, pagina = 0 } = {}) =>
  api.get('/api/admin/usuarios', { rol, activo, pagina });
export const cambiarRol = (username, rol) =>
  api.put(`/api/admin/usuarios/${encodeURIComponent(username)}/rol`, { rol });
export const desactivarUsuario = (username) =>
  api.post(`/api/admin/usuarios/${encodeURIComponent(username)}/desactivacion`);

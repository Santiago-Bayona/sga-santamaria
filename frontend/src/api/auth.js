import { api } from './cliente';

export const iniciarSesion = (username, password) => api.post('/api/auth/login', { username, password });

/** Registro público (7.2): siempre crea un usuario con rol HUESPED. */
export const registrarHuesped = (datos) => api.post('/api/auth/registro', datos);

/** Recuperación de contraseña: siempre responde 202, exista o no el correo. */
export const solicitarRecuperacion = (correo) => api.post('/api/auth/recuperar-contrasena', { correo });

/** Cambia la contraseña con el token que llegó en el enlace del correo (204). */
export const restablecerContrasena = (token, contrasena) => api.post('/api/auth/restablecer-contrasena', { token, contrasena });

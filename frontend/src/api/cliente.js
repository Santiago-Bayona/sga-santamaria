/**
 * Cliente HTTP único del frontend: el "adaptador" hacia la API del SGA.
 * Pone el token, convierte la respuesta y traduce los errores al ErrorResponse del backend.
 */
/** Origen del API. Vacío = mismo origen (en desarrollo, el proxy de Vite). Acepta la URL con o sin /api final. */
function origenApi() {
  let base = (import.meta.env.VITE_API_URL ?? '').trim().replace(/\/$/, '');
  if (base.endsWith('/api')) base = base.slice(0, -4);
  return base;
}

let tokenActual = null;
let alExpirarSesion = () => {};

/** El AuthContext guarda aquí el token de la sesión (o null al cerrarla). */
export function establecerToken(token) {
  tokenActual = token ?? null;
}

/** El AuthContext indica qué hacer cuando el backend responde 401 con un token: la sesión venció. */
export function alVencerSesion(funcion) {
  alExpirarSesion = funcion;
}

/** Error con la forma del ErrorResponse del backend: status, mensaje y detalles. */
export class ErrorApi extends Error {
  constructor({ status, mensaje, detalles = [], error }) {
    super(mensaje);
    this.status = status;
    this.detalles = detalles;
    this.tipo = error;
  }
}

const MENSAJES_POR_ESTADO = {
  401: 'Tu sesión expiró o no iniciaste sesión.',
  403: 'No tienes permiso para realizar esta acción.',
  404: 'No encontramos lo que buscas.',
  500: 'Ocurrió un error inesperado en el servidor.',
};

function construirUrl(ruta, consulta) {
  const origen = origenApi();
  const url = origen
    ? new URL(ruta, origen.endsWith('/') ? origen : `${origen}/`)
    : new URL(ruta, window.location.origin);
  if (consulta) {
    Object.entries(consulta).forEach(([clave, valor]) => {
      if (valor === undefined || valor === null || valor === '') return;
      (Array.isArray(valor) ? valor : [valor]).forEach((v) => url.searchParams.append(clave, v));
    });
  }
  return url;
}

export async function solicitar(ruta, { metodo = 'GET', cuerpo, consulta, encabezados = {} } = {}) {
  const token = tokenActual;
  // Un FormData (subida de archivos) va tal cual: el navegador pone el Content-Type con su boundary.
  const esFormulario = typeof FormData !== 'undefined' && cuerpo instanceof FormData;
  let respuesta;
  try {
    respuesta = await fetch(construirUrl(ruta, consulta), {
      method: metodo,
      signal: AbortSignal.timeout(70_000),
      headers: {
        Accept: 'application/json',
        ...(cuerpo !== undefined && !esFormulario ? { 'Content-Type': 'application/json' } : {}),
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...encabezados,
      },
      body: esFormulario ? cuerpo : cuerpo !== undefined ? JSON.stringify(cuerpo) : undefined,
    });
  } catch (error) {
    if (error?.name === 'TimeoutError' || error?.name === 'AbortError') {
      throw new ErrorApi({ status: 0, mensaje: 'El servidor tardó demasiado en responder. Si acaba de despertar, intenta de nuevo.' });
    }
    throw new ErrorApi({ status: 0, mensaje: 'No hay conexión con el servidor. ¿Está arriba el backend?' });
  }

  if (respuesta.status === 204) return null;
  const texto = await respuesta.text();
  let datos = null;
  try { datos = texto ? JSON.parse(texto) : null; } catch { /* respuesta sin JSON */ }

  if (!respuesta.ok) {
    // Un 401 con token significa sesión vencida; sin token (login fallido) solo es un error del formulario.
    if (respuesta.status === 401 && token) alExpirarSesion();
    throw new ErrorApi({
      status: respuesta.status,
      error: datos?.error,
      mensaje: datos?.mensaje || MENSAJES_POR_ESTADO[respuesta.status] || `Error ${respuesta.status}`,
      detalles: datos?.detalles ?? [],
    });
  }
  return datos;
}

export const api = {
  get: (ruta, consulta, opciones) => solicitar(ruta, { ...opciones, consulta }),
  post: (ruta, cuerpo, opciones) => solicitar(ruta, { ...opciones, metodo: 'POST', cuerpo }),
  put: (ruta, cuerpo, opciones) => solicitar(ruta, { ...opciones, metodo: 'PUT', cuerpo }),
};

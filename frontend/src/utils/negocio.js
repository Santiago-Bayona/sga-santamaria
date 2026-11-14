/** Textos del lenguaje ubicuo (sección 4 del enunciado) para mostrar en pantalla. */

export const ESTADOS_RESERVA = {
  PENDIENTE: 'Pendiente',
  CONFIRMADA: 'Confirmada',
  EN_CURSO: 'En curso',
  FINALIZADA: 'Finalizada',
  CANCELADA: 'Cancelada',
  NO_SHOW: 'No-show',
};

export const CANALES = { PORTAL: 'Portal', DIRECTO: 'Directo', EXTERNO: 'Externo' };

export const MEDIOS_PAGO = { EFECTIVO: 'Efectivo', TRANSFERENCIA: 'Transferencia', TARJETA: 'Tarjeta' };

export const TIPOS_CARGO = {
  ALOJAMIENTO: 'Alojamiento',
  SERVICIO_ADICIONAL: 'Servicio adicional',
  AJUSTE: 'Ajuste',
  PENALIDAD: 'Penalidad',
};

export const ROLES = {
  ADMIN: 'Administrador',
  RECEPCION: 'Recepción',
  SERVICIO: 'Personal de servicio',
  HUESPED: 'Huésped',
  CANAL: 'Canal externo',
};

/** Por qué el resumen salió sin IA (MotivoDegradacionIa del backend). */
export const MOTIVOS_SIN_IA = {
  IA_DESHABILITADA: 'La IA está apagada en la configuración.',
  IA_SIMULADA: 'La IA está en modo simulado (sin modelo real).',
  TIEMPO_AGOTADO: 'El modelo de IA tardó demasiado en responder.',
  ERROR_PROVEEDOR: 'El proveedor de IA no respondió correctamente.',
};

/**
 * Qué acciones muestra la interfaz según el estado (sección 8). Es solo comodidad visual:
 * quien valida la transición de verdad es el dominio, que responde 409 si no aplica.
 */
export const ACCIONES_POR_ESTADO = {
  PENDIENTE: ['confirmar', 'cancelar'],
  CONFIRMADA: ['llegada', 'cancelar', 'noShow'],
  EN_CURSO: ['salida'],
  FINALIZADA: [],
  CANCELADA: [],
  NO_SHOW: [],
};

/** Pantalla de inicio de cada rol después de iniciar sesión. */
export function inicioPorRol(rol) {
  if (rol === 'ADMIN' || rol === 'RECEPCION') return '/recepcion';
  if (rol === 'HUESPED') return '/huesped';
  if (rol === 'SERVICIO') return '/servicio';
  return '/';
}

export const ESTADOS_OPERATIVOS = {
  PREPARADO: 'Preparado',
  OCUPADO: 'Ocupado',
  PENDIENTE_PREPARACION: 'Pendiente de preparación',
  EN_PREPARACION: 'En preparación',
  FUERA_DE_SERVICIO: 'Fuera de servicio',
};

export const GRAVEDADES = { BAJA: 'Baja', MEDIA: 'Media', ALTA: 'Alta' };

export const SITUACIONES_NOCHE = { DISPONIBLE: 'Disponible', OCUPADA: 'Ocupada', BLOQUEADA: 'Bloqueada' };

export const MESES_LARGOS = ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto',
  'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'];

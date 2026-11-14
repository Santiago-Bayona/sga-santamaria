const pesos = new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 });

/** 80000 → "$ 80.000" (COP sin decimales, 3.4). */
export const formatearPesos = (valor) => (valor === null || valor === undefined ? '—' : pesos.format(Number(valor)));

const MESES = ['ene', 'feb', 'mar', 'abr', 'may', 'jun', 'jul', 'ago', 'sep', 'oct', 'nov', 'dic'];

/** "2026-11-17" → "17 nov 2026". Se parte el texto para no correr el día por la zona horaria. */
export function formatearFecha(iso) {
  if (!iso) return '—';
  const [a, m, d] = String(iso).slice(0, 10).split('-');
  return `${Number(d)} ${MESES[Number(m) - 1]} ${a}`;
}

/** "2026-11-17T15:04:00" → "17 nov 2026, 3:04 p. m." */
export function formatearFechaHora(iso) {
  if (!iso) return '—';
  return `${formatearFecha(iso)}, ${formatearHora(String(iso).slice(11, 16))}`;
}

/** "15:00:00" → "3:00 p. m." */
export function formatearHora(hora) {
  if (!hora) return '—';
  const [h, m] = String(hora).split(':').map(Number);
  const sufijo = h >= 12 ? 'p. m.' : 'a. m.';
  return `${((h + 11) % 12) + 1}:${String(m).padStart(2, '0')} ${sufijo}`;
}

/** Fecha de hoy en hora local (Colombia) como "YYYY-MM-DD". */
export function hoyIso() {
  const d = new Date();
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
}

/** Suma días a una fecha "YYYY-MM-DD". */
export function sumarDias(iso, dias) {
  const [a, m, d] = iso.split('-').map(Number);
  const f = new Date(a, m - 1, d + dias);
  return `${f.getFullYear()}-${String(f.getMonth() + 1).padStart(2, '0')}-${String(f.getDate()).padStart(2, '0')}`;
}

/** "15:00" (input time) → "15:00:00" (LocalTime de Java). */
export const aHoraJava = (hora) => (hora && hora.length === 5 ? `${hora}:00` : hora);

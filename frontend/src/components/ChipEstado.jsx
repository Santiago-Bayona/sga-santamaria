import { ESTADOS_RESERVA } from '../utils/negocio';

export default function ChipEstado({ estado }) {
  return <span className={`chip chip--${estado}`}>{ESTADOS_RESERVA[estado] ?? estado}</span>;
}

import { UserX } from 'lucide-react';
import EstadoVacio from '../../components/EstadoVacio';

/** El backend no devolvió el titular del huésped (falta el cambio de AuthController o la sesión es anterior). */
export default function AvisoSinTitular() {
  return (
    <div className="tarjeta">
      <EstadoVacio icono={UserX} titulo="Tu cuenta no tiene un titular asociado">
        Cierra sesión y vuelve a entrar. Si sigue igual, pide a recepción que revise tu cuenta.
      </EstadoVacio>
    </div>
  );
}

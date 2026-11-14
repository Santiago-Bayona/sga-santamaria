import { Link } from 'react-router';
import { MapPinOff } from 'lucide-react';
import EstadoVacio from '../../components/EstadoVacio';

export default function NoEncontrado() {
  return (
    <div className="pagina" style={{ minHeight: '80vh', justifyContent: 'center' }}>
      <div className="tarjeta">
        <EstadoVacio icono={MapPinOff} titulo="Esta página no existe">Revisa la dirección o vuelve al inicio.</EstadoVacio>
        <div className="fila" style={{ justifyContent: 'center', paddingBottom: 32 }}>
          <Link className="boton boton--secundario" to="/">Volver al inicio</Link>
        </div>
      </div>
    </div>
  );
}

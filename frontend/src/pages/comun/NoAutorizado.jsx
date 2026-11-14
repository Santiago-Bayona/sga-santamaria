import { Link } from 'react-router';
import { ShieldAlert } from 'lucide-react';
import EstadoVacio from '../../components/EstadoVacio';
import { useAuth } from '../../auth/AuthContext';
import { inicioPorRol } from '../../utils/negocio';

/** 403: la sesión es válida, pero el rol no permite ver esta pantalla. */
export default function NoAutorizado() {
  const { usuario } = useAuth();
  return (
    <div className="pagina" style={{ minHeight: '70vh', justifyContent: 'center' }}>
      <div className="tarjeta">
        <EstadoVacio icono={ShieldAlert} titulo="No tienes permiso para ver esta sección">
          Tu rol no tiene acceso a esta pantalla.
        </EstadoVacio>
        <div className="fila" style={{ justifyContent: 'center', paddingBottom: 32 }}>
          <Link className="boton boton--secundario" to={inicioPorRol(usuario?.rol)}>Ir a mi inicio</Link>
        </div>
      </div>
    </div>
  );
}

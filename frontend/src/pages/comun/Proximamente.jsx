import { Hammer } from 'lucide-react';
import EstadoVacio from '../../components/EstadoVacio';
import { useAuth } from '../../auth/AuthContext';
import { ROLES } from '../../utils/negocio';

/** Inicio provisional de los roles que llegan en la segunda mitad del frontend. */
export default function Proximamente() {
  const { usuario } = useAuth();
  return (
    <div className="pagina">
      <h1 className="pagina__titulo">Hola, {usuario.nombre}</h1>
      <div className="tarjeta">
        <EstadoVacio icono={Hammer} titulo={`El panel de ${ROLES[usuario.rol]?.toLowerCase() ?? 'tu rol'} está en construcción`}>
          Muy pronto podrás gestionar desde aquí todo lo de tu rol.
        </EstadoVacio>
      </div>
    </div>
  );
}

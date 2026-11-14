import { Navigate, Outlet, useLocation } from 'react-router';
import { useAuth } from './AuthContext';
import NoAutorizado from '../pages/comun/NoAutorizado';

/**
 * Envuelve las rutas privadas: sin sesión manda al login; con un rol que no corresponde muestra 403.
 * Es comodidad visual: la seguridad real la aplica el backend con @PreAuthorize.
 */
export default function RutaProtegida({ roles }) {
  const { autenticado, usuario } = useAuth();
  const ubicacion = useLocation();
  if (!autenticado) return <Navigate to="/login" replace state={{ desde: ubicacion.pathname + ubicacion.search }} />;
  if (roles && !roles.includes(usuario.rol)) return <NoAutorizado />;
  return <Outlet />;
}

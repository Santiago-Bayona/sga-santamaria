import { Link, Outlet } from 'react-router';
import { LayoutDashboard } from 'lucide-react';
import Marca from '../components/Marca';
import { useAuth } from '../auth/AuthContext';
import { inicioPorRol } from '../utils/negocio';
import './publico.css';

/** Marco del sitio público: inicio, búsqueda y detalle de apartamentos. */
export default function LayoutPublico() {
  const { autenticado, usuario } = useAuth();
  return (
    <div className="publico">
      <header className="cabecera-publica">
        <div className="cabecera-publica__interior">
          <Link to="/" className="marca-enlace" aria-label="Santamaría, inicio"><Marca /></Link>
          <nav className="fila">
            {autenticado ? (
              <Link to={inicioPorRol(usuario.rol)} className="boton boton--primario boton--pequeno">
                <LayoutDashboard size={16} aria-hidden="true" /> {usuario.rol === 'HUESPED' ? 'Mis reservas' : 'Ir al panel'}
              </Link>
            ) : (
              <>
                <Link to="/registro" className="boton boton--fantasma boton--pequeno">Crear cuenta</Link>
                <Link to="/login" className="boton boton--primario boton--pequeno">Iniciar sesión</Link>
              </>
            )}
          </nav>
        </div>
      </header>
      <main id="contenido" style={{ flex: 1 }}><Outlet /></main>
      <footer className="pie-publico">
        <div className="pie-publico__franja" />
        <div className="pie-publico__interior">
          <Marca claro />
          <p>Apartamentos completos con piscina en Montenegro, Quindío.</p>
        </div>
      </footer>
    </div>
  );
}

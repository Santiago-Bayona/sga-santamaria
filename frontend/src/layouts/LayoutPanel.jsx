import { useState } from 'react';
import { NavLink, Outlet, useLocation } from 'react-router';
import {
  Ban, BarChart3, BedDouble, Building2, CalendarCheck, CalendarDays, CalendarRange, ClipboardList, Globe, Luggage, LogOut,
  Menu, MessageSquareWarning, PlusCircle, ScrollText, Sparkles, X,
} from 'lucide-react';
import Marca from '../components/Marca';
import Boton from '../components/Boton';
import { useAuth } from '../auth/AuthContext';
import { ROLES } from '../utils/negocio';
import './panel.css';

/** Menú por rol (matriz 7.1). El administrador tiene todo lo de recepción más su propia sección. */
const RECEPCION = {
  titulo: 'Recepción',
  enlaces: [
    { a: '/recepcion', texto: 'Operación del día', icono: CalendarCheck, exacto: true },
    { a: '/recepcion/reservas', texto: 'Reservas', icono: ClipboardList,
      activo: (ruta) => (ruta.startsWith('/recepcion/reservas') && ruta !== '/recepcion/reservas/nueva') || ruta.startsWith('/recepcion/folios') },
    { a: '/recepcion/reservas/nueva', texto: 'Nueva reserva', icono: PlusCircle },
    { a: '/recepcion/calendario', texto: 'Calendario', icono: CalendarDays },
    { a: '/recepcion/bloqueos', texto: 'Bloqueos', icono: Ban },
  ],
};
const SITIO = { titulo: 'Sitio', enlaces: [{ a: '/', texto: 'Ver sitio público', icono: Globe, exacto: true }] };
const MENU = {
  RECEPCION: [RECEPCION, SITIO],
  ADMIN: [RECEPCION, {
    titulo: 'Administración',
    enlaces: [
      { a: '/admin/alojamiento', texto: 'Alojamiento', icono: Building2 },
      { a: '/admin/apartamentos', texto: 'Apartamentos', icono: BedDouble },
      { a: '/admin/temporadas', texto: 'Temporadas', icono: CalendarRange },
      { a: '/admin/politica', texto: 'Política de cancelación', icono: ScrollText },
      { a: '/admin/reportes', texto: 'Reportes', icono: BarChart3 },
      { a: '/servicio/novedades', texto: 'Novedades', icono: MessageSquareWarning },
    ],
  }, SITIO],
  SERVICIO: [{
    titulo: 'Servicio',
    enlaces: [
      { a: '/servicio', texto: 'Preparación', icono: Sparkles, exacto: true },
      { a: '/servicio/novedades', texto: 'Novedades', icono: MessageSquareWarning },
    ],
  }],
  HUESPED: [{
    titulo: 'Mi cuenta',
    enlaces: [
      { a: '/huesped', texto: 'Mis reservas', icono: Luggage,
        activo: (ruta) => ruta === '/huesped' || (ruta.startsWith('/huesped/reservas/') && ruta !== '/huesped/reservas/nueva') },
      { a: '/', texto: 'Buscar y reservar', icono: Globe, exacto: true },
    ],
  }],
};

function iniciales(nombre = '') {
  return nombre.split(' ').filter(Boolean).slice(0, 2).map((p) => p[0]).join('').toUpperCase() || 'S';
}

export default function LayoutPanel() {
  const { usuario, cerrarSesion } = useAuth();
  const [menuAbierto, setMenuAbierto] = useState(false);
  const { pathname } = useLocation();
  const [rutaPrevia, setRutaPrevia] = useState(pathname);
  if (rutaPrevia !== pathname) { setRutaPrevia(pathname); setMenuAbierto(false); }

  const secciones = MENU[usuario.rol] ?? [];

  return (
    <div className={`panel ${menuAbierto ? 'panel--menu-abierto' : ''}`}>
      <div className="barra-movil">
        <Marca claro />
        <button type="button" onClick={() => setMenuAbierto((v) => !v)} aria-label={menuAbierto ? 'Cerrar menú' : 'Abrir menú'}>
          {menuAbierto ? <X size={24} /> : <Menu size={24} />}
        </button>
      </div>
      {menuAbierto && <div className="cortina" onClick={() => setMenuAbierto(false)} aria-hidden="true" />}

      <aside className="lateral" aria-label="Menú principal">
        <div className="lateral__franja" />
        <div className="lateral__marca"><Marca claro /></div>
        <nav>
          {secciones.map((s) => (
            <div key={s.titulo} className="lateral__seccion">
              <p className="lateral__titulo">{s.titulo}</p>
              {s.enlaces.map(({ a, texto, icono: Icono, exacto, activo }) => (
                <NavLink key={a} to={a} end={exacto}
                  className={({ isActive }) => `lateral__enlace ${(activo ? activo(pathname) : isActive) ? 'active' : ''}`}>
                  <Icono size={19} aria-hidden="true" /> {texto}
                </NavLink>
              ))}
            </div>
          ))}
        </nav>
        <div className="lateral__pie">
          <div className="lateral__usuario">
            <span className="lateral__avatar" aria-hidden="true">{iniciales(usuario.nombre)}</span>
            <div>
              <p className="lateral__nombre">{usuario.nombre}</p>
              <p className="lateral__rol">{ROLES[usuario.rol] ?? usuario.rol}</p>
            </div>
          </div>
          <Boton variante="fantasma" tamano="pequeno" className="lateral__salir" onClick={cerrarSesion}>
            <LogOut size={17} aria-hidden="true" /> Cerrar sesión
          </Boton>
        </div>
      </aside>

      <main id="contenido">
        <Outlet />
      </main>
    </div>
  );
}

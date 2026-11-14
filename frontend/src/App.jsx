import { BrowserRouter, Route, Routes } from 'react-router';
import { AuthProvider } from './auth/AuthContext';
import RutaProtegida from './auth/RutaProtegida';
import { ToastProvider } from './components/Toasts';
import LayoutPanel from './layouts/LayoutPanel';
import LayoutPublico from './layouts/LayoutPublico';
import Inicio from './pages/publico/Inicio';
import Disponibilidad from './pages/publico/Disponibilidad';
import DetalleApartamento from './pages/publico/DetalleApartamento';
import Login from './pages/publico/Login';
import Registro from './pages/publico/Registro';
import RecuperarContrasena from './pages/publico/RecuperarContrasena';
import RestablecerContrasena from './pages/publico/RestablecerContrasena';
import MisReservas from './pages/huesped/MisReservas';
import NuevaReservaHuesped from './pages/huesped/NuevaReservaHuesped';
import DetalleReservaHuesped from './pages/huesped/DetalleReservaHuesped';
import OperacionDelDia from './pages/recepcion/OperacionDelDia';
import Reservas from './pages/recepcion/Reservas';
import NuevaReserva from './pages/recepcion/NuevaReserva';
import DetalleReserva from './pages/recepcion/DetalleReserva';
import Folio from './pages/recepcion/Folio';
import Calendario from './pages/recepcion/Calendario';
import Bloqueos from './pages/recepcion/Bloqueos';
import Preparacion from './pages/servicio/Preparacion';
import Novedades from './pages/servicio/Novedades';
import Alojamiento from './pages/admin/Alojamiento';
import Apartamentos from './pages/admin/Apartamentos';
import DetalleApartamentoAdmin from './pages/admin/DetalleApartamentoAdmin';
import Temporadas from './pages/admin/Temporadas';
import Politica from './pages/admin/Politica';
import Reportes from './pages/admin/Reportes';
import NoEncontrado from './pages/comun/NoEncontrado';

const INTERNOS = ['RECEPCION', 'ADMIN'];

/** Mapa de rutas: qué pantalla, dentro de qué marco y para qué roles (matriz 7.1). */
export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <ToastProvider>
          <Routes>
            <Route element={<LayoutPublico />}>
              <Route path="/" element={<Inicio />} />
              <Route path="/disponibilidad" element={<Disponibilidad />} />
              <Route path="/apartamentos/:id" element={<DetalleApartamento />} />
            </Route>
            <Route path="/login" element={<Login />} />
            <Route path="/registro" element={<Registro />} />
            <Route path="/recuperar-contrasena" element={<RecuperarContrasena />} />
            <Route path="/restablecer" element={<RestablecerContrasena />} />

            <Route element={<RutaProtegida />}>
              <Route element={<LayoutPanel />}>
                <Route element={<RutaProtegida roles={['HUESPED']} />}>
                  <Route path="/huesped" element={<MisReservas />} />
                  <Route path="/huesped/reservas/nueva" element={<NuevaReservaHuesped />} />
                  <Route path="/huesped/reservas/:codigo" element={<DetalleReservaHuesped />} />
                </Route>
                <Route element={<RutaProtegida roles={INTERNOS} />}>
                  <Route path="/recepcion" element={<OperacionDelDia />} />
                  <Route path="/recepcion/reservas" element={<Reservas />} />
                  <Route path="/recepcion/reservas/nueva" element={<NuevaReserva />} />
                  <Route path="/recepcion/reservas/:codigo" element={<DetalleReserva />} />
                  <Route path="/recepcion/folios/:codigo" element={<Folio />} />
                  <Route path="/recepcion/calendario" element={<Calendario />} />
                  <Route path="/recepcion/bloqueos" element={<Bloqueos />} />
                </Route>
                <Route element={<RutaProtegida roles={['SERVICIO', 'ADMIN']} />}>
                  <Route path="/servicio" element={<Preparacion />} />
                  <Route path="/servicio/novedades" element={<Novedades />} />
                </Route>
                <Route element={<RutaProtegida roles={['ADMIN']} />}>
                  <Route path="/admin/alojamiento" element={<Alojamiento />} />
                  <Route path="/admin/apartamentos" element={<Apartamentos />} />
                  <Route path="/admin/apartamentos/:id" element={<DetalleApartamentoAdmin />} />
                  <Route path="/admin/temporadas" element={<Temporadas />} />
                  <Route path="/admin/politica" element={<Politica />} />
                  <Route path="/admin/reportes" element={<Reportes />} />
                </Route>
              </Route>
            </Route>

            <Route path="*" element={<NoEncontrado />} />
          </Routes>
        </ToastProvider>
      </AuthProvider>
    </BrowserRouter>
  );
}

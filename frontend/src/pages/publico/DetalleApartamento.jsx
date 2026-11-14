import { useState } from 'react';
import { Link, useParams, useSearchParams } from 'react-router';
import { ArrowLeft, BedDouble, Calculator, Check, Users } from 'lucide-react';
import Tarjeta from '../../components/Tarjeta';
import Boton from '../../components/Boton';
import Campo from '../../components/Campo';
import Esqueleto from '../../components/Esqueleto';
import EstadoVacio from '../../components/EstadoVacio';
import Galeria from '../../components/Galeria';
import MapaUbicacion from '../../components/MapaUbicacion';
import { fechasDelGrupo } from '../../utils/grupo';
import { useToast } from '../../components/Toasts';
import { useAuth } from '../../auth/AuthContext';
import useApartamentos from '../../hooks/useApartamentos';
import useConsulta from '../../hooks/useConsulta';
import { cotizarEstancia } from '../../api/disponibilidad';
import { consultarAlojamiento } from '../../api/alojamiento';
import { formatearFecha, formatearHora, formatearPesos, hoyIso, sumarDias } from '../../utils/formato';
import { SelectorDesplegable } from './Inicio';
import { leerBusqueda, parametrosBusqueda } from './busqueda';

/**
 * Detalle público de un apartamento con el cotizador (7.4): valor noche por noche, con la temporada,
 * la tarifa y los ocupantes facturables de cada noche. El cálculo lo hace el backend.
 */
export default function DetalleApartamento() {
  const { id } = useParams();
  const [parametros] = useSearchParams();
  const { apartamentos } = useApartamentos();
  const { autenticado, usuario } = useAuth();
  const toast = useToast();
  const { datos: alojamiento } = useConsulta(consultarAlojamiento, []);
  const [busqueda, setBusqueda] = useState(() => leerBusqueda(parametros));
  const [cotizacion, setCotizacion] = useState(null);
  const [cotizando, setCotizando] = useState(false);

  const apto = apartamentos.find((a) => a.identificacion === id);
  if (!apartamentos.length) return <div className="seccion"><div className="tarjeta"><Esqueleto filas={6} /></div></div>;
  if (!apto) {
    return (
      <div className="seccion">
        <div className="tarjeta"><EstadoVacio titulo="Este apartamento no está disponible">Puede que se haya retirado de la venta.</EstadoVacio></div>
      </div>
    );
  }

  async function cotizar(e) {
    e.preventDefault();
    if (busqueda.grupo.ninos.some((n) => !n)) { toast.error('Indica la fecha de nacimiento de cada niño.'); return; }
    setCotizando(true);
    try {
      const ocupantes = fechasDelGrupo(busqueda.grupo).map((fechaNacimiento, i) => ({ nombre: `Ocupante ${i + 1}`, fechaNacimiento }));
      setCotizacion(await cotizarEstancia({ identificacionApartamento: id, fechaEntrada: busqueda.entrada, fechaSalida: busqueda.salida, ocupantes }));
    } catch (error) {
      setCotizacion(null);
      toast.error(error);
    } finally {
      setCotizando(false);
    }
  }

  const destinoReserva = `/huesped/reservas/nueva?apto=${id}&${parametrosBusqueda(busqueda)}`;
  const cfg = alojamiento?.configuracion;

  return (
    <div className="seccion">
      <nav className="migas" aria-label="Ruta"><Link to="/"><ArrowLeft size={14} /> Inicio</Link></nav>
      <div className="rejilla rejilla--detalle-publico">
        <div className="pila">
          <Galeria nombre={apto.nombre} imagenes={apto.imagenes} principal={apto.imagenPrincipal} />
          <div>
            <h1 className="pagina__titulo">{apto.nombre}</h1>
            <div className="apto__meta" style={{ marginTop: 8 }}>
              <span><Users size={16} aria-hidden="true" /> Hasta {apto.capacidad} personas</span>
              <span><BedDouble size={16} aria-hidden="true" /> {apto.dormitorios} dormitorio{apto.dormitorios === 1 ? '' : 's'}</span>
            </div>
          </div>
          {apto.descripcion && <p style={{ fontSize: 16, lineHeight: 1.7 }}>{apto.descripcion}</p>}
          {apto.dotacion?.length > 0 && (
            <Tarjeta titulo="Dotación">
              <ul style={{ listStyle: 'none', margin: 0, padding: 0, display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(180px, 1fr))', gap: 10 }}>
                {apto.dotacion.map((d) => <li key={d} className="fila" style={{ gap: 8 }}><Check size={16} color="var(--verde)" aria-hidden="true" /> {d}</li>)}
              </ul>
            </Tarjeta>
          )}
          {cfg && (
            <Tarjeta titulo="Bueno saber">
              <div className="lista-datos">
                <Dato etiqueta="Entrada" valor={`Desde las ${formatearHora(cfg.horaEntrada)}`} />
                <Dato etiqueta="Salida" valor={`Hasta las ${formatearHora(cfg.horaSalida)}`} />
                <Dato etiqueta="Niños" valor={`Menores de ${cfg.umbralEdadFacturable} años no pagan`} />
                <Dato etiqueta="Anticipo" valor={`${cfg.porcentajeAnticipo}% para confirmar`} />
              </div>
              {alojamiento.normasConvivencia?.length > 0 && (
                <ul style={{ margin: '18px 0 0', paddingLeft: 18, color: 'var(--texto-2)', fontSize: 14, display: 'grid', gap: 4 }}>
                  {alojamiento.normasConvivencia.map((n) => <li key={n}>{n}</li>)}
                </ul>
              )}
            </Tarjeta>
          )}
          {alojamiento && (
            <section className="pila" aria-labelledby="titulo-ubicacion">
              <h2 id="titulo-ubicacion" className="seccion__titulo" style={{ fontSize: 20 }}>Dónde queda</h2>
              <MapaUbicacion latitud={alojamiento.latitud} longitud={alojamiento.longitud} nombre={alojamiento.nombre}
                direccion={alojamiento.direccion} ciudad={alojamiento.ciudad} />
            </section>
          )}
        </div>

        <Tarjeta titulo={<span className="fila" style={{ gap: 8 }}><Calculator size={18} /> Cotiza tu estancia</span>} className="pegajosa">
          <form className="pila" onSubmit={cotizar}>
            <div className="rejilla rejilla--2" style={{ gap: 10 }}>
              <Campo etiqueta="Llegada">
                {(p) => <input {...p} type="date" min={hoyIso()} value={busqueda.entrada}
                  onChange={(e) => { setBusqueda({ ...busqueda, entrada: e.target.value }); setCotizacion(null); }} />}
              </Campo>
              <Campo etiqueta="Salida">
                {(p) => <input {...p} type="date" min={sumarDias(busqueda.entrada || hoyIso(), 1)} value={busqueda.salida}
                  onChange={(e) => { setBusqueda({ ...busqueda, salida: e.target.value }); setCotizacion(null); }} />}
              </Campo>
            </div>
            <SelectorDesplegable grupo={busqueda.grupo} alCambiar={(grupo) => { setBusqueda({ ...busqueda, grupo }); setCotizacion(null); }} />
            <Boton type="submit" variante="secundario" cargando={cotizando}>Calcular valor</Boton>
          </form>

          {cotizacion && (
            <div style={{ marginTop: 20 }}>
              <table className="desglose">
                <thead><tr><th>Noche</th><th>Temporada</th><th className="num">Tarifa × pers.</th><th className="num">Subtotal</th></tr></thead>
                <tbody>
                  {cotizacion.desglose.map((n) => (
                    <tr key={n.fecha}>
                      <td>{formatearFecha(n.fecha)}</td>
                      <td>{n.temporada}</td>
                      <td className="num">{formatearPesos(n.tarifaAplicada)} × {n.ocupantesFacturables}</td>
                      <td className="num">{formatearPesos(n.subtotal)}</td>
                    </tr>
                  ))}
                </tbody>
                <tfoot><tr><td colSpan={3}>Total · {cotizacion.noches} noche{cotizacion.noches === 1 ? '' : 's'}</td><td className="num">{formatearPesos(cotizacion.total)}</td></tr></tfoot>
              </table>
              <div style={{ marginTop: 18 }}>
                {autenticado && usuario.rol !== 'HUESPED' ? (
                  <p className="campo__ayuda">Las reservas de recepción se crean desde el panel interno.</p>
                ) : (
                  <Link className="boton boton--primario boton--bloque" to={autenticado ? destinoReserva : '/login'}
                    state={autenticado ? undefined : { desde: destinoReserva }}>
                    {autenticado ? 'Reservar este apartamento' : 'Inicia sesión para reservar'}
                  </Link>
                )}
              </div>
            </div>
          )}
        </Tarjeta>
      </div>
    </div>
  );
}

function Dato({ etiqueta, valor }) {
  return <div><p className="dato__etiqueta">{etiqueta}</p><p className="dato__valor">{valor}</p></div>;
}

import { useState } from 'react';
import { Link, useParams } from 'react-router';
import { ArrowLeft, Ban, Power, RotateCcw, Save, Trash2, Users } from 'lucide-react';
import Tarjeta from '../../components/Tarjeta';
import Boton from '../../components/Boton';
import Campo from '../../components/Campo';
import Modal from '../../components/Modal';
import Esqueleto from '../../components/Esqueleto';
import EstadoVacio from '../../components/EstadoVacio';
import GaleriaAdmin from '../../components/GaleriaAdmin';
import { useToast } from '../../components/Toasts';
import useConsulta from '../../hooks/useConsulta';
import { listarTemporadas } from '../../api/alojamiento';
import {
  activarApartamento, cambiarCapacidad, consultarApartamento, consultarTarifas, declararFueraDeServicio,
  definirTarifa, reintegrarApartamento, retirarApartamento,
} from '../../api/apartamentos';
import { formatearFecha, formatearPesos } from '../../utils/formato';
import { ESTADOS_OPERATIVOS } from '../../utils/negocio';

/**
 * Administración de un apartamento (7.3): tarifas por temporada, activación, capacidad, fuera de servicio y retiro.
 * Cambiar la capacidad no altera reservas existentes: el sistema las lista como advertencia.
 */
export default function DetalleApartamentoAdmin() {
  const { id } = useParams();
  const toast = useToast();
  const apto = useConsulta(() => consultarApartamento(id), [id]);
  const tarifas = useConsulta(() => consultarTarifas(id), [id]);
  const temporadas = useConsulta(listarTemporadas, []);
  const [valores, setValores] = useState({});
  const [modal, setModal] = useState(null);
  const [capacidad, setCapacidad] = useState('');
  const [afectadas, setAfectadas] = useState(null);
  const [enviando, setEnviando] = useState(null);

  async function ejecutar(clave, funcion, mensaje) {
    setEnviando(clave);
    try {
      const r = await funcion();
      toast.exito(mensaje);
      setModal(null);
      await Promise.all([apto.recargar(), tarifas.recargar()]);
      return r;
    } catch (e) {
      toast.error(e);
      return undefined;
    } finally {
      setEnviando(null);
    }
  }

  if (apto.cargando && !apto.datos) return <div className="pagina"><Tarjeta><Esqueleto filas={6} /></Tarjeta></div>;
  if (apto.error) return <div className="pagina"><Tarjeta><EstadoVacio titulo="No se pudo cargar el apartamento">{apto.error.message}</EstadoVacio></Tarjeta></div>;

  const a = apto.datos;
  const tarifaDe = (t) => tarifas.datos?.find((x) => x.idTemporada === t)?.valorPorOcupante;
  const fuera = a.estadoOperativo === 'FUERA_DE_SERVICIO';

  return (
    <div className="pagina">
      <div>
        <nav className="migas" aria-label="Ruta"><Link to="/admin/apartamentos"><ArrowLeft size={14} /> Apartamentos</Link></nav>
        <div className="pagina__cabecera">
          <div className="fila" style={{ gap: 12 }}>
            <h1 className="pagina__titulo">{a.nombre}</h1>
            <span className={`chip chip--${a.estadoOperativo}`}>{ESTADOS_OPERATIVOS[a.estadoOperativo]}</span>
            <span className={`chip ${a.activo ? 'chip--CONFIRMADA' : 'chip--PENDIENTE'}`}>{a.activo ? 'Activo' : 'Inactivo'}</span>
          </div>
        </div>
        <p className="pagina__subtitulo">{a.identificacion} · {a.dormitorios} dormitorio(s) · capacidad {a.capacidad}</p>
      </div>

      <Tarjeta titulo="Acciones">
        <div className="acciones-reserva fila">
          {!a.activo && (
            <Boton cargando={enviando === 'activar'} onClick={() => ejecutar('activar', () => activarApartamento(id), 'Apartamento activado')}>
              <Power size={17} aria-hidden="true" /> Activar
            </Boton>
          )}
          <Boton variante="secundario" onClick={() => { setCapacidad(String(a.capacidad)); setAfectadas(null); setModal('capacidad'); }}>
            <Users size={17} aria-hidden="true" /> Cambiar capacidad
          </Boton>
          {fuera ? (
            <Boton variante="secundario" cargando={enviando === 'reintegrar'} onClick={() => ejecutar('reintegrar', () => reintegrarApartamento(id), 'Apartamento reintegrado')}>
              <RotateCcw size={17} aria-hidden="true" /> Reintegrar al servicio
            </Boton>
          ) : (
            <Boton variante="peligro" disabled={a.estadoOperativo === 'OCUPADO'} cargando={enviando === 'fuera'}
              onClick={async () => { const r = await ejecutar('fuera', () => declararFueraDeServicio(id), 'Declarado fuera de servicio'); if (r) setAfectadas(r); }}>
              <Ban size={17} aria-hidden="true" /> Fuera de servicio
            </Boton>
          )}
          <Boton variante="peligro" onClick={() => setModal('retirar')}><Trash2 size={17} aria-hidden="true" /> Retirar de la venta</Boton>
        </div>
        {fuera && <p className="campo__ayuda" style={{ marginTop: 12 }}>Recuerda registrar un bloqueo: fuera de servicio sin bloqueo sigue apareciendo como vendible a futuro (3.3).</p>}
        {afectadas && !modal && <ReservasAfectadas reservas={afectadas} />}
      </Tarjeta>

      <GaleriaAdmin apartamento={a} alCambiar={apto.recargar} />

      <Tarjeta titulo="Tarifas por temporada" sinRelleno>
        {(tarifas.cargando && !tarifas.datos) || (temporadas.cargando && !temporadas.datos) ? <Esqueleto filas={3} /> : (
          <table className="tabla">
            <thead><tr><th>Temporada</th><th className="num">Tarifa actual</th><th>Nueva tarifa (por ocupante facturable, por noche)</th></tr></thead>
            <tbody>
              {temporadas.datos?.map((t) => (
                <tr key={t.id}>
                  <td><strong>{t.nombre}</strong> {t.base && <span className="chip chip--neutro">Base</span>}</td>
                  <td className="num">{tarifaDe(t.id) !== undefined ? formatearPesos(tarifaDe(t.id)) : <span style={{ color: 'var(--rojo)' }}>Sin tarifa</span>}</td>
                  <td>
                    <div className="fila" style={{ flexWrap: 'nowrap' }}>
                      <input className="control" type="number" min={1} step={1000} aria-label={`Nueva tarifa ${t.nombre}`} style={{ maxWidth: 180, minHeight: 38 }}
                        value={valores[t.id] ?? ''} onChange={(e) => setValores({ ...valores, [t.id]: e.target.value })} />
                      <Boton variante="secundario" tamano="pequeno" disabled={!(Number(valores[t.id]) > 0)} cargando={enviando === t.id}
                        onClick={() => ejecutar(t.id, () => definirTarifa(id, t.id, Number(valores[t.id])), `Tarifa de ${t.nombre} actualizada`).then(() => setValores({ ...valores, [t.id]: '' }))}>
                        <Save size={15} aria-hidden="true" /> Guardar
                      </Boton>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </Tarjeta>

      <Modal abierto={modal === 'capacidad'} titulo="Cambiar capacidad" alCerrar={() => setModal(null)}
        pie={<><Boton variante="fantasma" onClick={() => setModal(null)}>Cerrar</Boton>
          <Boton cargando={enviando === 'capacidad'} disabled={!(Number(capacidad) > 0)}
            onClick={async () => { const r = await cambiarCapacidad(id, Number(capacidad)).catch((e) => { toast.error(e); return null; }); if (r) { setAfectadas(r); toast.exito('Capacidad actualizada'); apto.recargar(); } }}>
            Guardar</Boton></>}>
        <Campo etiqueta="Nueva capacidad" ayuda="Las reservas ya creadas no cambian aunque queden por encima.">
          {(p) => <input {...p} type="number" min={1} value={capacidad} onChange={(e) => setCapacidad(e.target.value)} />}
        </Campo>
        {afectadas && <ReservasAfectadas reservas={afectadas} />}
      </Modal>

      <Modal abierto={modal === 'retirar'} titulo="Retirar de la venta" alCerrar={() => setModal(null)}
        pie={<><Boton variante="fantasma" onClick={() => setModal(null)}>Volver</Boton>
          <Boton variante="peligro" cargando={enviando === 'retirar'} onClick={() => ejecutar('retirar', () => retirarApartamento(id), 'Apartamento retirado')}>Retirar</Boton></>}>
        <p>La eliminación es lógica: el apartamento deja de aparecer en listados y búsquedas, pero las reservas históricas lo siguen referenciando.
          Solo se permite si no tiene reservas activas ni futuras.</p>
      </Modal>
    </div>
  );
}

function ReservasAfectadas({ reservas }) {
  if (!reservas.length) return <div className="aviso aviso--info" style={{ marginTop: 14 }}>Ninguna reserva activa queda afectada.</div>;
  return (
    <div className="aviso aviso--alerta" style={{ marginTop: 14, flexDirection: 'column' }}>
      <strong>Reservas a revisar ({reservas.length})</strong>
      <ul style={{ margin: 0, paddingLeft: 18 }}>
        {reservas.map((r) => (
          <li key={r.codigo}><Link to={`/recepcion/reservas/${r.codigo}`}>{r.codigo}</Link> · {formatearFecha(r.fechaEntrada)} – {formatearFecha(r.fechaSalida)} · {r.totalOcupantes} personas</li>
        ))}
      </ul>
    </div>
  );
}

import { useState } from 'react';
import { Link, useParams } from 'react-router';
import { ArrowLeft, XCircle } from 'lucide-react';
import Tarjeta from '../../components/Tarjeta';
import Boton from '../../components/Boton';
import Campo from '../../components/Campo';
import Modal from '../../components/Modal';
import ChipEstado from '../../components/ChipEstado';
import Esqueleto from '../../components/Esqueleto';
import EstadoVacio from '../../components/EstadoVacio';
import { useToast } from '../../components/Toasts';
import { useAuth } from '../../auth/AuthContext';
import useConsulta from '../../hooks/useConsulta';
import useApartamentos from '../../hooks/useApartamentos';
import { cancelarReserva, consultarReserva } from '../../api/reservas';
import { consultarFolioPropio } from '../../api/folios';
import { formatearFecha, formatearHora, formatearPesos } from '../../utils/formato';
import { MEDIOS_PAGO, TIPOS_CARGO } from '../../utils/negocio';
import '../recepcion/recepcion.css';

/** Detalle de una reserva propia con su folio y la opción de cancelar (7.1). */
export default function DetalleReservaHuesped() {
  const { codigo } = useParams();
  const { usuario } = useAuth();
  const toast = useToast();
  const { nombre } = useApartamentos();
  const { datos: reserva, cargando, error, recargar } = useConsulta(() => consultarReserva(codigo), [codigo]);
  const folio = useConsulta(() => consultarFolioPropio(codigo, usuario.codigoTitular), [codigo, usuario.codigoTitular]);
  const [modal, setModal] = useState(false);
  const [motivo, setMotivo] = useState('');
  const [enviando, setEnviando] = useState(false);

  async function cancelar() {
    setEnviando(true);
    try {
      const l = await cancelarReserva(codigo, motivo.trim());
      toast.exito('Reserva cancelada', `Retención según tu política: ${formatearPesos(l.retencion)}.`);
      setModal(false);
      await Promise.all([recargar(), folio.recargar()]);
    } catch (e) {
      toast.error(e);
    } finally {
      setEnviando(false);
    }
  }

  if (cargando && !reserva) return <div className="pagina"><Tarjeta><Esqueleto filas={6} /></Tarjeta></div>;
  if (error) {
    return (
      <div className="pagina">
        <Tarjeta><EstadoVacio titulo={error.status === 403 ? 'Esta reserva no es tuya' : 'No pudimos cargar la reserva'}>{error.message}</EstadoVacio></Tarjeta>
      </div>
    );
  }

  const cancelable = ['PENDIENTE', 'CONFIRMADA'].includes(reserva.estado);
  const f = folio.datos;

  return (
    <div className="pagina">
      <div>
        <nav className="migas" aria-label="Ruta"><Link to="/huesped"><ArrowLeft size={14} /> Mis reservas</Link></nav>
        <div className="pagina__cabecera">
          <div className="fila" style={{ gap: 14 }}>
            <h1 className="pagina__titulo">{nombre(reserva.identificacionApartamento)}</h1>
            <ChipEstado estado={reserva.estado} />
          </div>
          {cancelable && <Boton variante="peligro" onClick={() => setModal(true)}><XCircle size={18} aria-hidden="true" /> Cancelar reserva</Boton>}
        </div>
        <p className="pagina__subtitulo">Reserva {reserva.codigo}</p>
      </div>

      {reserva.estado === 'PENDIENTE' && (
        <div className="aviso aviso--alerta">Tu reserva está pendiente. Se confirma cuando recepción registre el anticipo.</div>
      )}

      <Tarjeta titulo="Tu estancia">
        <div className="lista-datos">
          <Dato etiqueta="Llegada" valor={formatearFecha(reserva.fechaEntrada)} />
          <Dato etiqueta="Salida" valor={formatearFecha(reserva.fechaSalida)} />
          <Dato etiqueta="Noches" valor={reserva.noches} />
          <Dato etiqueta="Hora estimada" valor={formatearHora(reserva.horaEstimadaLlegada)} />
          <Dato etiqueta="Personas" valor={reserva.totalOcupantes} />
          <Dato etiqueta="Valor" valor={formatearPesos(reserva.valorTotal)} />
        </div>
      </Tarjeta>

      <Tarjeta titulo="Folio" sinRelleno>
        {folio.cargando && !f ? <Esqueleto filas={3} /> : !f ? (
          <EstadoVacio titulo="Folio no disponible">{folio.error?.message}</EstadoVacio>
        ) : (
          <>
            <div className="tabla-contenedor">
              <table className="tabla">
                <thead><tr><th>Movimiento</th><th>Fecha</th><th className="num">Monto</th></tr></thead>
                <tbody>
                  {f.cargos.map((c) => (
                    <tr key={`c${c.numero}`}><td>{c.esReverso ? `Reverso de cargo #${c.revierteA}` : `${TIPOS_CARGO[c.tipo] ?? c.tipo}: ${c.concepto}`}</td>
                      <td>{formatearFecha(c.fecha)}</td><td className="num">{c.esReverso ? '−' : ''}{formatearPesos(c.monto)}</td></tr>
                  ))}
                  {f.pagos.map((p) => (
                    <tr key={`p${p.numero}`}><td>{p.esReverso ? `Reverso de pago #${p.revierteA}` : `Pago (${MEDIOS_PAGO[p.medio] ?? p.medio})`}</td>
                      <td>{formatearFecha(p.fecha)}</td><td className="num" style={{ color: 'var(--verde)' }}>−{formatearPesos(p.monto)}</td></tr>
                  ))}
                </tbody>
              </table>
            </div>
            <div className="saldo">
              <div><p className="dato__etiqueta">Cargos</p><p className="saldo__valor">{formatearPesos(f.totalCargos)}</p></div>
              <div><p className="dato__etiqueta">Pagos</p><p className="saldo__valor">{formatearPesos(f.totalPagos)}</p></div>
              <div>
                <p className="dato__etiqueta">{f.saldo < 0 ? 'Saldo a tu favor' : 'Saldo pendiente'}</p>
                <p className={`saldo__valor ${f.saldo > 0 ? 'saldo__valor--deuda' : f.saldo < 0 ? 'saldo__valor--favor' : ''}`}>{formatearPesos(Math.abs(f.saldo))}</p>
              </div>
            </div>
          </>
        )}
      </Tarjeta>

      <Modal abierto={modal} titulo="Cancelar reserva" alCerrar={() => setModal(false)}
        pie={<>
          <Boton variante="fantasma" onClick={() => setModal(false)}>Volver</Boton>
          <Boton variante="peligro" cargando={enviando} disabled={!motivo.trim()} onClick={cancelar}>Cancelar reserva</Boton>
        </>}>
        <p>La retención y la devolución se calculan con la política vigente cuando hiciste la reserva, según la antelación.</p>
        <Campo etiqueta="¿Por qué cancelas?">
          {(p) => <textarea {...p} value={motivo} onChange={(e) => setMotivo(e.target.value)} />}
        </Campo>
      </Modal>
    </div>
  );
}

function Dato({ etiqueta, valor }) {
  return <div><p className="dato__etiqueta">{etiqueta}</p><p className="dato__valor">{valor}</p></div>;
}

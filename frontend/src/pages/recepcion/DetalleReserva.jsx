import { useState } from 'react';
import { Link, useParams } from 'react-router';
import { ArrowLeft, CheckCircle2, DoorOpen, LogOut, UserX, XCircle, Receipt } from 'lucide-react';
import Tarjeta from '../../components/Tarjeta';
import Boton from '../../components/Boton';
import ChipEstado from '../../components/ChipEstado';
import Esqueleto from '../../components/Esqueleto';
import EstadoVacio from '../../components/EstadoVacio';
import Modal from '../../components/Modal';
import Campo from '../../components/Campo';
import { useToast } from '../../components/Toasts';
import { useAuth } from '../../auth/AuthContext';
import useConsulta from '../../hooks/useConsulta';
import useApartamentos from '../../hooks/useApartamentos';
import {
  cancelarReserva, confirmarReserva, consultarReserva, declararNoShow, registrarLlegada, registrarSalida,
} from '../../api/reservas';
import { formatearFecha, formatearFechaHora, formatearHora, formatearPesos } from '../../utils/formato';
import { ACCIONES_POR_ESTADO, CANALES } from '../../utils/negocio';
import './recepcion.css';

/**
 * Detalle de una reserva con las acciones que permite su estado (sección 8): confirmar, registrar llegada,
 * registrar salida, cancelar y no-show. Si el dominio rechaza la acción, se muestra su mensaje (409).
 */
export default function DetalleReserva() {
  const { codigo } = useParams();
  const { usuario } = useAuth();
  const toast = useToast();
  const { nombre } = useApartamentos();
  const { datos: reserva, cargando, error, recargar } = useConsulta(() => consultarReserva(codigo), [codigo]);
  const [enCurso, setEnCurso] = useState(null);
  const [modal, setModal] = useState(null);
  const [motivo, setMotivo] = useState('');

  async function ejecutar(accion, funcion, exito) {
    setEnCurso(accion);
    try {
      const resultado = await funcion();
      toast.exito(exito(resultado));
      setModal(null);
      setMotivo('');
      await recargar();
    } catch (e) {
      toast.error(e);
    } finally {
      setEnCurso(null);
    }
  }

  if (cargando && !reserva) return <div className="pagina"><Tarjeta><Esqueleto filas={6} /></Tarjeta></div>;
  if (error) {
    return (
      <div className="pagina">
        <Tarjeta>
          <EstadoVacio titulo={error.status === 404 ? 'Reserva no encontrada' : 'No se pudo cargar la reserva'}>{error.message}</EstadoVacio>
          <div className="fila" style={{ justifyContent: 'center' }}>
            <Link to="/recepcion/reservas" className="boton boton--secundario">Volver a reservas</Link>
          </div>
        </Tarjeta>
      </div>
    );
  }

  const acciones = ACCIONES_POR_ESTADO[reserva.estado] ?? [];
  const liquidacion = (l) => `Retención ${formatearPesos(l.retencion)} · saldo del folio ${formatearPesos(l.saldoFolio)}`;

  return (
    <div className="pagina">
      <div>
        <nav className="migas" aria-label="Ruta"><Link to="/recepcion/reservas"><ArrowLeft size={14} /> Reservas</Link></nav>
        <div className="pagina__cabecera">
          <div className="fila" style={{ gap: 14 }}>
            <h1 className="pagina__titulo">{reserva.codigo}</h1>
            <ChipEstado estado={reserva.estado} />
          </div>
          <Link to={`/recepcion/folios/${reserva.codigo}`} className="boton boton--secundario">
            <Receipt size={18} aria-hidden="true" /> Ver folio
          </Link>
        </div>
      </div>

      {acciones.length > 0 && (
        <Tarjeta titulo="Acciones">
          <div className="acciones-reserva">
            {acciones.includes('confirmar') && (
              <Boton cargando={enCurso === 'confirmar'} onClick={() => ejecutar('confirmar', () => confirmarReserva(codigo), () => 'Reserva confirmada')}>
                <CheckCircle2 size={18} aria-hidden="true" /> Confirmar
              </Boton>
            )}
            {acciones.includes('llegada') && (
              <Boton cargando={enCurso === 'llegada'} onClick={() => ejecutar('llegada', () => registrarLlegada(codigo), () => 'Llegada registrada: el grupo ya ocupa el apartamento')}>
                <DoorOpen size={18} aria-hidden="true" /> Registrar llegada
              </Boton>
            )}
            {acciones.includes('salida') && (
              <Boton onClick={() => setModal('salida')}><LogOut size={18} aria-hidden="true" /> Registrar salida</Boton>
            )}
            {acciones.includes('cancelar') && (
              <Boton variante="peligro" onClick={() => setModal('cancelar')}><XCircle size={18} aria-hidden="true" /> Cancelar</Boton>
            )}
            {acciones.includes('noShow') && (
              <Boton variante="peligro" onClick={() => setModal('noShow')}><UserX size={18} aria-hidden="true" /> Declarar no-show</Boton>
            )}
          </div>
          {reserva.estado === 'PENDIENTE' && (
            <p className="campo__ayuda" style={{ marginTop: 12 }}>Para confirmar, el folio debe tener registrado el anticipo exigido por el alojamiento.</p>
          )}
        </Tarjeta>
      )}

      <div className="rejilla rejilla--detalle">
        <div className="pila">
          <Tarjeta titulo="Estancia">
            <div className="lista-datos">
              <Dato etiqueta="Apartamento" valor={`${nombre(reserva.identificacionApartamento)} (${reserva.identificacionApartamento})`} />
              <Dato etiqueta="Entrada" valor={formatearFecha(reserva.fechaEntrada)} />
              <Dato etiqueta="Salida" valor={formatearFecha(reserva.fechaSalida)} />
              <Dato etiqueta="Noches" valor={reserva.noches} />
              <Dato etiqueta="Hora estimada de llegada" valor={formatearHora(reserva.horaEstimadaLlegada)} />
              <Dato etiqueta="Mascotas" valor={reserva.mascotas || 'Ninguna'} />
              <Dato etiqueta="Valor congelado" valor={formatearPesos(reserva.valorTotal)} />
              <Dato etiqueta="Canal" valor={`${CANALES[reserva.canalOrigen] ?? reserva.canalOrigen}${reserva.identificadorExterno ? ` · ${reserva.identificadorExterno}` : ''}`} />
              <Dato etiqueta="Política de cancelación" valor={`Versión ${reserva.versionPoliticaCancelacion}`} />
            </div>
          </Tarjeta>
          <Tarjeta titulo={`Ocupantes (${reserva.totalOcupantes})`} sinRelleno>
            <div className="tabla-contenedor">
              <table className="tabla">
                <thead><tr><th>Nombre</th><th>Fecha de nacimiento</th></tr></thead>
                <tbody>
                  {reserva.ocupantes.map((o, i) => (
                    <tr key={`${o.nombre}-${i}`}>
                      <td>{o.nombre}{i === 0 && <span className="chip chip--neutro" style={{ marginLeft: 8 }}>Titular · {reserva.codigoTitular}</span>}</td>
                      <td>{formatearFecha(o.fechaNacimiento)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </Tarjeta>
        </div>
        <Tarjeta titulo="Historial">
          <ol className="linea-tiempo">
            <Hito texto="Reserva creada" fecha={formatearFechaHora(reserva.fechaCreacion)} />
            <Hito texto="Llegada registrada" fecha={reserva.momentoRegistro && formatearFechaHora(reserva.momentoRegistro)} />
            <Hito texto="Salida registrada" fecha={reserva.momentoSalida && formatearFechaHora(reserva.momentoSalida)} />
            {reserva.motivoCancelacion && <Hito texto="Cancelada" fecha={reserva.motivoCancelacion} />}
          </ol>
        </Tarjeta>
      </div>

      <Modal abierto={modal === 'cancelar'} titulo="Cancelar reserva" alCerrar={() => setModal(null)}
        pie={<>
          <Boton variante="fantasma" onClick={() => setModal(null)}>Volver</Boton>
          <Boton variante="peligro" cargando={enCurso === 'cancelar'} disabled={!motivo.trim()}
            onClick={() => ejecutar('cancelar', () => cancelarReserva(codigo, motivo.trim()), (l) => `Reserva cancelada · ${liquidacion(l)}`)}>
            Cancelar reserva
          </Boton>
        </>}>
        <p>La retención se calcula con la política congelada en la reserva (versión {reserva.versionPoliticaCancelacion}) y las noches se liberan de inmediato.</p>
        <Campo etiqueta="Motivo de la cancelación">
          {(p) => <textarea {...p} value={motivo} onChange={(e) => setMotivo(e.target.value)} placeholder="Ej. El titular cambió sus planes" />}
        </Campo>
      </Modal>

      <Modal abierto={modal === 'noShow'} titulo="Declarar no-show" alCerrar={() => setModal(null)}
        pie={<>
          <Boton variante="fantasma" onClick={() => setModal(null)}>Volver</Boton>
          <Boton variante="peligro" cargando={enCurso === 'noShow'}
            onClick={() => ejecutar('noShow', () => declararNoShow(codigo), (l) => `No-show declarado · ${liquidacion(l)}`)}>
            Declarar no-show
          </Boton>
        </>}>
        <p>Solo se puede declarar después de la hora límite del día de entrada. Se aplica la consecuencia de la política y se libera la disponibilidad.</p>
      </Modal>

      <Modal abierto={modal === 'salida'} titulo="Registrar salida" alCerrar={() => setModal(null)}
        pie={<>
          <Boton variante="fantasma" onClick={() => setModal(null)}>Volver</Boton>
          <Boton cargando={enCurso === 'salida'}
            onClick={() => ejecutar('salida', () => registrarSalida(codigo, motivo.trim(), usuario.username), () => 'Salida registrada: el apartamento queda pendiente de preparación')}>
            Registrar salida
          </Boton>
        </>}>
        <p>Para completar la salida, el folio debe quedar cerrado. Si queda saldo pendiente, se necesita una autorización con motivo.</p>
        <Campo etiqueta="Motivo de autorización (opcional)" ayuda="Solo si el folio tiene saldo distinto de cero.">
          {(p) => <textarea {...p} value={motivo} onChange={(e) => setMotivo(e.target.value)} />}
        </Campo>
      </Modal>
    </div>
  );
}

function Dato({ etiqueta, valor }) {
  return (
    <div>
      <p className="dato__etiqueta">{etiqueta}</p>
      <p className="dato__valor">{valor}</p>
    </div>
  );
}

function Hito({ texto, fecha }) {
  return (
    <li>
      <span className={`linea-tiempo__punto ${fecha ? '' : 'linea-tiempo__punto--apagado'}`} aria-hidden="true" />
      <div>
        <p className="linea-tiempo__texto">{texto}</p>
        <p className="linea-tiempo__fecha">{fecha || 'Pendiente'}</p>
      </div>
    </li>
  );
}

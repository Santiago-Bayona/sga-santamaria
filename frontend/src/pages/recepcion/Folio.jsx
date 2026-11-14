import { useState } from 'react';
import { Link, useParams } from 'react-router';
import { ArrowLeft, Lock, Plus, RotateCcw, Wallet } from 'lucide-react';
import Tarjeta from '../../components/Tarjeta';
import Boton from '../../components/Boton';
import Campo from '../../components/Campo';
import Modal from '../../components/Modal';
import Esqueleto from '../../components/Esqueleto';
import EstadoVacio from '../../components/EstadoVacio';
import { useToast } from '../../components/Toasts';
import { useAuth } from '../../auth/AuthContext';
import useConsulta from '../../hooks/useConsulta';
import { consultarFolio, cerrarFolio, registrarPago, registrarServicio, revertirCargo, revertirPago } from '../../api/folios';
import { consultarAlojamiento } from '../../api/alojamiento';
import { formatearFecha, formatearPesos, hoyIso } from '../../utils/formato';
import { MEDIOS_PAGO, TIPOS_CARGO } from '../../utils/negocio';
import './recepcion.css';

/**
 * Folio de la reserva (7.7): cargos, pagos y saldo. Los movimientos no se editan ni se borran:
 * una corrección es un movimiento inverso (RN-16). Cerrar con saldo distinto de cero exige autorización (RN-17).
 */
export default function Folio() {
  const { codigo } = useParams();
  const { usuario } = useAuth();
  const toast = useToast();
  const { datos: folio, cargando, error, recargar } = useConsulta(() => consultarFolio(codigo), [codigo]);
  const { datos: alojamiento } = useConsulta(consultarAlojamiento, []);
  const [modal, setModal] = useState(null);
  const [enviando, setEnviando] = useState(false);
  const [pago, setPago] = useState({ monto: '', medioPago: 'TRANSFERENCIA', fecha: hoyIso() });
  const [servicio, setServicio] = useState({ nombre: '', fecha: hoyIso() });
  const [motivoCierre, setMotivoCierre] = useState('');

  async function ejecutar(funcion, mensaje) {
    setEnviando(true);
    try {
      await funcion();
      toast.exito(mensaje);
      setModal(null);
      await recargar();
    } catch (e) {
      toast.error(e);
    } finally {
      setEnviando(false);
    }
  }

  if (cargando && !folio) return <div className="pagina"><Tarjeta><Esqueleto filas={6} /></Tarjeta></div>;
  if (error) {
    return (
      <div className="pagina">
        <Tarjeta>
          <EstadoVacio titulo="No se pudo cargar el folio">{error.message}</EstadoVacio>
        </Tarjeta>
      </div>
    );
  }

  const servicios = alojamiento?.serviciosAdicionales?.filter((s) => s.generaCargo) ?? [];
  const anticipo = alojamiento?.configuracion?.porcentajeAnticipo;
  const cargoAlojamiento = folio.cargos.find((c) => c.tipo === 'ALOJAMIENTO' && !c.esReverso)?.monto ?? 0;
  const montoAnticipo = anticipo ? Math.round((cargoAlojamiento * anticipo) / 100) : null;
  const revertidos = (lista) => new Set(lista.filter((m) => m.esReverso).map((m) => m.revierteA));
  const cargosRevertidos = revertidos(folio.cargos);
  const pagosRevertidos = revertidos(folio.pagos);
  const abierto = !folio.cerrado;

  return (
    <div className="pagina">
      <div>
        <nav className="migas" aria-label="Ruta"><Link to={`/recepcion/reservas/${codigo}`}><ArrowLeft size={14} /> Reserva {codigo}</Link></nav>
        <div className="pagina__cabecera">
          <div className="fila" style={{ gap: 14 }}>
            <h1 className="pagina__titulo">Folio {folio.codigo}</h1>
            <span className={`chip ${folio.cerrado ? 'chip--FINALIZADA' : 'chip--CONFIRMADA'}`}>{folio.cerrado ? 'Cerrado' : 'Abierto'}</span>
          </div>
          {abierto && (
            <div className="fila">
              <Boton variante="secundario" onClick={() => setModal('servicio')}><Plus size={17} aria-hidden="true" /> Agregar servicio</Boton>
              <Boton onClick={() => { setPago((p) => ({ ...p, monto: p.monto || (folio.saldo > 0 ? String(folio.saldo) : '') })); setModal('pago'); }}>
                <Wallet size={17} aria-hidden="true" /> Registrar pago
              </Boton>
            </div>
          )}
        </div>
      </div>

      <Tarjeta sinRelleno>
        <div className="saldo" style={{ borderTop: 0 }}>
          <div><p className="dato__etiqueta">Total cargos</p><p className="saldo__valor">{formatearPesos(folio.totalCargos)}</p></div>
          <div><p className="dato__etiqueta">Total pagos</p><p className="saldo__valor">{formatearPesos(folio.totalPagos)}</p></div>
          <div>
            <p className="dato__etiqueta">{folio.saldo < 0 ? 'Saldo a favor del huésped' : 'Saldo pendiente'}</p>
            <p className={`saldo__valor ${folio.saldo > 0 ? 'saldo__valor--deuda' : folio.saldo < 0 ? 'saldo__valor--favor' : ''}`}>
              {formatearPesos(Math.abs(folio.saldo))}
            </p>
          </div>
        </div>
      </Tarjeta>

      {abierto && montoAnticipo !== null && folio.totalPagos < montoAnticipo && (
        <div className="aviso aviso--info">
          El anticipo exigido es del {anticipo}% del alojamiento: {formatearPesos(montoAnticipo)}. Regístralo para poder confirmar la reserva.
        </div>
      )}
      {folio.cerrado && folio.motivoCierre && (
        <div className="aviso aviso--alerta">Cerrado con autorización de {folio.autorCierre}: {folio.motivoCierre}</div>
      )}

      <Tarjeta titulo="Cargos" sinRelleno>
        <TablaMovimientos
          filas={folio.cargos}
          columnas={['#', 'Tipo', 'Concepto', 'Fecha', 'Monto']}
          celdas={(c) => [c.numero, TIPOS_CARGO[c.tipo] ?? c.tipo, c.esReverso ? `Reverso del cargo #${c.revierteA}` : c.concepto, formatearFecha(c.fecha)]}
          revertidos={cargosRevertidos}
          puedeRevertir={abierto}
          alRevertir={(c) => ejecutar(() => revertirCargo(codigo, c.numero, hoyIso()), `Cargo #${c.numero} revertido`)}
          enviando={enviando}
        />
      </Tarjeta>

      <Tarjeta titulo="Pagos" sinRelleno>
        <TablaMovimientos
          filas={folio.pagos}
          columnas={['#', 'Medio', 'Detalle', 'Fecha', 'Monto']}
          celdas={(p) => [p.numero, MEDIOS_PAGO[p.medio] ?? p.medio, p.esReverso ? `Reverso del pago #${p.revierteA}` : 'Abono', formatearFecha(p.fecha)]}
          revertidos={pagosRevertidos}
          puedeRevertir={abierto}
          alRevertir={(p) => ejecutar(() => revertirPago(codigo, p.numero, hoyIso()), `Pago #${p.numero} revertido`)}
          enviando={enviando}
          vacio="Aún no hay pagos registrados."
        />
      </Tarjeta>

      {abierto && (
        <div className="fila" style={{ justifyContent: 'flex-end' }}>
          <Boton variante="secundario" onClick={() => setModal('cierre')}><Lock size={17} aria-hidden="true" /> Cerrar folio</Boton>
        </div>
      )}

      <Modal abierto={modal === 'pago'} titulo="Registrar pago" alCerrar={() => setModal(null)}
        pie={<>
          <Boton variante="fantasma" onClick={() => setModal(null)}>Volver</Boton>
          <Boton cargando={enviando} disabled={!(Number(pago.monto) > 0)}
            onClick={() => ejecutar(() => registrarPago(codigo, { ...pago, monto: Number(pago.monto) }), `Pago de ${formatearPesos(pago.monto)} registrado`)}>
            Registrar pago
          </Boton>
        </>}>
        {montoAnticipo !== null && folio.totalPagos < montoAnticipo && (
          <Boton variante="fantasma" tamano="pequeno" onClick={() => setPago({ ...pago, monto: String(montoAnticipo - folio.totalPagos) })}>
            Usar anticipo pendiente ({formatearPesos(montoAnticipo - folio.totalPagos)})
          </Boton>
        )}
        <Campo etiqueta="Monto (COP)">
          {(p) => <input {...p} type="number" min={1} step={1} inputMode="numeric" value={pago.monto} onChange={(e) => setPago({ ...pago, monto: e.target.value })} />}
        </Campo>
        <div className="rejilla rejilla--2">
          <Campo etiqueta="Medio de pago">
            {(p) => (
              <select {...p} value={pago.medioPago} onChange={(e) => setPago({ ...pago, medioPago: e.target.value })}>
                {Object.entries(MEDIOS_PAGO).map(([v, t]) => <option key={v} value={v}>{t}</option>)}
              </select>
            )}
          </Campo>
          <Campo etiqueta="Fecha">
            {(p) => <input {...p} type="date" value={pago.fecha} onChange={(e) => setPago({ ...pago, fecha: e.target.value })} />}
          </Campo>
        </div>
      </Modal>

      <Modal abierto={modal === 'servicio'} titulo="Agregar servicio adicional" alCerrar={() => setModal(null)}
        pie={<>
          <Boton variante="fantasma" onClick={() => setModal(null)}>Volver</Boton>
          <Boton cargando={enviando} disabled={!servicio.nombre}
            onClick={() => ejecutar(() => registrarServicio(codigo, servicio.nombre, servicio.fecha), `${servicio.nombre} agregado al folio`)}>
            Agregar cargo
          </Boton>
        </>}>
        <Campo etiqueta="Servicio">
          {(p) => (
            <select {...p} value={servicio.nombre} onChange={(e) => setServicio({ ...servicio, nombre: e.target.value })}>
              <option value="">Elige un servicio</option>
              {servicios.map((s) => <option key={s.nombre} value={s.nombre}>{s.nombre} — {formatearPesos(s.valor)}</option>)}
            </select>
          )}
        </Campo>
        <Campo etiqueta="Fecha del cargo">
          {(p) => <input {...p} type="date" value={servicio.fecha} onChange={(e) => setServicio({ ...servicio, fecha: e.target.value })} />}
        </Campo>
      </Modal>

      <Modal abierto={modal === 'cierre'} titulo="Cerrar folio" alCerrar={() => setModal(null)}
        pie={<>
          <Boton variante="fantasma" onClick={() => setModal(null)}>Volver</Boton>
          <Boton cargando={enviando} disabled={folio.saldo !== 0 && !motivoCierre.trim()}
            onClick={() => ejecutar(() => cerrarFolio(codigo, motivoCierre.trim(), usuario.username), 'Folio cerrado')}>
            Cerrar folio
          </Boton>
        </>}>
        {folio.saldo === 0 ? (
          <p>El saldo está en cero. Al cerrar, el folio ya no admite cargos ni pagos.</p>
        ) : (
          <>
            <div className="aviso aviso--alerta">
              El folio tiene {folio.saldo > 0 ? 'un saldo pendiente' : 'un saldo a favor'} de {formatearPesos(Math.abs(folio.saldo))}.
              Cerrarlo así exige autorización del administrador, que queda registrada con autor y motivo.
            </div>
            <Campo etiqueta="Motivo de la autorización">
              {(p) => <textarea {...p} value={motivoCierre} onChange={(e) => setMotivoCierre(e.target.value)} />}
            </Campo>
          </>
        )}
      </Modal>
    </div>
  );
}

function TablaMovimientos({ filas, columnas, celdas, revertidos, puedeRevertir, alRevertir, enviando, vacio = 'Sin movimientos.' }) {
  if (!filas.length) return <EstadoVacio titulo="Sin movimientos">{vacio}</EstadoVacio>;
  return (
    <div className="tabla-contenedor">
      <table className="tabla">
        <thead>
          <tr>
            {columnas.map((c, i) => <th key={c} className={i === columnas.length - 1 ? 'num' : undefined}>{c}</th>)}
            {puedeRevertir && <th><span className="visualmente-oculto">Acciones</span></th>}
          </tr>
        </thead>
        <tbody>
          {filas.map((m) => {
            const anulado = revertidos.has(m.numero);
            return (
              <tr key={m.numero}>
                {celdas(m).map((v, i) => <td key={i} className={anulado ? 'tachado' : undefined}>{v}</td>)}
                <td className={`num ${anulado ? 'tachado' : ''}`}>{m.esReverso ? '−' : ''}{formatearPesos(m.monto)}</td>
                {puedeRevertir && (
                  <td className="num">
                    {!m.esReverso && !anulado && (
                      <Boton variante="fantasma" tamano="pequeno" disabled={enviando} onClick={() => alRevertir(m)}
                        aria-label={`Revertir movimiento ${m.numero}`}>
                        <RotateCcw size={15} aria-hidden="true" /> Revertir
                      </Boton>
                    )}
                  </td>
                )}
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}

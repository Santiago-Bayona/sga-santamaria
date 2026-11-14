import { useState } from 'react';
import { Ban, CalendarClock, Plus, Unlock } from 'lucide-react';
import Tarjeta from '../../components/Tarjeta';
import Boton from '../../components/Boton';
import Campo from '../../components/Campo';
import Modal from '../../components/Modal';
import Esqueleto from '../../components/Esqueleto';
import EstadoVacio from '../../components/EstadoVacio';
import Paginacion from '../../components/Paginacion';
import SelectorApartamento from '../../components/SelectorApartamento';
import { useToast } from '../../components/Toasts';
import useConsulta from '../../hooks/useConsulta';
import { levantarBloqueo, listarBloqueos, registrarBloqueo, reprogramarBloqueo } from '../../api/operacion';
import { formatearFecha, hoyIso, sumarDias } from '../../utils/formato';

/**
 * Bloqueos (7.3): decisión administrativa que impide vender un apartamento en un rango de fechas.
 * No se puede bloquear sobre noches con reservas activas: el dominio responde 409.
 */
export default function Bloqueos() {
  const toast = useToast();
  const [apto, setApto] = useState('APT-01');
  const [pagina, setPagina] = useState(0);
  const [modal, setModal] = useState(null);
  const [form, setForm] = useState({ fechaInicio: hoyIso(), fechaFin: sumarDias(hoyIso(), 2), motivo: '' });
  const [enviando, setEnviando] = useState(false);
  const { datos, cargando, error, recargar } = useConsulta(() => listarBloqueos(apto, pagina), [apto, pagina]);

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

  const abrirNuevo = () => { setForm({ fechaInicio: hoyIso(), fechaFin: sumarDias(hoyIso(), 2), motivo: '' }); setModal({ tipo: 'nuevo' }); };
  const abrirReprogramar = (b) => { setForm({ fechaInicio: b.fechaInicio, fechaFin: b.fechaFin, motivo: b.motivo }); setModal({ tipo: 'reprogramar', bloqueo: b }); };

  return (
    <div className="pagina">
      <div className="pagina__cabecera">
        <div>
          <h1 className="pagina__titulo">Bloqueos</h1>
          <p className="pagina__subtitulo">Un apartamento bloqueado no aparece como disponible en esas noches.</p>
        </div>
        <Boton onClick={abrirNuevo}><Plus size={18} aria-hidden="true" /> Registrar bloqueo</Boton>
      </div>
      <Tarjeta sinRelleno>
        <div style={{ padding: '18px 22px', borderBottom: '1px solid var(--borde)' }}>
          <SelectorApartamento valor={apto} alCambiar={(v) => { setApto(v); setPagina(0); }} />
        </div>
        {error && <div className="aviso aviso--error" style={{ margin: 22 }}>{error.message}</div>}
        {cargando && !datos ? <Esqueleto filas={4} /> : !datos?.contenido?.length ? (
          <EstadoVacio icono={Ban} titulo="Sin bloqueos">Este apartamento no tiene bloqueos registrados.</EstadoVacio>
        ) : (
          <div className="tabla-contenedor">
            <table className="tabla">
              <thead><tr><th>Código</th><th>Desde</th><th>Hasta</th><th>Motivo</th><th>Estado</th><th><span className="visualmente-oculto">Acciones</span></th></tr></thead>
              <tbody>
                {datos.contenido.map((b) => (
                  <tr key={b.codigo}>
                    <td>{b.codigo}</td>
                    <td>{formatearFecha(b.fechaInicio)}</td>
                    <td>{formatearFecha(b.fechaFin)}</td>
                    <td>{b.motivo}</td>
                    <td><span className={`chip ${b.vigente ? 'chip--CANCELADA' : 'chip--FINALIZADA'}`}>{b.vigente ? 'Vigente' : 'Levantado'}</span></td>
                    <td className="num">
                      {b.vigente && (
                        <div className="fila" style={{ justifyContent: 'flex-end', gap: 4 }}>
                          <Boton variante="fantasma" tamano="pequeno" onClick={() => abrirReprogramar(b)}><CalendarClock size={15} aria-hidden="true" /> Reprogramar</Boton>
                          <Boton variante="fantasma" tamano="pequeno" disabled={enviando}
                            onClick={() => ejecutar(() => levantarBloqueo(b.codigo), `Bloqueo ${b.codigo} levantado`)}><Unlock size={15} aria-hidden="true" /> Levantar</Boton>
                        </div>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
        <Paginacion pagina={datos} alCambiar={setPagina} />
      </Tarjeta>

      <Modal abierto={Boolean(modal)} titulo={modal?.tipo === 'reprogramar' ? `Reprogramar ${modal.bloqueo.codigo}` : 'Registrar bloqueo'}
        alCerrar={() => setModal(null)}
        pie={<>
          <Boton variante="fantasma" onClick={() => setModal(null)}>Volver</Boton>
          {modal?.tipo === 'reprogramar' ? (
            <Boton cargando={enviando} onClick={() => ejecutar(() => reprogramarBloqueo(modal.bloqueo.codigo, form.fechaInicio, form.fechaFin), 'Bloqueo reprogramado')}>Guardar fechas</Boton>
          ) : (
            <Boton cargando={enviando} disabled={!form.motivo.trim()}
              onClick={() => ejecutar(() => registrarBloqueo({
                codigoBloqueo: `BLQ-${Date.now().toString(36).toUpperCase()}`,
                identificacionApartamento: apto, fechaInicio: form.fechaInicio, fechaFin: form.fechaFin, motivo: form.motivo.trim(),
              }), 'Bloqueo registrado')}>Registrar bloqueo</Boton>
          )}
        </>}>
        {modal?.tipo === 'nuevo' && <SelectorApartamento id="apto-bloqueo" valor={apto} alCambiar={setApto} />}
        <div className="rejilla rejilla--2">
          <Campo etiqueta="Desde">
            {(p) => <input {...p} type="date" value={form.fechaInicio} onChange={(e) => setForm({ ...form, fechaInicio: e.target.value })} />}
          </Campo>
          <Campo etiqueta="Hasta" ayuda="Se bloquean las noches del rango.">
            {(p) => <input {...p} type="date" min={form.fechaInicio} value={form.fechaFin} onChange={(e) => setForm({ ...form, fechaFin: e.target.value })} />}
          </Campo>
        </div>
        {modal?.tipo === 'nuevo' && (
          <Campo etiqueta="Motivo">
            {(p) => <input {...p} value={form.motivo} onChange={(e) => setForm({ ...form, motivo: e.target.value })} placeholder="Ej. Reparación de plomería" />}
          </Campo>
        )}
      </Modal>
    </div>
  );
}

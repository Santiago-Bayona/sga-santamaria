import { useState } from 'react';
import { Plus, Trash2 } from 'lucide-react';
import Tarjeta from '../../components/Tarjeta';
import Boton from '../../components/Boton';
import Campo from '../../components/Campo';
import Modal from '../../components/Modal';
import Esqueleto from '../../components/Esqueleto';
import { useToast } from '../../components/Toasts';
import useConsulta from '../../hooks/useConsulta';
import useApartamentos from '../../hooks/useApartamentos';
import { crearTemporada, listarTemporadas, retirarTemporada } from '../../api/alojamiento';
import { MESES_LARGOS } from '../../utils/negocio';

const periodoTexto = (p) => `${p.diaInicio} ${MESES_LARGOS[p.mesInicio - 1].toLowerCase()} – ${p.diaFin} ${MESES_LARGOS[p.mesFin - 1].toLowerCase()}`;
const PERIODO_VACIO = { mesInicio: 1, diaInicio: 1, mesFin: 1, diaFin: 31 };

/**
 * Temporadas (7.4): no se solapan, y la base cubre todas las fechas no asignadas. Al crear una temporada
 * se definen sus tarifas para todos los apartamentos activos, porque ninguno puede quedar sin tarifa.
 */
export default function Temporadas() {
  const toast = useToast();
  const { apartamentos } = useApartamentos();
  const { datos, cargando, error, recargar } = useConsulta(listarTemporadas, []);
  const [modal, setModal] = useState(false);
  const [nueva, setNueva] = useState({ idTemporada: '', nombre: '', periodos: [PERIODO_VACIO], tarifas: {} });
  const [enviando, setEnviando] = useState(false);

  async function ejecutar(funcion, mensaje) {
    setEnviando(true);
    try { await funcion(); toast.exito(mensaje); setModal(false); await recargar(); } catch (e) { toast.error(e); } finally { setEnviando(false); }
  }
  const periodo = (i, k, v) => setNueva({ ...nueva, periodos: nueva.periodos.map((p, j) => (j === i ? { ...p, [k]: Number(v) } : p)) });
  const completa = nueva.idTemporada.trim() && nueva.nombre.trim() && apartamentos.every((a) => Number(nueva.tarifas[a.identificacion]) > 0);

  return (
    <div className="pagina">
      <div className="pagina__cabecera">
        <div>
          <h1 className="pagina__titulo">Temporadas</h1>
          <p className="pagina__subtitulo">Periodos del calendario con tarifas propias. Una estancia que cruza temporadas se liquida noche por noche.</p>
        </div>
        <Boton onClick={() => { setNueva({ idTemporada: '', nombre: '', periodos: [PERIODO_VACIO], tarifas: {} }); setModal(true); }}>
          <Plus size={18} aria-hidden="true" /> Nueva temporada
        </Boton>
      </div>
      {error && <div className="aviso aviso--error">{error.message}</div>}
      <Tarjeta sinRelleno>
        {cargando && !datos ? <Esqueleto filas={4} /> : (
          <table className="tabla">
            <thead><tr><th>Temporada</th><th>Periodos</th><th><span className="visualmente-oculto">Acciones</span></th></tr></thead>
            <tbody>
              {datos?.map((t) => (
                <tr key={t.id}>
                  <td><strong>{t.nombre}</strong> <span className="campo__ayuda">({t.id})</span> {t.base && <span className="chip chip--neutro">Base</span>}</td>
                  <td>{t.base ? 'Todas las fechas sin otra temporada' : t.periodos.map(periodoTexto).join(' · ')}</td>
                  <td className="num">
                    {!t.base && (
                      <Boton variante="fantasma" tamano="pequeno" disabled={enviando}
                        onClick={() => ejecutar(() => retirarTemporada(t.id), `Temporada ${t.nombre} retirada`)}>
                        <Trash2 size={15} aria-hidden="true" /> Retirar
                      </Boton>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </Tarjeta>

      <Modal abierto={modal} titulo="Nueva temporada" alCerrar={() => setModal(false)}
        pie={<><Boton variante="fantasma" onClick={() => setModal(false)}>Volver</Boton>
          <Boton cargando={enviando} disabled={!completa} onClick={() => ejecutar(() => crearTemporada({
            idTemporada: nueva.idTemporada.trim().toUpperCase(), nombre: nueva.nombre.trim(), base: false, periodos: nueva.periodos,
            tarifas: apartamentos.map((a) => ({ identificacionApartamento: a.identificacion, valorPorOcupante: Number(nueva.tarifas[a.identificacion]) })),
          }), 'Temporada creada')}>Crear</Boton></>}>
        <div className="rejilla rejilla--2">
          <Campo etiqueta="Código">{(p) => <input {...p} value={nueva.idTemporada} onChange={(e) => setNueva({ ...nueva, idTemporada: e.target.value })} placeholder="SEMANA_SANTA" />}</Campo>
          <Campo etiqueta="Nombre">{(p) => <input {...p} value={nueva.nombre} onChange={(e) => setNueva({ ...nueva, nombre: e.target.value })} placeholder="Semana Santa" />}</Campo>
        </div>
        <p className="campo__etiqueta">Periodos (cada año)</p>
        {nueva.periodos.map((p, i) => (
          <div key={i} className="fila" style={{ gap: 6, fontSize: 14 }}>
            Del <input className="control" style={{ width: 70 }} type="number" min={1} max={31} aria-label="Día inicial" value={p.diaInicio} onChange={(e) => periodo(i, 'diaInicio', e.target.value)} />
            <select className="control" style={{ width: 130 }} aria-label="Mes inicial" value={p.mesInicio} onChange={(e) => periodo(i, 'mesInicio', e.target.value)}>
              {MESES_LARGOS.map((m, k) => <option key={m} value={k + 1}>{m}</option>)}</select>
            al <input className="control" style={{ width: 70 }} type="number" min={1} max={31} aria-label="Día final" value={p.diaFin} onChange={(e) => periodo(i, 'diaFin', e.target.value)} />
            <select className="control" style={{ width: 130 }} aria-label="Mes final" value={p.mesFin} onChange={(e) => periodo(i, 'mesFin', e.target.value)}>
              {MESES_LARGOS.map((m, k) => <option key={m} value={k + 1}>{m}</option>)}</select>
            {nueva.periodos.length > 1 && <Boton variante="fantasma" aria-label="Quitar periodo" onClick={() => setNueva({ ...nueva, periodos: nueva.periodos.filter((_, j) => j !== i) })}><Trash2 size={16} /></Boton>}
          </div>
        ))}
        <Boton variante="fantasma" tamano="pequeno" style={{ alignSelf: 'flex-start' }} onClick={() => setNueva({ ...nueva, periodos: [...nueva.periodos, PERIODO_VACIO] })}>
          <Plus size={16} aria-hidden="true" /> Agregar periodo
        </Boton>
        <p className="campo__etiqueta">Tarifa por ocupante facturable por noche</p>
        <div className="rejilla rejilla--2" style={{ gap: 10 }}>
          {apartamentos.map((a) => (
            <Campo key={a.identificacion} etiqueta={a.nombre}>
              {(p) => <input {...p} type="number" min={1} step={1000} value={nueva.tarifas[a.identificacion] ?? ''}
                onChange={(e) => setNueva({ ...nueva, tarifas: { ...nueva.tarifas, [a.identificacion]: e.target.value } })} />}
            </Campo>
          ))}
        </div>
      </Modal>
    </div>
  );
}

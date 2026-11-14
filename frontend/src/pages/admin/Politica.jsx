import { useState } from 'react';
import { Plus, Save, Trash2 } from 'lucide-react';
import Tarjeta from '../../components/Tarjeta';
import Boton from '../../components/Boton';
import Esqueleto from '../../components/Esqueleto';
import { useToast } from '../../components/Toasts';
import useConsulta from '../../hooks/useConsulta';
import { consultarPoliticaVigente, definirPolitica } from '../../api/alojamiento';
import { formatearFechaHora } from '../../utils/formato';

/**
 * Política de cancelación (7.4): una sola vigente, versionada. Publicar una nueva conserva la anterior;
 * las reservas ya creadas siguen con la versión que tenían congelada (RN-13, RN-22).
 */
export default function Politica() {
  const toast = useToast();
  const { datos, cargando, error, recargar } = useConsulta(consultarPoliticaVigente, []);
  const [borrador, setBorrador] = useState(null);
  const [guardando, setGuardando] = useState(false);

  if (cargando && !datos) return <div className="pagina"><Tarjeta><Esqueleto filas={5} /></Tarjeta></div>;
  if (error) return <div className="pagina"><div className="aviso aviso--error">{error.message}</div></div>;

  const editar = () => setBorrador({ tramos: datos.tramos.map((t) => ({ ...t })), retencionNoShow: datos.retencionNoShow });
  const tramo = (i, k, v) => setBorrador({ ...borrador, tramos: borrador.tramos.map((t, j) => (j === i ? { ...t, [k]: v } : t)) });

  async function publicar() {
    setGuardando(true);
    try {
      await definirPolitica({
        tramos: borrador.tramos.map((t) => ({ diasAntelacionMinima: Number(t.diasAntelacionMinima), porcentajeRetencion: Number(t.porcentajeRetencion) })),
        retencionNoShow: Number(borrador.retencionNoShow),
      });
      toast.exito('Nueva versión publicada', 'Las reservas existentes conservan su versión.');
      setBorrador(null);
      await recargar();
    } catch (e) {
      toast.error(e);
    } finally {
      setGuardando(false);
    }
  }

  const tramosOrdenados = [...datos.tramos].sort((a, b) => b.diasAntelacionMinima - a.diasAntelacionMinima);

  return (
    <div className="pagina">
      <div className="pagina__cabecera">
        <div>
          <h1 className="pagina__titulo">Política de cancelación</h1>
          <p className="pagina__subtitulo">Versión {datos.version} vigente desde {formatearFechaHora(datos.creadaEn)}.</p>
        </div>
        {!borrador && <Boton onClick={editar}>Nueva versión</Boton>}
      </div>

      {!borrador ? (
        <Tarjeta titulo="Tramos vigentes" sinRelleno>
          <table className="tabla">
            <thead><tr><th>Antelación</th><th className="num">Retención del anticipo</th></tr></thead>
            <tbody>
              {tramosOrdenados.map((t) => (
                <tr key={t.diasAntelacionMinima}><td>{t.diasAntelacionMinima === 0 ? 'Menos del siguiente tramo' : `${t.diasAntelacionMinima} días o más antes de la llegada`}</td><td className="num">{t.porcentajeRetencion}%</td></tr>
              ))}
              <tr><td><strong>No-show</strong></td><td className="num"><strong>{datos.retencionNoShow}%</strong></td></tr>
            </tbody>
          </table>
        </Tarjeta>
      ) : (
        <Tarjeta titulo={`Borrador de la versión ${datos.version + 1}`} acciones={<>
          <Boton variante="fantasma" tamano="pequeno" onClick={() => setBorrador(null)}>Descartar</Boton>
          <Boton tamano="pequeno" cargando={guardando} onClick={publicar}><Save size={16} aria-hidden="true" /> Publicar</Boton>
        </>}>
          <div className="pila">
            <p className="campo__ayuda">Mínimo dos tramos. Cada tramo aplica desde esa cantidad de días de antelación.</p>
            {borrador.tramos.map((t, i) => (
              <div key={i} className="fila" style={{ alignItems: 'flex-end' }}>
                <label className="campo"><span className="campo__etiqueta">Días de antelación mínima</span>
                  <input className="control" type="number" min={0} value={t.diasAntelacionMinima} onChange={(e) => tramo(i, 'diasAntelacionMinima', e.target.value)} style={{ width: 200 }} /></label>
                <label className="campo"><span className="campo__etiqueta">Retención (%)</span>
                  <input className="control" type="number" min={0} max={100} value={t.porcentajeRetencion} onChange={(e) => tramo(i, 'porcentajeRetencion', e.target.value)} style={{ width: 160 }} /></label>
                <Boton variante="fantasma" aria-label={`Quitar tramo ${i + 1}`} disabled={borrador.tramos.length <= 2}
                  onClick={() => setBorrador({ ...borrador, tramos: borrador.tramos.filter((_, j) => j !== i) })}><Trash2 size={17} /></Boton>
              </div>
            ))}
            <Boton variante="fantasma" tamano="pequeno" style={{ alignSelf: 'flex-start' }}
              onClick={() => setBorrador({ ...borrador, tramos: [...borrador.tramos, { diasAntelacionMinima: '', porcentajeRetencion: '' }] })}>
              <Plus size={16} aria-hidden="true" /> Agregar tramo
            </Boton>
            <label className="campo"><span className="campo__etiqueta">Retención por no-show (%)</span>
              <input className="control" type="number" min={0} max={100} value={borrador.retencionNoShow} onChange={(e) => setBorrador({ ...borrador, retencionNoShow: e.target.value })} style={{ width: 160 }} /></label>
          </div>
        </Tarjeta>
      )}
    </div>
  );
}

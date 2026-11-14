import { useState } from 'react';
import { ClipboardX, Plus } from 'lucide-react';
import Tarjeta from '../../components/Tarjeta';
import Boton from '../../components/Boton';
import Campo from '../../components/Campo';
import Modal from '../../components/Modal';
import Esqueleto from '../../components/Esqueleto';
import EstadoVacio from '../../components/EstadoVacio';
import Paginacion from '../../components/Paginacion';
import SelectorApartamento from '../../components/SelectorApartamento';
import { useToast } from '../../components/Toasts';
import { useAuth } from '../../auth/AuthContext';
import useConsulta from '../../hooks/useConsulta';
import { listarNovedades, registrarNovedad } from '../../api/operacion';
import { formatearFechaHora } from '../../utils/formato';
import { GRAVEDADES } from '../../utils/negocio';

/** Novedades de un apartamento (7.6): daños, faltantes o situaciones, con fecha, autor, descripción y gravedad. */
export default function Novedades() {
  const { usuario } = useAuth();
  const toast = useToast();
  const [apto, setApto] = useState('APT-01');
  const [pagina, setPagina] = useState(0);
  const [modal, setModal] = useState(false);
  const [nueva, setNueva] = useState({ descripcion: '', gravedad: 'MEDIA' });
  const [enviando, setEnviando] = useState(false);
  const puedeRegistrar = ['SERVICIO', 'ADMIN'].includes(usuario.rol);
  const { datos, cargando, error, recargar } = useConsulta(() => listarNovedades(apto, pagina), [apto, pagina]);

  async function guardar() {
    setEnviando(true);
    try {
      await registrarNovedad({
        codigoNovedad: `NOV-${Date.now().toString(36).toUpperCase()}`,
        identificacionApartamento: apto,
        descripcion: nueva.descripcion.trim(),
        gravedad: nueva.gravedad,
      }, usuario.username);
      toast.exito('Novedad registrada');
      setModal(false);
      setNueva({ descripcion: '', gravedad: 'MEDIA' });
      await recargar();
    } catch (e) {
      toast.error(e);
    } finally {
      setEnviando(false);
    }
  }

  return (
    <div className="pagina">
      <div className="pagina__cabecera">
        <div>
          <h1 className="pagina__titulo">Novedades</h1>
          <p className="pagina__subtitulo">Daños, faltantes o situaciones reportadas en cada apartamento.</p>
        </div>
        {puedeRegistrar && <Boton onClick={() => setModal(true)}><Plus size={18} aria-hidden="true" /> Registrar novedad</Boton>}
      </div>
      <Tarjeta sinRelleno>
        <div style={{ padding: '18px 22px', borderBottom: '1px solid var(--borde)' }}>
          <SelectorApartamento valor={apto} alCambiar={(v) => { setApto(v); setPagina(0); }} />
        </div>
        {error && <div className="aviso aviso--error" style={{ margin: 22 }}>{error.message}</div>}
        {cargando && !datos ? <Esqueleto filas={4} /> : !datos?.contenido?.length ? (
          <EstadoVacio icono={ClipboardX} titulo="Sin novedades">Este apartamento no tiene novedades registradas.</EstadoVacio>
        ) : (
          <div className="tabla-contenedor">
            <table className="tabla">
              <thead><tr><th>Fecha</th><th>Gravedad</th><th>Descripción</th><th>Autor</th><th>Estado</th></tr></thead>
              <tbody>
                {datos.contenido.map((n) => (
                  <tr key={n.codigo}>
                    <td style={{ whiteSpace: 'nowrap' }}>{formatearFechaHora(n.fecha)}</td>
                    <td><span className={`chip chip--${n.gravedad}`}>{GRAVEDADES[n.gravedad]}</span></td>
                    <td>{n.descripcion}</td>
                    <td>{n.autor}</td>
                    <td>{n.resuelta ? `Resuelta ${formatearFechaHora(n.fechaResolucion)}` : 'Abierta'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
        <Paginacion pagina={datos} alCambiar={setPagina} />
      </Tarjeta>

      <Modal abierto={modal} titulo="Registrar novedad" alCerrar={() => setModal(false)}
        pie={<><Boton variante="fantasma" onClick={() => setModal(false)}>Volver</Boton>
          <Boton cargando={enviando} disabled={!nueva.descripcion.trim()} onClick={guardar}>Registrar</Boton></>}>
        <SelectorApartamento id="apto-novedad" valor={apto} alCambiar={setApto} />
        <Campo etiqueta="Gravedad">
          {(p) => (
            <select {...p} value={nueva.gravedad} onChange={(e) => setNueva({ ...nueva, gravedad: e.target.value })}>
              {Object.entries(GRAVEDADES).map(([v, t]) => <option key={v} value={v}>{t}</option>)}
            </select>
          )}
        </Campo>
        <Campo etiqueta="Descripción">
          {(p) => <textarea {...p} value={nueva.descripcion} onChange={(e) => setNueva({ ...nueva, descripcion: e.target.value })} placeholder="Ej. Fuga en la llave del lavaplatos" />}
        </Campo>
      </Modal>
    </div>
  );
}

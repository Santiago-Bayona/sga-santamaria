import { useState } from 'react';
import { useNavigate } from 'react-router';
import { Plus } from 'lucide-react';
import Tarjeta from '../../components/Tarjeta';
import Boton from '../../components/Boton';
import Campo from '../../components/Campo';
import Modal from '../../components/Modal';
import Esqueleto from '../../components/Esqueleto';
import Paginacion from '../../components/Paginacion';
import { useToast } from '../../components/Toasts';
import useConsulta from '../../hooks/useConsulta';
import { listarApartamentos } from '../../api/alojamiento';
import { crearApartamento } from '../../api/apartamentos';
import { ESTADOS_OPERATIVOS } from '../../utils/negocio';

const VACIO = { identificacionApartamento: '', nombre: '', descripcion: '', dormitorios: 1, capacidad: 2, imagenes: '', dotacion: '' };
const lineas = (texto) => texto.split(/\n|,/).map((l) => l.trim()).filter(Boolean);

/** Gestión de apartamentos (7.3): listado de los activos y creación. Un apartamento nuevo nace inactivo hasta tener tarifas completas. */
export default function Apartamentos() {
  const toast = useToast();
  const navegar = useNavigate();
  const [pagina, setPagina] = useState(0);
  const [modal, setModal] = useState(false);
  const [form, setForm] = useState(VACIO);
  const [enviando, setEnviando] = useState(false);
  const { datos, cargando, error } = useConsulta(() => listarApartamentos(pagina), [pagina]);
  const cambiar = (k) => (e) => setForm({ ...form, [k]: e.target.value });

  async function crear() {
    setEnviando(true);
    try {
      const a = await crearApartamento({
        identificacionApartamento: form.identificacionApartamento.trim().toUpperCase(),
        nombre: form.nombre.trim(), descripcion: form.descripcion.trim(),
        dormitorios: Number(form.dormitorios), capacidad: Number(form.capacidad),
        imagenes: lineas(form.imagenes), indiceImagenPrincipal: 0, dotacion: lineas(form.dotacion),
      });
      toast.exito('Apartamento creado', 'Define sus tarifas en todas las temporadas para poder activarlo.');
      navegar(`/admin/apartamentos/${a.identificacion}`);
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
          <h1 className="pagina__titulo">Apartamentos</h1>
          <p className="pagina__subtitulo">Inventario vendible. Los retirados no aparecen en listados ni búsquedas.</p>
        </div>
        <Boton onClick={() => { setForm(VACIO); setModal(true); }}><Plus size={18} aria-hidden="true" /> Nuevo apartamento</Boton>
      </div>
      {error && <div className="aviso aviso--error">{error.message}</div>}
      <Tarjeta sinRelleno>
        {cargando && !datos ? <Esqueleto filas={5} /> : (
          <div className="tabla-contenedor">
            <table className="tabla">
              <thead><tr><th>Código</th><th>Nombre</th><th className="num">Dormitorios</th><th className="num">Capacidad</th><th>Estado operativo</th></tr></thead>
              <tbody>
                {datos?.contenido.map((a) => (
                  <tr key={a.identificacion} className="tabla__fila--enlace" onClick={() => navegar(`/admin/apartamentos/${a.identificacion}`)}>
                    <td><button type="button" className="enlace-tabla" onClick={() => navegar(`/admin/apartamentos/${a.identificacion}`)}>{a.identificacion}</button></td>
                    <td>{a.nombre}</td>
                    <td className="num">{a.dormitorios}</td>
                    <td className="num">{a.capacidad}</td>
                    <td><span className={`chip chip--${a.estadoOperativo}`}>{ESTADOS_OPERATIVOS[a.estadoOperativo]}</span></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
        <Paginacion pagina={datos} alCambiar={setPagina} />
      </Tarjeta>

      <Modal abierto={modal} titulo="Nuevo apartamento" alCerrar={() => setModal(false)}
        pie={<><Boton variante="fantasma" onClick={() => setModal(false)}>Volver</Boton>
          <Boton cargando={enviando} disabled={!form.identificacionApartamento.trim() || !form.nombre.trim() || !lineas(form.imagenes).length} onClick={crear}>Crear</Boton></>}>
        <div className="rejilla rejilla--2">
          <Campo etiqueta="Código">{(p) => <input {...p} value={form.identificacionApartamento} onChange={cambiar('identificacionApartamento')} placeholder="APT-06" />}</Campo>
          <Campo etiqueta="Nombre">{(p) => <input {...p} value={form.nombre} onChange={cambiar('nombre')} placeholder="Cafeto" />}</Campo>
          <Campo etiqueta="Dormitorios">{(p) => <input {...p} type="number" min={1} value={form.dormitorios} onChange={cambiar('dormitorios')} />}</Campo>
          <Campo etiqueta="Capacidad">{(p) => <input {...p} type="number" min={1} value={form.capacidad} onChange={cambiar('capacidad')} />}</Campo>
        </div>
        <Campo etiqueta="Descripción">{(p) => <textarea {...p} value={form.descripcion} onChange={cambiar('descripcion')} />}</Campo>
        <Campo etiqueta="Imágenes" ayuda="Una URL por línea (de 1 a 10). La primera es la principal.">
          {(p) => <textarea {...p} value={form.imagenes} onChange={cambiar('imagenes')} placeholder="https://…/cafeto-1.jpg" />}
        </Campo>
        <Campo etiqueta="Dotación" ayuda="Separada por comas.">{(p) => <input {...p} value={form.dotacion} onChange={cambiar('dotacion')} placeholder="Cocina, 2 baños, WiFi" />}</Campo>
      </Modal>
    </div>
  );
}

import { useState } from 'react';
import { Link, useNavigate } from 'react-router';
import { ArrowLeft, Plus, Trash2, UserPlus } from 'lucide-react';
import Tarjeta from '../../components/Tarjeta';
import Boton from '../../components/Boton';
import Campo from '../../components/Campo';
import Modal from '../../components/Modal';
import { useToast } from '../../components/Toasts';
import useApartamentos from '../../hooks/useApartamentos';
import { crearReserva } from '../../api/reservas';
import { registrarTitular } from '../../api/titulares';
import { aHoraJava, hoyIso, sumarDias } from '../../utils/formato';
import './recepcion.css';

const INICIAL = {
  codigoTitular: '', identificacionApartamento: '', fechaEntrada: '', fechaSalida: '',
  horaLlegadaEstimada: '15:00', mascotas: 0, acompanantes: [],
};

/** Detalles del 400 ("campo: mensaje") → { campo: mensaje }. */
const porCampo = (detalles = []) => Object.fromEntries(detalles.map((d) => d.split(/:\s(.+)/)).filter(([c, m]) => c && m));

/**
 * Reserva por canal DIRECTO (7.1): recepción la registra a nombre de un titular que puede no tener cuenta.
 * El frontend solo valida la forma; las seis validaciones de creación (7.5) las hace el dominio.
 */
export default function NuevaReserva() {
  const { apartamentos } = useApartamentos();
  const toast = useToast();
  const navegar = useNavigate();
  const [form, setForm] = useState(INICIAL);
  const [errores, setErrores] = useState({});
  const [enviando, setEnviando] = useState(false);
  const [modalTitular, setModalTitular] = useState(false);

  const apto = apartamentos.find((a) => a.identificacion === form.identificacionApartamento);
  const totalPersonas = 1 + form.acompanantes.length;
  const cambiar = (campo) => (e) => setForm({ ...form, [campo]: e.target.value });

  function cambiarAcompanante(i, campo, valor) {
    setForm({ ...form, acompanantes: form.acompanantes.map((a, j) => (j === i ? { ...a, [campo]: valor } : a)) });
  }

  function validar() {
    const e = {};
    if (!form.codigoTitular.trim()) e.codigoTitular = 'Indica el código del titular.';
    if (!form.identificacionApartamento) e.identificacionApartamento = 'Elige un apartamento.';
    if (!form.fechaEntrada) e.fechaEntrada = 'Elige la fecha de entrada.';
    if (!form.fechaSalida) e.fechaSalida = 'Elige la fecha de salida.';
    if (!form.horaLlegadaEstimada) e.horaLlegadaEstimada = 'Indica la hora estimada.';
    form.acompanantes.forEach((a, i) => {
      if (!a.nombre.trim() || !a.fechaNacimiento) e[`acompanante${i}`] = 'Nombre y fecha de nacimiento son obligatorios.';
    });
    return e;
  }

  async function enviar(evento) {
    evento.preventDefault();
    const nuevos = validar();
    setErrores(nuevos);
    if (Object.keys(nuevos).length) return;
    setEnviando(true);
    try {
      const reserva = await crearReserva({
        codigoTitular: form.codigoTitular.trim(),
        identificacionApartamento: form.identificacionApartamento,
        fechaEntrada: form.fechaEntrada,
        fechaSalida: form.fechaSalida,
        acompanantes: form.acompanantes.map((a) => ({ nombre: a.nombre.trim(), fechaNacimiento: a.fechaNacimiento })),
        horaLlegadaEstimada: aHoraJava(form.horaLlegadaEstimada),
        canalOrigen: 'DIRECTO',
        mascotas: Number(form.mascotas) || 0,
      });
      toast.exito('Reserva creada', `${reserva.codigo} quedó PENDIENTE. Registra el anticipo para confirmarla.`);
      navegar(`/recepcion/reservas/${reserva.codigo}`);
    } catch (e) {
      if (e.status === 400) setErrores(porCampo(e.detalles));
      toast.error(e);
    } finally {
      setEnviando(false);
    }
  }

  return (
    <div className="pagina">
      <div>
        <nav className="migas" aria-label="Ruta"><Link to="/recepcion/reservas"><ArrowLeft size={14} /> Reservas</Link></nav>
        <h1 className="pagina__titulo">Nueva reserva</h1>
        <p className="pagina__subtitulo">Canal directo: reserva recibida por teléfono, mensajería o en persona.</p>
      </div>

      <form className="pila" onSubmit={enviar} noValidate>
        <Tarjeta titulo="Titular" acciones={
          <Boton variante="fantasma" tamano="pequeno" onClick={() => setModalTitular(true)}>
            <UserPlus size={16} aria-hidden="true" /> Registrar titular nuevo
          </Boton>
        }>
          <Campo etiqueta="Código del titular" error={errores.codigoTitular}
            ayuda="El titular es siempre un ocupante facturable. Si no existe, regístralo primero.">
            {(p) => <input {...p} value={form.codigoTitular} onChange={cambiar('codigoTitular')} placeholder="Ej. TIT-002" style={{ maxWidth: 320 }} />}
          </Campo>
        </Tarjeta>

        <Tarjeta titulo="Estancia">
          <div className="rejilla rejilla--2">
            <Campo etiqueta="Apartamento" error={errores.identificacionApartamento}
              ayuda={apto ? `${apto.dormitorios} dormitorio(s) · capacidad ${apto.capacidad} personas` : undefined}>
              {(p) => (
                <select {...p} value={form.identificacionApartamento} onChange={cambiar('identificacionApartamento')}>
                  <option value="">Elige un apartamento</option>
                  {apartamentos.map((a) => (
                    <option key={a.identificacion} value={a.identificacion}>{a.nombre} — hasta {a.capacidad} personas</option>
                  ))}
                </select>
              )}
            </Campo>
            <Campo etiqueta="Hora estimada de llegada" error={errores.horaLlegadaEstimada}>
              {(p) => <input {...p} type="time" value={form.horaLlegadaEstimada} onChange={cambiar('horaLlegadaEstimada')} />}
            </Campo>
            <Campo etiqueta="Fecha de entrada" error={errores.fechaEntrada}>
              {(p) => <input {...p} type="date" min={hoyIso()} value={form.fechaEntrada}
                onChange={(e) => setForm({ ...form, fechaEntrada: e.target.value, fechaSalida: form.fechaSalida || (e.target.value && sumarDias(e.target.value, 1)) })} />}
            </Campo>
            <Campo etiqueta="Fecha de salida" error={errores.fechaSalida} ayuda="La noche de la salida no se cobra.">
              {(p) => <input {...p} type="date" min={form.fechaEntrada ? sumarDias(form.fechaEntrada, 1) : hoyIso()} value={form.fechaSalida} onChange={cambiar('fechaSalida')} />}
            </Campo>
            <Campo etiqueta="Mascotas" ayuda="Máximo 2 por apartamento, con cargo por estancia.">
              {(p) => <input {...p} type="number" min={0} max={9} value={form.mascotas} onChange={cambiar('mascotas')} style={{ maxWidth: 140 }} />}
            </Campo>
          </div>
        </Tarjeta>

        <Tarjeta titulo={`Acompañantes · ${totalPersonas} persona${totalPersonas === 1 ? '' : 's'} en total`} acciones={
          <Boton variante="secundario" tamano="pequeno"
            onClick={() => setForm({ ...form, acompanantes: [...form.acompanantes, { nombre: '', fechaNacimiento: '' }] })}>
            <Plus size={16} aria-hidden="true" /> Agregar acompañante
          </Boton>
        }>
          {form.acompanantes.length === 0 ? (
            <p className="campo__ayuda">Solo viaja el titular. Agrega a cada acompañante con su fecha de nacimiento: la edad define si es facturable.</p>
          ) : (
            <div className="pila">
              {form.acompanantes.map((a, i) => (
                <div key={i}>
                  <div className="ocupante">
                    <Campo etiqueta={`Acompañante ${i + 1}`}>
                      {(p) => <input {...p} value={a.nombre} onChange={(e) => cambiarAcompanante(i, 'nombre', e.target.value)} placeholder="Nombre completo" />}
                    </Campo>
                    <Campo etiqueta="Fecha de nacimiento">
                      {(p) => <input {...p} type="date" max={hoyIso()} value={a.fechaNacimiento} onChange={(e) => cambiarAcompanante(i, 'fechaNacimiento', e.target.value)} />}
                    </Campo>
                    <Boton variante="fantasma" aria-label={`Quitar acompañante ${i + 1}`}
                      onClick={() => setForm({ ...form, acompanantes: form.acompanantes.filter((_, j) => j !== i) })}>
                      <Trash2 size={18} />
                    </Boton>
                  </div>
                  {errores[`acompanante${i}`] && <p className="campo__error" style={{ marginTop: 6 }}>{errores[`acompanante${i}`]}</p>}
                </div>
              ))}
            </div>
          )}
          {apto && totalPersonas > apto.capacidad && (
            <div className="aviso aviso--alerta" style={{ marginTop: 16 }}>
              {apto.nombre} admite hasta {apto.capacidad} personas y la reserva tiene {totalPersonas}. El sistema la rechazará (RN-02).
            </div>
          )}
        </Tarjeta>

        <div className="fila" style={{ justifyContent: 'flex-end' }}>
          <Link to="/recepcion/reservas" className="boton boton--fantasma">Cancelar</Link>
          <Boton type="submit" cargando={enviando}>Crear reserva</Boton>
        </div>
      </form>

      <ModalTitular abierto={modalTitular} alCerrar={() => setModalTitular(false)}
        alCrear={(codigo) => { setForm((f) => ({ ...f, codigoTitular: codigo })); setModalTitular(false); }} />
    </div>
  );
}

/** Registro de un titular sin cuenta (3.6): recepción crea al responsable de la reserva. */
function ModalTitular({ abierto, alCerrar, alCrear }) {
  const toast = useToast();
  const [t, setT] = useState({ codigoTitular: '', nombre: '', fechaNacimiento: '', telefono: '', correo: '' });
  const [errores, setErrores] = useState({});
  const [enviando, setEnviando] = useState(false);
  const cambiar = (c) => (e) => setT({ ...t, [c]: e.target.value });

  async function guardar() {
    setEnviando(true);
    setErrores({});
    try {
      const titular = await registrarTitular({ ...t, codigoTitular: t.codigoTitular.trim() });
      toast.exito('Titular registrado', `${titular.nombre} (${titular.codigo})`);
      alCrear(titular.codigo);
    } catch (e) {
      if (e.status === 400) setErrores(porCampo(e.detalles));
      toast.error(e);
    } finally {
      setEnviando(false);
    }
  }

  return (
    <Modal abierto={abierto} titulo="Registrar titular" alCerrar={alCerrar}
      pie={<><Boton variante="fantasma" onClick={alCerrar}>Volver</Boton><Boton cargando={enviando} onClick={guardar}>Registrar</Boton></>}>
      <Campo etiqueta="Código del titular" error={errores.codigoTitular} ayuda="Ej. TIT- seguido del documento: TIT-1094000222">
        {(p) => <input {...p} value={t.codigoTitular} onChange={cambiar('codigoTitular')} />}
      </Campo>
      <Campo etiqueta="Nombre completo" error={errores.nombre}>
        {(p) => <input {...p} value={t.nombre} onChange={cambiar('nombre')} />}
      </Campo>
      <div className="rejilla rejilla--2">
        <Campo etiqueta="Fecha de nacimiento" error={errores.fechaNacimiento}>
          {(p) => <input {...p} type="date" max={hoyIso()} value={t.fechaNacimiento} onChange={cambiar('fechaNacimiento')} />}
        </Campo>
        <Campo etiqueta="Teléfono" error={errores.telefono}>
          {(p) => <input {...p} type="tel" value={t.telefono} onChange={cambiar('telefono')} />}
        </Campo>
      </div>
      <Campo etiqueta="Correo" error={errores.correo}>
        {(p) => <input {...p} type="email" value={t.correo} onChange={cambiar('correo')} />}
      </Campo>
    </Modal>
  );
}

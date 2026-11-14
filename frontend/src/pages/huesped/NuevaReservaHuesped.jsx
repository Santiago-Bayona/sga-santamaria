import { useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router';
import { ArrowLeft, Plus, Trash2 } from 'lucide-react';
import Tarjeta from '../../components/Tarjeta';
import Boton from '../../components/Boton';
import Campo from '../../components/Campo';
import { useToast } from '../../components/Toasts';
import { useAuth } from '../../auth/AuthContext';
import useApartamentos from '../../hooks/useApartamentos';
import { crearReserva } from '../../api/reservas';
import { aHoraJava, hoyIso, sumarDias } from '../../utils/formato';
import AvisoSinTitular from './AvisoSinTitular';
import '../recepcion/recepcion.css';

/**
 * Reserva por el canal PORTAL (7.1): el huésped reserva a su propio nombre. El titular es él mismo
 * (su fecha de nacimiento ya está registrada), así que aquí solo se piden los acompañantes.
 */
export default function NuevaReservaHuesped() {
  const [parametros] = useSearchParams();
  const { usuario } = useAuth();
  const { apartamentos } = useApartamentos();
  const toast = useToast();
  const navegar = useNavigate();
  const adultos = Math.max(1, Number(parametros.get('adultos') || 1));
  const [form, setForm] = useState({
    identificacionApartamento: parametros.get('apto') ?? '',
    fechaEntrada: parametros.get('entrada') ?? '',
    fechaSalida: parametros.get('salida') ?? '',
    horaLlegadaEstimada: '15:00',
    mascotas: 0,
    acompanantes: [
      ...Array.from({ length: adultos - 1 }, () => ({ nombre: '', fechaNacimiento: '' })),
      ...parametros.getAll('nino').map((fechaNacimiento) => ({ nombre: '', fechaNacimiento })),
    ],
  });
  const [errores, setErrores] = useState({});
  const [enviando, setEnviando] = useState(false);

  if (!usuario.codigoTitular) return <div className="pagina"><AvisoSinTitular /></div>;

  const apto = apartamentos.find((a) => a.identificacion === form.identificacionApartamento);
  const total = 1 + form.acompanantes.length;
  const cambiar = (c) => (e) => setForm({ ...form, [c]: e.target.value });
  const cambiarAcompanante = (i, c, v) => setForm({ ...form, acompanantes: form.acompanantes.map((a, j) => (j === i ? { ...a, [c]: v } : a)) });

  async function enviar(e) {
    e.preventDefault();
    const nuevos = {};
    if (!form.identificacionApartamento) nuevos.apto = 'Elige un apartamento.';
    if (!form.fechaEntrada) nuevos.entrada = 'Elige la fecha de llegada.';
    if (!form.fechaSalida || form.fechaSalida <= form.fechaEntrada) nuevos.salida = 'Debe ser posterior a la llegada.';
    if (form.acompanantes.some((a) => !a.nombre.trim() || !a.fechaNacimiento)) nuevos.acompanantes = 'Completa el nombre y la fecha de nacimiento de cada acompañante.';
    setErrores(nuevos);
    if (Object.keys(nuevos).length) return;
    setEnviando(true);
    try {
      const reserva = await crearReserva({
        codigoTitular: usuario.codigoTitular,
        identificacionApartamento: form.identificacionApartamento,
        fechaEntrada: form.fechaEntrada,
        fechaSalida: form.fechaSalida,
        acompanantes: form.acompanantes.map((a) => ({ nombre: a.nombre.trim(), fechaNacimiento: a.fechaNacimiento })),
        horaLlegadaEstimada: aHoraJava(form.horaLlegadaEstimada),
        canalOrigen: 'PORTAL',
        mascotas: Number(form.mascotas) || 0,
      });
      toast.exito('¡Reserva creada!', `${reserva.codigo} está pendiente hasta que se registre el anticipo.`);
      navegar(`/huesped/reservas/${reserva.codigo}`);
    } catch (error) {
      toast.error(error);
    } finally {
      setEnviando(false);
    }
  }

  return (
    <div className="pagina">
      <div>
        <nav className="migas" aria-label="Ruta"><Link to="/huesped"><ArrowLeft size={14} /> Mis reservas</Link></nav>
        <h1 className="pagina__titulo">Confirma tu reserva</h1>
        <p className="pagina__subtitulo">La reserva queda a tu nombre como titular.</p>
      </div>
      <form className="pila" onSubmit={enviar} noValidate>
        <Tarjeta titulo="Tu estancia">
          <div className="rejilla rejilla--2">
            <Campo etiqueta="Apartamento" error={errores.apto} ayuda={apto ? `Hasta ${apto.capacidad} personas` : undefined}>
              {(p) => (
                <select {...p} value={form.identificacionApartamento} onChange={cambiar('identificacionApartamento')}>
                  <option value="">Elige un apartamento</option>
                  {apartamentos.map((a) => <option key={a.identificacion} value={a.identificacion}>{a.nombre}</option>)}
                </select>
              )}
            </Campo>
            <Campo etiqueta="Hora estimada de llegada" ayuda="Obligatoria para confirmar (RN-09).">
              {(p) => <input {...p} type="time" value={form.horaLlegadaEstimada} onChange={cambiar('horaLlegadaEstimada')} />}
            </Campo>
            <Campo etiqueta="Llegada" error={errores.entrada}>
              {(p) => <input {...p} type="date" min={hoyIso()} value={form.fechaEntrada} onChange={cambiar('fechaEntrada')} />}
            </Campo>
            <Campo etiqueta="Salida" error={errores.salida}>
              {(p) => <input {...p} type="date" min={sumarDias(form.fechaEntrada || hoyIso(), 1)} value={form.fechaSalida} onChange={cambiar('fechaSalida')} />}
            </Campo>
            <Campo etiqueta="Mascotas" ayuda="Máximo 2, con un cargo por estancia.">
              {(p) => <input {...p} type="number" min={0} max={9} value={form.mascotas} onChange={cambiar('mascotas')} style={{ maxWidth: 140 }} />}
            </Campo>
          </div>
        </Tarjeta>

        <Tarjeta titulo={`Quiénes viajan · ${total} persona${total === 1 ? '' : 's'}`} acciones={
          <Boton variante="secundario" tamano="pequeno" onClick={() => setForm({ ...form, acompanantes: [...form.acompanantes, { nombre: '', fechaNacimiento: '' }] })}>
            <Plus size={16} aria-hidden="true" /> Agregar acompañante
          </Boton>
        }>
          <div className="pila">
            <p className="campo__ayuda">Tú ({usuario.nombre}) viajas como titular. Agrega a cada acompañante con su fecha de nacimiento.</p>
            {form.acompanantes.map((a, i) => (
              <div key={i} className="ocupante">
                <Campo etiqueta={`Acompañante ${i + 1}`}>
                  {(p) => <input {...p} value={a.nombre} onChange={(e) => cambiarAcompanante(i, 'nombre', e.target.value)} placeholder="Nombre completo" />}
                </Campo>
                <Campo etiqueta="Fecha de nacimiento">
                  {(p) => <input {...p} type="date" max={hoyIso()} value={a.fechaNacimiento} onChange={(e) => cambiarAcompanante(i, 'fechaNacimiento', e.target.value)} />}
                </Campo>
                <Boton variante="fantasma" aria-label={`Quitar acompañante ${i + 1}`}
                  onClick={() => setForm({ ...form, acompanantes: form.acompanantes.filter((_, j) => j !== i) })}><Trash2 size={18} /></Boton>
              </div>
            ))}
            {errores.acompanantes && <p className="campo__error">{errores.acompanantes}</p>}
            {apto && total > apto.capacidad && (
              <div className="aviso aviso--alerta">{apto.nombre} admite hasta {apto.capacidad} personas. Elige otro apartamento o reduce el grupo.</div>
            )}
          </div>
        </Tarjeta>

        <div className="aviso aviso--info">
          Tu reserva queda pendiente hasta que se registre el anticipo. Si no se confirma dentro del plazo del alojamiento, se cancela sola.
        </div>
        <div className="fila" style={{ justifyContent: 'flex-end' }}>
          <Link to="/" className="boton boton--fantasma">Volver</Link>
          <Boton type="submit" cargando={enviando}>Reservar</Boton>
        </div>
      </form>
    </div>
  );
}

import { useState } from 'react';
import { Plus, Save, Trash2 } from 'lucide-react';
import Tarjeta from '../../components/Tarjeta';
import Boton from '../../components/Boton';
import Campo from '../../components/Campo';
import Esqueleto from '../../components/Esqueleto';
import { useToast } from '../../components/Toasts';
import useConsulta from '../../hooks/useConsulta';
import { actualizarConfiguracion, actualizarNormas, agregarServicio, consultarAlojamiento } from '../../api/alojamiento';
import { aHoraJava, formatearPesos } from '../../utils/formato';

/**
 * Parámetros del alojamiento (sección 6 y 7.3). Son configuración, no constantes: el administrador los
 * cambia aquí sin recompilar. Las reglas sobre ellos (p. ej. no-show posterior a la entrada) las valida el dominio.
 */
export default function Alojamiento() {
  const consulta = useConsulta(consultarAlojamiento, []);
  if (consulta.cargando && !consulta.datos) return <div className="pagina"><Tarjeta><Esqueleto filas={8} /></Tarjeta></div>;
  if (consulta.error) return <div className="pagina"><div className="aviso aviso--error">{consulta.error.message}</div></div>;
  return <Formularios alojamiento={consulta.datos} recargar={consulta.recargar} />;
}

function Formularios({ alojamiento, recargar }) {
  const toast = useToast();
  const c = alojamiento.configuracion;
  const hora = (h) => (h ? String(h).slice(0, 5) : '');
  const [cfg, setCfg] = useState({
    horaEntrada: hora(c.horaEntrada), horaSalida: hora(c.horaSalida), horaLimiteNoShow: hora(c.horaLimiteNoShow),
    tiempoPreparacionMinutos: c.tiempoPreparacionMinutos, plazoConfirmacionHoras: c.plazoConfirmacionHoras,
    umbralEdadFacturable: c.umbralEdadFacturable, porcentajeAnticipo: c.porcentajeAnticipo,
    antelacionRecordatorioEntradaHoras: c.antelacionRecordatorioEntradaHoras ?? 24,
    antelacionRecordatorioSalidaHoras: c.antelacionRecordatorioSalidaHoras ?? 4,
  });
  const [normas, setNormas] = useState(alojamiento.normasConvivencia ?? []);
  const [servicio, setServicio] = useState({ nombre: '', valor: '' });
  const [guardando, setGuardando] = useState(null);

  async function ejecutar(clave, funcion, mensaje) {
    setGuardando(clave);
    try { await funcion(); toast.exito(mensaje); await recargar(); } catch (e) { toast.error(e); } finally { setGuardando(null); }
  }
  const numero = (k) => (e) => setCfg({ ...cfg, [k]: e.target.value });

  return (
    <div className="pagina">
      <div>
        <h1 className="pagina__titulo">{alojamiento.nombre}</h1>
        <p className="pagina__subtitulo">{alojamiento.ciudad} · {alojamiento.direccion}</p>
      </div>

      <Tarjeta titulo="Parámetros de operación" acciones={
        <Boton tamano="pequeno" cargando={guardando === 'cfg'} onClick={() => ejecutar('cfg', () => actualizarConfiguracion({
          ...cfg,
          horaEntrada: aHoraJava(cfg.horaEntrada), horaSalida: aHoraJava(cfg.horaSalida), horaLimiteNoShow: aHoraJava(cfg.horaLimiteNoShow),
          ...Object.fromEntries(['tiempoPreparacionMinutos', 'plazoConfirmacionHoras', 'umbralEdadFacturable', 'porcentajeAnticipo',
            'antelacionRecordatorioEntradaHoras', 'antelacionRecordatorioSalidaHoras'].map((k) => [k, Number(cfg[k])])),
        }), 'Parámetros actualizados')}><Save size={16} aria-hidden="true" /> Guardar</Boton>
      }>
        <div className="rejilla rejilla--3">
          <Campo etiqueta="Hora de entrada">{(p) => <input {...p} type="time" value={cfg.horaEntrada} onChange={numero('horaEntrada')} />}</Campo>
          <Campo etiqueta="Hora de salida">{(p) => <input {...p} type="time" value={cfg.horaSalida} onChange={numero('horaSalida')} />}</Campo>
          <Campo etiqueta="Hora límite de no-show" ayuda="Debe ser posterior a la entrada.">{(p) => <input {...p} type="time" value={cfg.horaLimiteNoShow} onChange={numero('horaLimiteNoShow')} />}</Campo>
          <Campo etiqueta="Tiempo de preparación (min)">{(p) => <input {...p} type="number" min={0} value={cfg.tiempoPreparacionMinutos} onChange={numero('tiempoPreparacionMinutos')} />}</Campo>
          <Campo etiqueta="Plazo de confirmación (h)">{(p) => <input {...p} type="number" min={1} value={cfg.plazoConfirmacionHoras} onChange={numero('plazoConfirmacionHoras')} />}</Campo>
          <Campo etiqueta="Umbral de edad facturable">{(p) => <input {...p} type="number" min={0} value={cfg.umbralEdadFacturable} onChange={numero('umbralEdadFacturable')} />}</Campo>
          <Campo etiqueta="Anticipo (%)">{(p) => <input {...p} type="number" min={0} max={100} value={cfg.porcentajeAnticipo} onChange={numero('porcentajeAnticipo')} />}</Campo>
          <Campo etiqueta="Recordatorio de entrada (h antes)">{(p) => <input {...p} type="number" min={0} value={cfg.antelacionRecordatorioEntradaHoras} onChange={numero('antelacionRecordatorioEntradaHoras')} />}</Campo>
          <Campo etiqueta="Recordatorio de salida (h antes)">{(p) => <input {...p} type="number" min={0} value={cfg.antelacionRecordatorioSalidaHoras} onChange={numero('antelacionRecordatorioSalidaHoras')} />}</Campo>
        </div>
      </Tarjeta>

      <div className="rejilla rejilla--2" style={{ alignItems: 'start' }}>
        <Tarjeta titulo="Normas de convivencia" acciones={
          <Boton tamano="pequeno" cargando={guardando === 'normas'} disabled={normas.some((n) => !n.trim())}
            onClick={() => ejecutar('normas', () => actualizarNormas(normas.map((n) => n.trim())), 'Normas actualizadas')}>
            <Save size={16} aria-hidden="true" /> Guardar
          </Boton>
        }>
          <div className="pila" style={{ gap: 10 }}>
            {normas.map((n, i) => (
              <div key={i} className="fila" style={{ flexWrap: 'nowrap' }}>
                <input className="control" aria-label={`Norma ${i + 1}`} value={n} onChange={(e) => setNormas(normas.map((x, j) => (j === i ? e.target.value : x)))} />
                <Boton variante="fantasma" aria-label={`Quitar norma ${i + 1}`} onClick={() => setNormas(normas.filter((_, j) => j !== i))}><Trash2 size={17} /></Boton>
              </div>
            ))}
            <Boton variante="fantasma" tamano="pequeno" style={{ alignSelf: 'flex-start' }} onClick={() => setNormas([...normas, ''])}>
              <Plus size={16} aria-hidden="true" /> Agregar norma
            </Boton>
          </div>
        </Tarjeta>

        <Tarjeta titulo="Servicios adicionales">
          <ul style={{ listStyle: 'none', margin: '0 0 18px', padding: 0, display: 'grid', gap: 8 }}>
            {alojamiento.serviciosAdicionales.map((s) => (
              <li key={s.nombre} className="fila fila--entre"><span>{s.nombre}</span><strong>{s.generaCargo ? formatearPesos(s.valor) : 'Sin cargo'}</strong></li>
            ))}
          </ul>
          <div className="rejilla" style={{ gridTemplateColumns: '1fr 140px auto', alignItems: 'end', gap: 10 }}>
            <Campo etiqueta="Nuevo servicio">{(p) => <input {...p} value={servicio.nombre} onChange={(e) => setServicio({ ...servicio, nombre: e.target.value })} />}</Campo>
            <Campo etiqueta="Valor">{(p) => <input {...p} type="number" min={0} value={servicio.valor} onChange={(e) => setServicio({ ...servicio, valor: e.target.value })} />}</Campo>
            <Boton tamano="pequeno" cargando={guardando === 'servicio'} disabled={!servicio.nombre.trim() || servicio.valor === ''}
              onClick={() => ejecutar('servicio', async () => {
                await agregarServicio({ nombre: servicio.nombre.trim(), valor: Number(servicio.valor), generaCargo: Number(servicio.valor) > 0 });
                setServicio({ nombre: '', valor: '' });
              }, 'Servicio agregado')}>Agregar</Boton>
          </div>
        </Tarjeta>
      </div>
    </div>
  );
}

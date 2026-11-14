import { useState } from 'react';
import { useNavigate } from 'react-router';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import Tarjeta from '../../components/Tarjeta';
import Boton from '../../components/Boton';
import Esqueleto from '../../components/Esqueleto';
import SelectorApartamento from '../../components/SelectorApartamento';
import useConsulta from '../../hooks/useConsulta';
import { consultarCalendario } from '../../api/operacion';
import { ESTADOS_RESERVA, MESES_LARGOS, SITUACIONES_NOCHE } from '../../utils/negocio';
import './calendario.css';

const DIAS = ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'];
const dos = (n) => String(n).padStart(2, '0');

/** Calendario de ocupación por apartamento y mes (7.5): cada noche disponible, ocupada o bloqueada. */
export default function Calendario() {
  const hoy = new Date();
  const [apto, setApto] = useState('APT-01');
  const [mes, setMes] = useState({ anio: hoy.getFullYear(), mes: hoy.getMonth() });
  const navegar = useNavigate();
  const diasMes = new Date(mes.anio, mes.mes + 1, 0).getDate();
  const desde = `${mes.anio}-${dos(mes.mes + 1)}-01`;
  const hasta = `${mes.anio}-${dos(mes.mes + 1)}-${dos(diasMes)}`;
  const { datos, cargando, error } = useConsulta(() => consultarCalendario(apto, desde, hasta), [apto, desde]);

  const mover = (delta) => setMes(({ anio, mes: m }) => {
    const f = new Date(anio, m + delta, 1);
    return { anio: f.getFullYear(), mes: f.getMonth() };
  });
  const huecos = (new Date(mes.anio, mes.mes, 1).getDay() + 6) % 7;
  const porFecha = Object.fromEntries((datos?.noches ?? []).map((n) => [n.fecha, n]));

  return (
    <div className="pagina">
      <div className="pagina__cabecera">
        <div>
          <h1 className="pagina__titulo">Calendario de ocupación</h1>
          <p className="pagina__subtitulo">Cada casilla es una noche del apartamento.</p>
        </div>
        <SelectorApartamento valor={apto} alCambiar={setApto} />
      </div>
      <Tarjeta titulo={`${MESES_LARGOS[mes.mes]} ${mes.anio}`} acciones={<>
        <Boton variante="fantasma" tamano="pequeno" onClick={() => mover(-1)} aria-label="Mes anterior"><ChevronLeft size={18} /></Boton>
        <Boton variante="fantasma" tamano="pequeno" onClick={() => setMes({ anio: hoy.getFullYear(), mes: hoy.getMonth() })}>Hoy</Boton>
        <Boton variante="fantasma" tamano="pequeno" onClick={() => mover(1)} aria-label="Mes siguiente"><ChevronRight size={18} /></Boton>
      </>}>
        {error && <div className="aviso aviso--error">{error.message}</div>}
        {cargando && !datos ? <Esqueleto filas={6} /> : (
          <>
            <div className="calendario" style={{ opacity: cargando ? 0.6 : 1 }}>
              {DIAS.map((d) => <div key={d} className="calendario__dia-semana">{d}</div>)}
              {Array.from({ length: huecos }, (_, i) => <div key={`h${i}`} />)}
              {Array.from({ length: diasMes }, (_, i) => {
                const fecha = `${mes.anio}-${dos(mes.mes + 1)}-${dos(i + 1)}`;
                const noche = porFecha[fecha];
                const situacion = noche?.situacion ?? 'DISPONIBLE';
                const enlace = noche?.codigoReserva;
                return (
                  <button key={fecha} type="button" disabled={!enlace}
                    className={`calendario__noche calendario__noche--${situacion}`}
                    onClick={() => enlace && navegar(`/recepcion/reservas/${enlace}`)}
                    title={noche?.motivoBloqueo ?? (enlace ? `${enlace} · ${ESTADOS_RESERVA[noche.estadoReserva] ?? ''}` : SITUACIONES_NOCHE[situacion])}>
                    <span className="calendario__numero">{i + 1}</span>
                    {enlace && <span className="calendario__detalle">{enlace.replace('RES-', '')}</span>}
                    {situacion === 'BLOQUEADA' && <span className="calendario__detalle">Bloqueo</span>}
                  </button>
                );
              })}
            </div>
            <div className="fila" style={{ marginTop: 18, fontSize: 13, color: 'var(--texto-2)' }}>
              {Object.entries(SITUACIONES_NOCHE).map(([k, t]) => (
                <span key={k} className="fila" style={{ gap: 6 }}><span className={`calendario__muestra calendario__noche--${k}`} /> {t}</span>
              ))}
            </div>
          </>
        )}
      </Tarjeta>
    </div>
  );
}

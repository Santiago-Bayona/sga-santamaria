import { useState } from 'react';
import Tarjeta from '../../components/Tarjeta';
import Esqueleto from '../../components/Esqueleto';
import useConsulta from '../../hooks/useConsulta';
import { reporteCanales, reporteIngresos, reporteOcupacion } from '../../api/operacion';
import { formatearPesos, hoyIso } from '../../utils/formato';
import { CANALES, MEDIOS_PAGO } from '../../utils/negocio';

const primerDiaMes = () => `${hoyIso().slice(0, 8)}01`;

/** Reportes (7.9) con filtro por rango de fechas: ocupación, ingresos por medio de pago y reservas por canal. */
export default function Reportes() {
  const [rango, setRango] = useState({ desde: primerDiaMes(), hasta: hoyIso() });
  const valido = rango.desde && rango.hasta && rango.desde <= rango.hasta;
  const clave = `${rango.desde}|${rango.hasta}`;
  const vacio = () => Promise.resolve(null);
  const ocupacion = useConsulta(() => (valido ? reporteOcupacion(rango.desde, rango.hasta) : vacio()), [clave]);
  const ingresos = useConsulta(() => (valido ? reporteIngresos(rango.desde, rango.hasta) : vacio()), [clave]);
  const canales = useConsulta(() => (valido ? reporteCanales(rango.desde, rango.hasta) : vacio()), [clave]);

  const o = ocupacion.datos;
  const i = ingresos.datos;
  const maxIngreso = Math.max(1, ...(i?.porMedio ?? []).map((m) => m.total));
  const maxCanal = Math.max(1, ...(canales.datos?.canales ?? []).map((c) => c.total));
  const porcentaje = Number(o?.porcentajeOcupacion ?? 0);

  return (
    <div className="pagina">
      <div className="pagina__cabecera">
        <div>
          <h1 className="pagina__titulo">Reportes</h1>
          <p className="pagina__subtitulo">Los ingresos son pagos registrados; los cargos sin pagar no cuentan.</p>
        </div>
        <div className="fila">
          <label className="campo"><span className="campo__etiqueta">Desde</span>
            <input className="control" type="date" value={rango.desde} onChange={(e) => setRango({ ...rango, desde: e.target.value })} /></label>
          <label className="campo"><span className="campo__etiqueta">Hasta</span>
            <input className="control" type="date" value={rango.hasta} min={rango.desde} onChange={(e) => setRango({ ...rango, hasta: e.target.value })} /></label>
        </div>
      </div>
      {!valido && <div className="aviso aviso--alerta">La fecha final debe ser igual o posterior a la inicial.</div>}
      {[ocupacion, ingresos, canales].map((c, k) => c.error && <div key={k} className="aviso aviso--error">{c.error.message}</div>)}

      <div className="rejilla rejilla--3">
        <Tarjeta titulo="Ocupación">
          {ocupacion.cargando && !o ? <Esqueleto filas={3} /> : o && (
            <div className="pila" style={{ gap: 12 }}>
              <p style={{ fontSize: 40, fontWeight: 700, color: 'var(--verde-oscuro)', lineHeight: 1 }}>{porcentaje.toFixed(1)}%</p>
              <div className="barra__pista"><div className="barra__relleno" style={{ width: `${Math.min(100, porcentaje)}%` }} /></div>
              <p className="campo__ayuda">{o.nochesVendidas} noches vendidas de {o.nochesPeriodo} posibles ({o.apartamentosActivos} apartamentos activos).</p>
            </div>
          )}
        </Tarjeta>
        <Tarjeta titulo="Ingresos">
          {ingresos.cargando && !i ? <Esqueleto filas={3} /> : i && (
            <div className="pila">
              <p style={{ fontSize: 32, fontWeight: 700 }}>{formatearPesos(i.totalIngresos)}</p>
              <div className="barras">
                {i.porMedio.map((m) => (
                  <div key={m.medio} className="barra__fila">
                    <span>{MEDIOS_PAGO[m.medio] ?? m.medio}</span>
                    <div className="barra__pista"><div className="barra__relleno" style={{ width: `${(m.total / maxIngreso) * 100}%` }} /></div>
                    <span className="barra__valor">{formatearPesos(m.total)}</span>
                  </div>
                ))}
              </div>
            </div>
          )}
        </Tarjeta>
        <Tarjeta titulo="Reservas por canal">
          {canales.cargando && !canales.datos ? <Esqueleto filas={3} /> : canales.datos && (
            <div className="barras">
              {canales.datos.canales.map((c) => (
                <div key={c.canal}>
                  <div className="barra__fila">
                    <span>{CANALES[c.canal] ?? c.canal}</span>
                    <div className="barra__pista"><div className="barra__relleno" style={{ width: `${(c.total / maxCanal) * 100}%` }} /></div>
                    <span className="barra__valor">{c.total}</span>
                  </div>
                  <p className="campo__ayuda" style={{ marginLeft: 132 }}>{c.canceladas} canceladas · {c.noShow} no-show</p>
                </div>
              ))}
            </div>
          )}
        </Tarjeta>
      </div>
    </div>
  );
}

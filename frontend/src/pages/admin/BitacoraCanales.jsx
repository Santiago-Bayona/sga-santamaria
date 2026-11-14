import { useState } from 'react';
import Tarjeta from '../../components/Tarjeta';
import Esqueleto from '../../components/Esqueleto';
import Paginacion from '../../components/Paginacion';
import useConsulta from '../../hooks/useConsulta';
import { listarBitacora, listarConflictosCanal } from '../../api/canales';
import { formatearFechaHora } from '../../utils/formato';

const RESULTADOS = {
  ACEPTADA: 'Aceptada',
  RECHAZADA_CONFLICTO: 'Rechazada por conflicto',
  DUPLICADO: 'Duplicado',
};

/** Bitácora de mensajes de canales (7.8) y conflictos pendientes de revisión. */
export default function BitacoraCanales() {
  const [paginaBitacora, setPaginaBitacora] = useState(0);
  const [paginaConflictos, setPaginaConflictos] = useState(0);
  const bitacora = useConsulta(() => listarBitacora(paginaBitacora), [paginaBitacora]);
  const conflictos = useConsulta(() => listarConflictosCanal(paginaConflictos), [paginaConflictos]);

  return (
    <div className="pagina">
      <div className="pagina__cabecera">
        <div>
          <h1 className="pagina__titulo">Canales externos</h1>
          <p className="pagina__subtitulo">Cada mensaje queda en la bitácora, también si se rechaza o llega repetido.</p>
        </div>
      </div>
      {bitacora.error && <div className="aviso aviso--error">{bitacora.error.message}</div>}
      <Tarjeta titulo="Bitácora" sinRelleno>
        {bitacora.cargando && !bitacora.datos ? <Esqueleto filas={4} /> : (
          <table className="tabla">
            <thead>
              <tr><th>Cuándo</th><th>Canal</th><th>Id externo</th><th>Resultado</th><th>Detalle</th></tr>
            </thead>
            <tbody>
              {(bitacora.datos?.contenido ?? []).map((evento) => (
                <tr key={evento.codigo}>
                  <td>{formatearFechaHora(evento.ocurridoEn)}</td>
                  <td>{evento.codigoCanal}</td>
                  <td>{evento.identificadorExterno}</td>
                  <td>{RESULTADOS[evento.resultado] ?? evento.resultado}</td>
                  <td>{evento.motivo}<br /><span className="campo__ayuda">{evento.payload}</span></td>
                </tr>
              ))}
              {bitacora.datos?.contenido?.length === 0 && (
                <tr><td colSpan={5}>Todavía no llega ningún mensaje de canal.</td></tr>
              )}
            </tbody>
          </table>
        )}
        <Paginacion pagina={bitacora.datos} alCambiar={setPaginaBitacora} />
      </Tarjeta>

      {conflictos.error && <div className="aviso aviso--error">{conflictos.error.message}</div>}
      <Tarjeta titulo="Conflictos" sinRelleno>
        {conflictos.cargando && !conflictos.datos ? <Esqueleto filas={3} /> : (
          <table className="tabla">
            <thead>
              <tr><th>Detectado</th><th>Id externo</th><th>Apartamento</th><th>Reserva vigente</th><th>Revisión</th></tr>
            </thead>
            <tbody>
              {(conflictos.datos?.contenido ?? []).map((c) => (
                <tr key={c.codigo}>
                  <td>{formatearFechaHora(c.fechaDeteccion)}</td>
                  <td>{c.identificadorExterno}</td>
                  <td>{c.identificacionApartamento}</td>
                  <td>{c.codigoReservaVigente}</td>
                  <td>{c.revisado ? c.notaRevision : 'Pendiente'}</td>
                </tr>
              ))}
              {conflictos.datos?.contenido?.length === 0 && (
                <tr><td colSpan={5}>No hay conflictos por revisar.</td></tr>
              )}
            </tbody>
          </table>
        )}
        <Paginacion pagina={conflictos.datos} alCambiar={setPaginaConflictos} />
      </Tarjeta>
    </div>
  );
}

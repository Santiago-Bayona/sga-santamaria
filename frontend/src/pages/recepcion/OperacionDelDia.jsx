import { useState } from 'react';
import { useNavigate } from 'react-router';
import { ArrowDownToLine, ArrowUpFromLine, RefreshCw, Sparkles, FileText, Users } from 'lucide-react';
import Tarjeta from '../../components/Tarjeta';
import Boton from '../../components/Boton';
import Esqueleto from '../../components/Esqueleto';
import EstadoVacio from '../../components/EstadoVacio';
import useConsulta from '../../hooks/useConsulta';
import useApartamentos from '../../hooks/useApartamentos';
import { obtenerResumenDiario } from '../../api/operacion';
import { formatearFecha, formatearHora, hoyIso, sumarDias } from '../../utils/formato';
import { MOTIVOS_SIN_IA } from '../../utils/negocio';
import './recepcion.css';

/**
 * Operación del día (7.6 + 10.2): llegadas y salidas previstas y el resumen redactado por la IA.
 * Si la IA está apagada o falla, la pantalla funciona igual: solo cambia el texto del resumen.
 */
export default function OperacionDelDia() {
  const [fecha, setFecha] = useState(hoyIso());
  const { datos, cargando, error, recargar } = useConsulta(() => obtenerResumenDiario(fecha), [fecha]);
  const { nombre } = useApartamentos();
  const navegar = useNavigate();

  const ocupantesHoy = datos?.llegadas?.reduce((total, m) => total + m.totalOcupantes, 0) ?? 0;

  return (
    <div className="pagina">
      <div className="pagina__cabecera">
        <div>
          <h1 className="pagina__titulo">Operación del día</h1>
          <p className="pagina__subtitulo">Llegadas y salidas previstas para {formatearFecha(fecha)}.</p>
        </div>
        <div className="fila">
          <Boton variante="fantasma" tamano="pequeno" onClick={() => setFecha(sumarDias(fecha, -1))}>Anterior</Boton>
          <label className="visualmente-oculto" htmlFor="fecha-operacion">Fecha</label>
          <input id="fecha-operacion" className="control" type="date" style={{ width: 180, minHeight: 40 }}
            value={fecha} onChange={(e) => e.target.value && setFecha(e.target.value)} />
          <Boton variante="fantasma" tamano="pequeno" onClick={() => setFecha(sumarDias(fecha, 1))}>Siguiente</Boton>
          {fecha !== hoyIso() && <Boton variante="secundario" tamano="pequeno" onClick={() => setFecha(hoyIso())}>Hoy</Boton>}
        </div>
      </div>

      {error && (
        <div className="aviso aviso--error" role="alert">
          {error.message}
          <Boton variante="fantasma" tamano="pequeno" onClick={recargar}>Reintentar</Boton>
        </div>
      )}

      <div className="rejilla rejilla--3">
        <Indicador icono={ArrowDownToLine} valor={datos?.totalLlegadas} etiqueta="Llegadas previstas" cargando={cargando} />
        <Indicador icono={ArrowUpFromLine} valor={datos?.totalSalidas} etiqueta="Salidas previstas" cargando={cargando} />
        <Indicador icono={Users} valor={ocupantesHoy} etiqueta="Personas que llegan" cargando={cargando} />
      </div>

      <ResumenIa datos={datos} cargando={cargando} alRegenerar={recargar} />

      <div className="rejilla rejilla--2">
        <Tarjeta titulo="Llegadas" sinRelleno>
          <TablaMovimientos movimientos={datos?.llegadas} cargando={cargando} conHora nombre={nombre}
            vacio="No hay llegadas previstas para esta fecha." alAbrir={(c) => navegar(`/recepcion/reservas/${c}`)} />
        </Tarjeta>
        <Tarjeta titulo="Salidas" sinRelleno>
          <TablaMovimientos movimientos={datos?.salidas} cargando={cargando} nombre={nombre}
            vacio="No hay salidas previstas para esta fecha." alAbrir={(c) => navegar(`/recepcion/reservas/${c}`)} />
        </Tarjeta>
      </div>
    </div>
  );
}

function Indicador({ icono: Icono, valor, etiqueta, cargando }) {
  return (
    <div className="tarjeta indicador">
      <span className="indicador__icono"><Icono size={22} aria-hidden="true" /></span>
      <div>
        <p className="indicador__valor">{cargando ? '·' : (valor ?? 0)}</p>
        <p className="indicador__etiqueta">{etiqueta}</p>
      </div>
    </div>
  );
}

function ResumenIa({ datos, cargando, alRegenerar }) {
  const conIa = datos?.generadoPorIa;
  return (
    <section className={`resumen-ia ${conIa ? 'resumen-ia--ia' : ''}`} aria-live="polite">
      <div className="resumen-ia__cabecera">
        <div className="fila" style={{ gap: 10 }}>
          <span className="resumen-ia__icono">{conIa ? <Sparkles size={20} /> : <FileText size={20} />}</span>
          <div>
            <h2 className="resumen-ia__titulo">Resumen del día</h2>
            {!cargando && datos && (
              <p className="resumen-ia__origen">
                {conIa ? '✨ Redactado con IA' : 'Resumen automático sin IA'}
              </p>
            )}
          </div>
        </div>
        <Boton variante="fantasma" tamano="pequeno" onClick={alRegenerar} cargando={cargando}>
          {!cargando && <RefreshCw size={15} aria-hidden="true" />} Actualizar
        </Boton>
      </div>
      {cargando ? (
        <Esqueleto filas={3} alto={14} />
      ) : (
        <>
          <p className="resumen-ia__texto">{datos?.texto ?? 'Sin resumen disponible.'}</p>
          {datos && !conIa && datos.motivoDegradacion && (
            <p className="resumen-ia__nota">{MOTIVOS_SIN_IA[datos.motivoDegradacion] ?? datos.motivoDegradacion} La operación sigue normal.</p>
          )}
        </>
      )}
    </section>
  );
}

function TablaMovimientos({ movimientos, cargando, conHora, vacio, nombre, alAbrir }) {
  if (cargando) return <Esqueleto filas={3} />;
  if (!movimientos?.length) return <EstadoVacio titulo="Sin movimientos">{vacio}</EstadoVacio>;
  return (
    <div className="tabla-contenedor">
      <table className="tabla">
        <thead>
          <tr>
            <th>Reserva</th>
            <th>Apartamento</th>
            {conHora && <th>Hora estimada</th>}
            <th className="num">Personas</th>
          </tr>
        </thead>
        <tbody>
          {movimientos.map((m) => (
            <tr key={m.codigoReserva} className="tabla__fila--enlace" onClick={() => alAbrir(m.codigoReserva)}>
              <td><button type="button" className="enlace-tabla" onClick={() => alAbrir(m.codigoReserva)}>{m.codigoReserva}</button></td>
              <td>{nombre(m.identificacionApartamento)}</td>
              {conHora && <td>{formatearHora(m.horaEstimada)}</td>}
              <td className="num">{m.totalOcupantes}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

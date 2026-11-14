import { useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router';
import { Plus, Search, X } from 'lucide-react';
import Tarjeta from '../../components/Tarjeta';
import Boton from '../../components/Boton';
import ChipEstado from '../../components/ChipEstado';
import Esqueleto from '../../components/Esqueleto';
import EstadoVacio from '../../components/EstadoVacio';
import Paginacion from '../../components/Paginacion';
import useConsulta from '../../hooks/useConsulta';
import useApartamentos from '../../hooks/useApartamentos';
import { listarReservas } from '../../api/reservas';
import { formatearFecha, formatearPesos } from '../../utils/formato';
import { CANALES, ESTADOS_RESERVA } from '../../utils/negocio';
import './recepcion.css';

const CAMPOS = ['estado', 'canalOrigen', 'identificacionApartamento', 'codigoTitular', 'fechaDesde', 'fechaHasta'];

/**
 * Listado de reservas (7.5): de la más reciente a la más antigua, 10 por página, con filtros por fechas,
 * estado, apartamento, canal y titular. Los filtros viven en la URL para poder compartir o recargar la vista.
 */
export default function Reservas() {
  const [parametros, setParametros] = useSearchParams();
  const filtros = Object.fromEntries(CAMPOS.map((c) => [c, parametros.get(c) ?? '']));
  const pagina = Number(parametros.get('pagina') ?? 0);
  const [borrador, setBorrador] = useState(filtros);
  const { apartamentos, nombre } = useApartamentos();
  const navegar = useNavigate();
  const clave = parametros.toString();

  const { datos, cargando, error, recargar } = useConsulta(() => listarReservas(filtros, pagina), [clave]);

  function aplicar(nuevos, nuevaPagina = 0) {
    const p = new URLSearchParams();
    Object.entries(nuevos).forEach(([c, v]) => v && p.set(c, v));
    if (nuevaPagina) p.set('pagina', nuevaPagina);
    setParametros(p);
  }
  const hayFiltros = CAMPOS.some((c) => filtros[c]);
  const cambiar = (campo) => (e) => setBorrador({ ...borrador, [campo]: e.target.value });

  return (
    <div className="pagina">
      <div className="pagina__cabecera">
        <div>
          <h1 className="pagina__titulo">Reservas</h1>
          <p className="pagina__subtitulo">De la más reciente a la más antigua.</p>
        </div>
        <Link to="/recepcion/reservas/nueva" className="boton boton--primario"><Plus size={18} aria-hidden="true" /> Nueva reserva</Link>
      </div>

      <Tarjeta sinRelleno>
        <form className="filtros" onSubmit={(e) => { e.preventDefault(); aplicar(borrador); }}>
          <Filtro etiqueta="Estado">
            <select className="control" value={borrador.estado} onChange={cambiar('estado')}>
              <option value="">Todos</option>
              {Object.entries(ESTADOS_RESERVA).map(([v, t]) => <option key={v} value={v}>{t}</option>)}
            </select>
          </Filtro>
          <Filtro etiqueta="Canal">
            <select className="control" value={borrador.canalOrigen} onChange={cambiar('canalOrigen')}>
              <option value="">Todos</option>
              {Object.entries(CANALES).map(([v, t]) => <option key={v} value={v}>{t}</option>)}
            </select>
          </Filtro>
          <Filtro etiqueta="Apartamento">
            <select className="control" value={borrador.identificacionApartamento} onChange={cambiar('identificacionApartamento')}>
              <option value="">Todos</option>
              {apartamentos.map((a) => <option key={a.identificacion} value={a.identificacion}>{a.nombre}</option>)}
            </select>
          </Filtro>
          <Filtro etiqueta="Titular">
            <input className="control" placeholder="Ej. TIT-001" value={borrador.codigoTitular} onChange={cambiar('codigoTitular')} />
          </Filtro>
          <Filtro etiqueta="Entrada desde">
            <input className="control" type="date" value={borrador.fechaDesde} onChange={cambiar('fechaDesde')} />
          </Filtro>
          <Filtro etiqueta="Entrada hasta">
            <input className="control" type="date" value={borrador.fechaHasta} min={borrador.fechaDesde || undefined} onChange={cambiar('fechaHasta')} />
          </Filtro>
          <div className="filtros__acciones">
            <Boton type="submit" tamano="pequeno"><Search size={16} aria-hidden="true" /> Filtrar</Boton>
            {hayFiltros && (
              <Boton variante="fantasma" tamano="pequeno" onClick={() => { const v = Object.fromEntries(CAMPOS.map((c) => [c, ''])); setBorrador(v); aplicar(v); }}>
                <X size={16} aria-hidden="true" /> Limpiar
              </Boton>
            )}
          </div>
        </form>

        {error && <div className="aviso aviso--error" style={{ margin: 22 }} role="alert">{error.message} <Boton variante="fantasma" tamano="pequeno" onClick={recargar}>Reintentar</Boton></div>}
        {cargando && !datos ? <Esqueleto filas={6} /> : !datos?.contenido?.length ? (
          <EstadoVacio titulo={hayFiltros ? 'Ninguna reserva coincide' : 'Aún no hay reservas'}>
            {hayFiltros ? 'Prueba con otros filtros.' : 'Crea la primera desde "Nueva reserva".'}
          </EstadoVacio>
        ) : (
          <div className="tabla-contenedor" style={{ opacity: cargando ? 0.6 : 1 }}>
            <table className="tabla">
              <thead>
                <tr>
                  <th>Reserva</th><th>Estado</th><th>Apartamento</th><th>Titular</th>
                  <th>Entrada</th><th>Salida</th><th className="num">Personas</th><th className="num">Valor</th>
                </tr>
              </thead>
              <tbody>
                {datos.contenido.map((r) => (
                  <tr key={r.codigo} className="tabla__fila--enlace" onClick={() => navegar(`/recepcion/reservas/${r.codigo}`)}>
                    <td><Link className="enlace-tabla" to={`/recepcion/reservas/${r.codigo}`} onClick={(e) => e.stopPropagation()}>{r.codigo}</Link></td>
                    <td><ChipEstado estado={r.estado} /></td>
                    <td>{nombre(r.identificacionApartamento)}</td>
                    <td>{r.codigoTitular}</td>
                    <td>{formatearFecha(r.fechaEntrada)}</td>
                    <td>{formatearFecha(r.fechaSalida)}</td>
                    <td className="num">{r.totalOcupantes}</td>
                    <td className="num">{formatearPesos(r.valorTotal)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
        <Paginacion pagina={datos} alCambiar={(p) => aplicar(filtros, p)} />
      </Tarjeta>
    </div>
  );
}

function Filtro({ etiqueta, children }) {
  return (
    <label className="campo">
      <span className="campo__etiqueta" style={{ fontSize: 13 }}>{etiqueta}</span>
      {children}
    </label>
  );
}

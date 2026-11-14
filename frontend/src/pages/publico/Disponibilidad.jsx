import { useState } from 'react';
import { Link, useSearchParams } from 'react-router';
import { SearchX, SlidersHorizontal } from 'lucide-react';
import Tarjeta from '../../components/Tarjeta';
import Boton from '../../components/Boton';
import Campo from '../../components/Campo';
import Esqueleto from '../../components/Esqueleto';
import EstadoVacio from '../../components/EstadoVacio';
import Paginacion from '../../components/Paginacion';
import TarjetaApartamento from '../../components/TarjetaApartamento';
import { fechasDelGrupo } from '../../utils/grupo';
import useConsulta from '../../hooks/useConsulta';
import { consultarDisponibilidad } from '../../api/disponibilidad';
import { formatearFecha, hoyIso, sumarDias } from '../../utils/formato';
import { SelectorDesplegable } from './Inicio';
import { leerBusqueda, parametrosBusqueda } from './busqueda';

/**
 * Resultados de disponibilidad (7.5): apartamentos libres en todas las noches, con capacidad para el grupo
 * y sin bloqueos. El cálculo lo hace el dominio; aquí solo se muestran las tarjetas y los filtros.
 */
export default function Disponibilidad() {
  const [parametros, setParametros] = useSearchParams();
  const busqueda = leerBusqueda(parametros);
  const filtros = {
    capacidadMinima: parametros.get('capacidadMinima') ?? '',
    precioMinimo: parametros.get('precioMinimo') ?? '',
    precioMaximo: parametros.get('precioMaximo') ?? '',
  };
  const pagina = Number(parametros.get('pagina') ?? 0);
  const [borrador, setBorrador] = useState({ ...busqueda, ...filtros });
  const clave = parametros.toString();
  const noches = Math.round((new Date(busqueda.salida) - new Date(busqueda.entrada)) / 86400000);

  const { datos, cargando, error } = useConsulta(
    () => consultarDisponibilidad({ fechaEntrada: busqueda.entrada, fechaSalida: busqueda.salida, fechasNacimiento: fechasDelGrupo(busqueda.grupo), ...filtros }, pagina),
    [clave],
  );

  function aplicar(e, nuevaPagina = 0) {
    e?.preventDefault();
    const p = new URLSearchParams(parametrosBusqueda(borrador));
    ['capacidadMinima', 'precioMinimo', 'precioMaximo'].forEach((c) => borrador[c] && p.set(c, borrador[c]));
    if (nuevaPagina) p.set('pagina', nuevaPagina);
    setParametros(p);
  }
  const valorFiltro = (c) => (e) => setBorrador({ ...borrador, [c]: e.target.value });
  const consulta = parametrosBusqueda(busqueda);

  return (
    <div className="seccion">
      <h1 className="seccion__titulo">Apartamentos disponibles</h1>
      <p className="seccion__bajada">
        Del {formatearFecha(busqueda.entrada)} al {formatearFecha(busqueda.salida)} · {noches} noche{noches === 1 ? '' : 's'} ·
        {' '}{busqueda.grupo.adultos + busqueda.grupo.ninos.length} persona{busqueda.grupo.adultos + busqueda.grupo.ninos.length === 1 ? '' : 's'}
      </p>

      <div className="rejilla rejilla--lateral">
        <Tarjeta titulo={<span className="fila" style={{ gap: 8 }}><SlidersHorizontal size={18} /> Ajustar búsqueda</span>}>
          <form className="pila" onSubmit={aplicar}>
            <Campo etiqueta="Llegada">
              {(p) => <input {...p} type="date" min={hoyIso()} value={borrador.entrada} onChange={valorFiltro('entrada')} />}
            </Campo>
            <Campo etiqueta="Salida">
              {(p) => <input {...p} type="date" min={sumarDias(borrador.entrada || hoyIso(), 1)} value={borrador.salida} onChange={valorFiltro('salida')} />}
            </Campo>
            <SelectorDesplegable grupo={borrador.grupo} alCambiar={(grupo) => setBorrador({ ...borrador, grupo })} />
            <Campo etiqueta="Capacidad mínima">
              {(p) => <input {...p} type="number" min={1} value={borrador.capacidadMinima} onChange={valorFiltro('capacidadMinima')} placeholder="Cualquiera" />}
            </Campo>
            <div className="rejilla rejilla--2" style={{ gap: 10 }}>
              <Campo etiqueta="Precio mín.">
                {(p) => <input {...p} type="number" min={0} step={10000} value={borrador.precioMinimo} onChange={valorFiltro('precioMinimo')} />}
              </Campo>
              <Campo etiqueta="Precio máx.">
                {(p) => <input {...p} type="number" min={0} step={10000} value={borrador.precioMaximo} onChange={valorFiltro('precioMaximo')} />}
              </Campo>
            </div>
            <Boton type="submit" bloque>Actualizar resultados</Boton>
          </form>
        </Tarjeta>

        <div className="pila">
          {error && <div className="aviso aviso--error" role="alert">{error.message}</div>}
          {cargando && !datos ? <div className="tarjeta"><Esqueleto filas={5} /></div> : !datos?.contenido?.length ? (
            <div className="tarjeta">
              <EstadoVacio icono={SearchX} titulo="No hay apartamentos disponibles con esos datos">
                Prueba con otras fechas, menos personas o sin filtros de precio.
              </EstadoVacio>
            </div>
          ) : (
            <>
              <div className="rejilla rejilla--2" style={{ opacity: cargando ? 0.6 : 1 }}>
                {datos.contenido.map((a) => (
                  <TarjetaApartamento key={a.identificacion} apartamento={a} valorEstimado={a.valorEstimado} noches={noches}
                    accion={<Link className="boton boton--primario boton--pequeno" to={`/apartamentos/${a.identificacion}?${consulta}`}>Ver y cotizar</Link>} />
                ))}
              </div>
              <div className="tarjeta"><Paginacion pagina={datos} alCambiar={(p) => aplicar(null, p)} /></div>
            </>
          )}
        </div>
      </div>
    </div>
  );
}

import { useEffect, useRef, useState } from 'react';
import { Link, useNavigate } from 'react-router';
import { ChevronDown, MapPin, Search, Trees, Waves, PawPrint } from 'lucide-react';
import Boton from '../../components/Boton';
import Campo from '../../components/Campo';
import SelectorGrupo from '../../components/SelectorGrupo';
import TarjetaApartamento from '../../components/TarjetaApartamento';
import Esqueleto from '../../components/Esqueleto';
import useApartamentos from '../../hooks/useApartamentos';
import useConsulta from '../../hooks/useConsulta';
import MapaUbicacion from '../../components/MapaUbicacion';
import { consultarAlojamiento } from '../../api/alojamiento';
import { hoyIso, sumarDias } from '../../utils/formato';
import { parametrosBusqueda } from './busqueda';


export default function Inicio() {
  const navegar = useNavigate();
  const { apartamentos } = useApartamentos();
  const { datos: alojamiento } = useConsulta(consultarAlojamiento, []);
  const [busqueda, setBusqueda] = useState({ entrada: sumarDias(hoyIso(), 7), salida: sumarDias(hoyIso(), 9), grupo: { adultos: 2, ninos: [] } });
  const [errores, setErrores] = useState({});

  function buscar(e) {
    e.preventDefault();
    const nuevos = {};
    if (!busqueda.entrada || busqueda.entrada < hoyIso()) nuevos.entrada = 'Elige una fecha desde hoy.';
    if (!busqueda.salida || busqueda.salida <= busqueda.entrada) nuevos.salida = 'Debe ser posterior a la entrada.';
    if (busqueda.grupo.ninos.some((n) => !n)) nuevos.grupo = 'Indica la fecha de nacimiento de cada niño.';
    setErrores(nuevos);
    if (!Object.keys(nuevos).length) navegar(`/disponibilidad?${parametrosBusqueda(busqueda)}`);
  }

  return (
    <>
      <section className="portada">
        <div className="portada__ilustracion"><img src="/mirador.svg" alt="" /></div>
        <div className="portada__interior">
          <div className="portada__texto">
            <span className="acceso__linea" style={{ width: 56, height: 3, borderRadius: 3, background: 'var(--verde)' }} />
            <h1 className="portada__titulo">El Quindío se ve mejor desde lo alto</h1>
            <p className="portada__bajada">
              Apartamentos completos con piscina en Montenegro, para familias, parejas y grupos que quieren
              descansar en el corazón del paisaje cafetero.
            </p>
          </div>
          <form className="buscador" onSubmit={buscar} noValidate aria-label="Buscar disponibilidad">
            <Campo etiqueta="Llegada" error={errores.entrada}>
              {(p) => <input {...p} type="date" min={hoyIso()} value={busqueda.entrada}
                onChange={(e) => setBusqueda({ ...busqueda, entrada: e.target.value })} />}
            </Campo>
            <Campo etiqueta="Salida" error={errores.salida}>
              {(p) => <input {...p} type="date" min={busqueda.entrada ? sumarDias(busqueda.entrada, 1) : hoyIso()} value={busqueda.salida}
                onChange={(e) => setBusqueda({ ...busqueda, salida: e.target.value })} />}
            </Campo>
            <SelectorDesplegable grupo={busqueda.grupo} error={errores.grupo}
              alCambiar={(grupo) => setBusqueda({ ...busqueda, grupo })} />
            <Boton type="submit"><Search size={18} aria-hidden="true" /> Buscar</Boton>
          </form>
        </div>
      </section>

      <section className="seccion">
        <div className="rasgos">
          <Rasgo icono={Trees} titulo="Apartamentos completos" texto="Cada grupo tiene su propio apartamento con cocina, baños y zona social." />
          <Rasgo icono={Waves} titulo="Piscina y naturaleza" texto="Áreas comunes rodeadas del paisaje cultural cafetero del Quindío." />
          <Rasgo icono={PawPrint} titulo="Mascotas bienvenidas" texto="Hasta dos por apartamento, con un cargo por estancia." />
        </div>
      </section>

      <section className="seccion" style={{ paddingTop: 0 }}>
        <h2 className="seccion__titulo">Nuestros apartamentos</h2>
        <p className="seccion__bajada"><MapPin size={15} aria-hidden="true" /> Montenegro, Quindío · cobro por persona y por noche; los menores de 3 años no pagan.</p>
        {!apartamentos.length ? <div className="tarjeta"><Esqueleto filas={3} /></div> : (
          <div className="rejilla rejilla--3">
            {apartamentos.map((a) => (
              <TarjetaApartamento key={a.identificacion} apartamento={a}
                accion={<Link className="boton boton--secundario boton--pequeno" to={`/apartamentos/${a.identificacion}`}>Ver detalle</Link>} />
            ))}
          </div>
        )}
      </section>

      {alojamiento && (
        <section className="seccion" style={{ paddingTop: 0 }} aria-labelledby="titulo-llegar">
          <h2 id="titulo-llegar" className="seccion__titulo">Cómo llegar</h2>
          <p className="seccion__bajada"><MapPin size={15} aria-hidden="true" /> {alojamiento.direccion}. A pocos minutos del Parque del Café.</p>
          <MapaUbicacion latitud={alojamiento.latitud} longitud={alojamiento.longitud} nombre={alojamiento.nombre}
            direccion={alojamiento.direccion} ciudad={alojamiento.ciudad} alto={360} />
        </section>
      )}
    </>
  );
}

function Rasgo({ icono: Icono, titulo, texto }) {
  return (
    <div className="tarjeta rasgo">
      <span className="rasgo__icono"><Icono size={22} aria-hidden="true" /></span>
      <div><p style={{ fontWeight: 600 }}>{titulo}</p><p className="campo__ayuda">{texto}</p></div>
    </div>
  );
}

/** Botón con el resumen del grupo que despliega el selector de adultos y niños. */
export function SelectorDesplegable({ grupo, alCambiar, error }) {
  const [abierto, setAbierto] = useState(false);
  const ref = useRef(null);
  useEffect(() => {
    if (!abierto) return undefined;
    const fuera = (e) => !ref.current?.contains(e.target) && setAbierto(false);
    document.addEventListener('mousedown', fuera);
    return () => document.removeEventListener('mousedown', fuera);
  }, [abierto]);
  const total = grupo.adultos + grupo.ninos.length;
  return (
    <div className="campo buscador__grupo" ref={ref}>
      <span className="campo__etiqueta">Huéspedes</span>
      <button type="button" className="control buscador__resumen fila" aria-expanded={abierto} onClick={() => setAbierto((v) => !v)}>
        <span>{total} persona{total === 1 ? '' : 's'}{grupo.ninos.length ? ` · ${grupo.ninos.length} niño${grupo.ninos.length === 1 ? '' : 's'}` : ''}</span>
        <ChevronDown size={16} aria-hidden="true" />
      </button>
      {error && <span className="campo__error">{error}</span>}
      {abierto && <div className="buscador__desplegable"><SelectorGrupo grupo={grupo} alCambiar={alCambiar} /></div>}
    </div>
  );
}

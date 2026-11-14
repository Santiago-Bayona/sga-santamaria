import { useState } from 'react';
import { Link, useNavigate } from 'react-router';
import { CalendarPlus, Luggage } from 'lucide-react';
import Tarjeta from '../../components/Tarjeta';
import ChipEstado from '../../components/ChipEstado';
import Esqueleto from '../../components/Esqueleto';
import EstadoVacio from '../../components/EstadoVacio';
import Paginacion from '../../components/Paginacion';
import { useAuth } from '../../auth/AuthContext';
import useConsulta from '../../hooks/useConsulta';
import useApartamentos from '../../hooks/useApartamentos';
import { historialTitular } from '../../api/reservas';
import { formatearFecha, formatearPesos } from '../../utils/formato';
import AvisoSinTitular from './AvisoSinTitular';

/** Historial del huésped (7.1): solo sus reservas; el backend lo verifica por propietario (7.2). */
export default function MisReservas() {
  const { usuario } = useAuth();
  const { nombre } = useApartamentos();
  const navegar = useNavigate();
  const [pagina, setPagina] = useState(0);
  const titular = usuario.codigoTitular;
  const { datos, cargando, error } = useConsulta(
    () => (titular ? historialTitular(titular, pagina) : Promise.resolve(null)), [titular, pagina]);

  if (!titular) return <div className="pagina"><AvisoSinTitular /></div>;

  return (
    <div className="pagina">
      <div className="pagina__cabecera">
        <div>
          <h1 className="pagina__titulo">Mis reservas</h1>
          <p className="pagina__subtitulo">Hola, {usuario.nombre}. Aquí están tus estancias en Santamaría.</p>
        </div>
        <Link className="boton boton--primario" to="/"><CalendarPlus size={18} aria-hidden="true" /> Buscar disponibilidad</Link>
      </div>
      {error && <div className="aviso aviso--error" role="alert">{error.message}</div>}
      <Tarjeta sinRelleno>
        {cargando && !datos ? <Esqueleto filas={4} /> : !datos?.contenido?.length ? (
          <EstadoVacio icono={Luggage} titulo="Aún no tienes reservas">Busca fechas y elige tu apartamento para empezar.</EstadoVacio>
        ) : (
          <div className="tabla-contenedor">
            <table className="tabla">
              <thead><tr><th>Reserva</th><th>Estado</th><th>Apartamento</th><th>Llegada</th><th>Salida</th><th className="num">Valor</th></tr></thead>
              <tbody>
                {datos.contenido.map((r) => (
                  <tr key={r.codigo} className="tabla__fila--enlace" onClick={() => navegar(`/huesped/reservas/${r.codigo}`)}>
                    <td><Link className="enlace-tabla" to={`/huesped/reservas/${r.codigo}`} onClick={(e) => e.stopPropagation()}>{r.codigo}</Link></td>
                    <td><ChipEstado estado={r.estado} /></td>
                    <td>{nombre(r.identificacionApartamento)}</td>
                    <td>{formatearFecha(r.fechaEntrada)}</td>
                    <td>{formatearFecha(r.fechaSalida)}</td>
                    <td className="num">{formatearPesos(r.valorTotal)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
        <Paginacion pagina={datos} alCambiar={setPagina} />
      </Tarjeta>
    </div>
  );
}

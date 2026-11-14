import { useState } from 'react';
import { CheckCircle2, PlayCircle, Sparkles } from 'lucide-react';
import Tarjeta from '../../components/Tarjeta';
import Boton from '../../components/Boton';
import Esqueleto from '../../components/Esqueleto';
import EstadoVacio from '../../components/EstadoVacio';
import Paginacion from '../../components/Paginacion';
import { useToast } from '../../components/Toasts';
import { useAuth } from '../../auth/AuthContext';
import useConsulta from '../../hooks/useConsulta';
import { iniciarPreparacion, listarPendientesPreparacion, terminarPreparacion } from '../../api/apartamentos';
import { ESTADOS_OPERATIVOS } from '../../utils/negocio';

/**
 * Apartamentos por preparar (7.1, personal de servicio): PENDIENTE_PREPARACION → EN_PREPARACION → PREPARADO.
 * El personal no puede declarar FUERA_DE_SERVICIO ni bloquear: eso es decisión administrativa.
 */
export default function Preparacion() {
  const { usuario } = useAuth();
  const toast = useToast();
  const [pagina, setPagina] = useState(0);
  const [enCurso, setEnCurso] = useState(null);
  const { datos, cargando, error, recargar } = useConsulta(() => listarPendientesPreparacion(pagina), [pagina]);

  async function cambiar(apto, funcion, mensaje) {
    setEnCurso(apto.identificacion);
    try {
      await funcion(apto.identificacion);
      toast.exito(mensaje);
      await recargar();
    } catch (e) {
      toast.error(e);
    } finally {
      setEnCurso(null);
    }
  }

  return (
    <div className="pagina">
      <div>
        <h1 className="pagina__titulo">Preparación de apartamentos</h1>
        <p className="pagina__subtitulo">Hola, {usuario.nombre}. Estos apartamentos esperan limpieza y preparación.</p>
      </div>
      {error && <div className="aviso aviso--error" role="alert">{error.message}</div>}
      {cargando && !datos ? <Tarjeta><Esqueleto filas={4} /></Tarjeta> : !datos?.contenido?.length ? (
        <Tarjeta><EstadoVacio icono={Sparkles} titulo="Todo está preparado">No hay apartamentos pendientes de preparación.</EstadoVacio></Tarjeta>
      ) : (
        <>
          <div className="rejilla rejilla--3">
            {datos.contenido.map((a) => (
              <div key={a.identificacion} className="tarjeta tarjeta__cuerpo pila" style={{ gap: 14 }}>
                <div className="fila fila--entre">
                  <div>
                    <h2 style={{ fontSize: 19, fontWeight: 600 }}>{a.nombre}</h2>
                    <p className="campo__ayuda">{a.identificacion} · {a.dormitorios} dormitorio(s)</p>
                  </div>
                  <span className={`chip chip--${a.estadoOperativo}`}>{ESTADOS_OPERATIVOS[a.estadoOperativo] ?? a.estadoOperativo}</span>
                </div>
                {a.estadoOperativo === 'PENDIENTE_PREPARACION' && (
                  <Boton cargando={enCurso === a.identificacion} onClick={() => cambiar(a, iniciarPreparacion, `Preparación de ${a.nombre} iniciada`)}>
                    <PlayCircle size={18} aria-hidden="true" /> Iniciar preparación
                  </Boton>
                )}
                {a.estadoOperativo === 'EN_PREPARACION' && (
                  <Boton cargando={enCurso === a.identificacion} onClick={() => cambiar(a, terminarPreparacion, `${a.nombre} quedó preparado`)}>
                    <CheckCircle2 size={18} aria-hidden="true" /> Terminar preparación
                  </Boton>
                )}
              </div>
            ))}
          </div>
          <div className="tarjeta"><Paginacion pagina={datos} alCambiar={setPagina} /></div>
        </>
      )}
    </div>
  );
}

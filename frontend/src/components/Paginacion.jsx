import { ChevronLeft, ChevronRight } from 'lucide-react';
import Boton from './Boton';

/** Controles para PaginaResponse { pagina, tamano, totalElementos, totalPaginas } (10 por página). */
export default function Paginacion({ pagina, alCambiar }) {
  if (!pagina || pagina.totalElementos === 0) return null;
  const { pagina: actual, tamano, totalElementos, totalPaginas } = pagina;
  const desde = actual * tamano + 1;
  const hasta = Math.min((actual + 1) * tamano, totalElementos);
  return (
    <nav className="paginacion" aria-label="Paginación">
      <span>{desde}–{hasta} de {totalElementos}</span>
      <div className="fila">
        <Boton variante="fantasma" tamano="pequeno" disabled={actual === 0} onClick={() => alCambiar(actual - 1)}>
          <ChevronLeft size={16} aria-hidden="true" /> Anterior
        </Boton>
        <span>Página {actual + 1} de {Math.max(totalPaginas, 1)}</span>
        <Boton variante="fantasma" tamano="pequeno" disabled={actual + 1 >= totalPaginas} onClick={() => alCambiar(actual + 1)}>
          Siguiente <ChevronRight size={16} aria-hidden="true" />
        </Boton>
      </div>
    </nav>
  );
}

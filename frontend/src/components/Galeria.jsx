import { useState } from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';

/**
 * Galería pública del apartamento (7.5): la imagen grande y las miniaturas debajo. Las imágenes que no cargan se
 * quitan solas; si no queda ninguna, se muestra el fondo de la marca con la inicial.
 */
export default function Galeria({ nombre, imagenes = [], principal }) {
  const ordenadas = principal && imagenes.includes(principal) ? [principal, ...imagenes.filter((u) => u !== principal)] : imagenes;
  const [rotas, setRotas] = useState([]);
  const [actual, setActual] = useState(0);
  const visibles = ordenadas.filter((u) => !rotas.includes(u));
  const indice = Math.min(actual, Math.max(visibles.length - 1, 0));
  const marcarRota = (url) => setRotas((r) => (r.includes(url) ? r : [...r, url]));

  if (!visibles.length) {
    return (
      <div className="apto__imagen galeria__principal">
        <span className="apto__inicial" style={{ fontSize: 72 }} aria-hidden="true">{nombre?.[0]}</span>
      </div>
    );
  }

  const mover = (paso) => setActual((indice + paso + visibles.length) % visibles.length);
  return (
    <div className="galeria">
      <div className="apto__imagen galeria__principal">
        <img src={visibles[indice]} alt={`${nombre}, foto ${indice + 1} de ${visibles.length}`} onError={() => marcarRota(visibles[indice])} />
        {visibles.length > 1 && (
          <>
            <button type="button" className="galeria__flecha galeria__flecha--izq" onClick={() => mover(-1)} aria-label="Foto anterior"><ChevronLeft size={22} /></button>
            <button type="button" className="galeria__flecha galeria__flecha--der" onClick={() => mover(1)} aria-label="Foto siguiente"><ChevronRight size={22} /></button>
            <span className="galeria__contador">{indice + 1} / {visibles.length}</span>
          </>
        )}
      </div>
      {visibles.length > 1 && (
        <div className="galeria__miniaturas" role="list">
          {visibles.map((url, i) => (
            <button key={url} type="button" role="listitem" className={`galeria__miniatura${i === indice ? ' galeria__miniatura--activa' : ''}`}
              onClick={() => setActual(i)} aria-label={`Ver foto ${i + 1}`} aria-current={i === indice}>
              <img src={url} alt="" loading="lazy" onError={() => marcarRota(url)} />
            </button>
          ))}
        </div>
      )}
    </div>
  );
}

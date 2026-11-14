import { useState } from 'react';
import { BedDouble, Users } from 'lucide-react';
import { formatearPesos } from '../utils/formato';

/** Tarjeta de resultado (7.5): imagen principal, capacidad y valor estimado. Sin imagen, usa un fondo de la marca. */
export default function TarjetaApartamento({ apartamento, valorEstimado, noches, accion }) {
  const [imagenRota, setImagenRota] = useState(false);
  const { nombre, capacidad, dormitorios, imagenPrincipal } = apartamento;
  return (
    <article className="tarjeta apto">
      <div className="apto__imagen">
        {imagenPrincipal && !imagenRota
          ? <img src={imagenPrincipal} alt={`Apartamento ${nombre}`} onError={() => setImagenRota(true)} loading="lazy" />
          : <span className="apto__inicial" aria-hidden="true">{nombre?.[0]}</span>}
      </div>
      <div className="apto__cuerpo">
        <h3 className="apto__nombre">{nombre}</h3>
        <div className="apto__meta">
          <span><Users size={16} aria-hidden="true" /> Hasta {capacidad} personas</span>
          <span><BedDouble size={16} aria-hidden="true" /> {dormitorios} dormitorio{dormitorios === 1 ? '' : 's'}</span>
        </div>
        <div className="apto__pie">
          {valorEstimado !== undefined ? (
            <p className="apto__precio">{formatearPesos(valorEstimado)}<small>valor estimado{noches ? ` · ${noches} noche${noches === 1 ? '' : 's'}` : ''}</small></p>
          ) : <span />}
          {accion}
        </div>
      </div>
    </article>
  );
}

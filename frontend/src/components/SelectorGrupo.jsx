import { Minus, Plus, X } from 'lucide-react';
import Boton from './Boton';

export default function SelectorGrupo({ grupo, alCambiar }) {
  const { adultos, ninos } = grupo;
  return (
    <div className="pila" style={{ gap: 12 }}>
      <div className="fila fila--entre">
        <div>
          <p style={{ fontWeight: 600 }}>Adultos</p>
          <p className="campo__ayuda">Mayores de edad</p>
        </div>
        <div className="fila">
          <Boton variante="secundario" tamano="pequeno" aria-label="Quitar un adulto" disabled={adultos <= 1}
            onClick={() => alCambiar({ ...grupo, adultos: adultos - 1 })}><Minus size={16} /></Boton>
          <span style={{ minWidth: 24, textAlign: 'center', fontWeight: 600 }} aria-live="polite">{adultos}</span>
          <Boton variante="secundario" tamano="pequeno" aria-label="Agregar un adulto"
            onClick={() => alCambiar({ ...grupo, adultos: adultos + 1 })}><Plus size={16} /></Boton>
        </div>
      </div>
      {ninos.map((fecha, i) => (
        <div key={i} className="fila">
          <label className="campo" style={{ flex: 1 }}>
            <span className="campo__etiqueta" style={{ fontSize: 13 }}>Niño {i + 1}: fecha de nacimiento</span>
            <input className="control" type="date" value={fecha}
              onChange={(e) => alCambiar({ ...grupo, ninos: ninos.map((n, j) => (j === i ? e.target.value : n)) })} />
          </label>
          <Boton variante="fantasma" aria-label={`Quitar niño ${i + 1}`} style={{ alignSelf: 'flex-end' }}
            onClick={() => alCambiar({ ...grupo, ninos: ninos.filter((_, j) => j !== i) })}><X size={18} /></Boton>
        </div>
      ))}
      <Boton variante="fantasma" tamano="pequeno" style={{ alignSelf: 'flex-start' }}
        onClick={() => alCambiar({ ...grupo, ninos: [...ninos, ''] })}>
        <Plus size={16} aria-hidden="true" /> Agregar niño
      </Boton>
    </div>
  );
}

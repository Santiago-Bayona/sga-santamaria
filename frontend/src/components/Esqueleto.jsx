/** Placeholder animado mientras llegan los datos. */
export default function Esqueleto({ filas = 4, alto = 18 }) {
  return (
    <div className="pila" style={{ gap: 12, padding: 22 }} aria-busy="true" aria-label="Cargando">
      {Array.from({ length: filas }, (_, i) => (
        <div key={i} className="esqueleto" style={{ height: alto, width: `${90 - (i % 3) * 15}%` }} />
      ))}
    </div>
  );
}

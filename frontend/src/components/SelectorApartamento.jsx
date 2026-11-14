import useApartamentos from '../hooks/useApartamentos';

/** Lista desplegable de los apartamentos activos. */
export default function SelectorApartamento({ valor, alCambiar, etiqueta = 'Apartamento', todos = false, id = 'selector-apartamento' }) {
  const { apartamentos } = useApartamentos();
  return (
    <label className="campo" htmlFor={id} style={{ minWidth: 220 }}>
      <span className="campo__etiqueta">{etiqueta}</span>
      <select id={id} className="control" value={valor} onChange={(e) => alCambiar(e.target.value)}>
        {todos ? <option value="">Todos</option> : !valor && <option value="">Elige un apartamento</option>}
        {apartamentos.map((a) => <option key={a.identificacion} value={a.identificacion}>{a.nombre} ({a.identificacion})</option>)}
      </select>
    </label>
  );
}

/** Botón tipo píldora de la línea E1. variante: primario | secundario | peligro | fantasma. */
export default function Boton({ variante = 'primario', tamano, bloque, cargando, children, className = '', ...props }) {
  const clases = ['boton', `boton--${variante}`, tamano === 'pequeno' && 'boton--pequeno', bloque && 'boton--bloque', className]
    .filter(Boolean).join(' ');
  return (
    <button type="button" className={clases} disabled={cargando || props.disabled} {...props}>
      {cargando && <span className="boton__giro" aria-hidden="true" />}
      {children}
    </button>
  );
}

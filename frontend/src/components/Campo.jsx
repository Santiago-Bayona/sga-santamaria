import { useId } from 'react';

/**
 * Campo de formulario con etiqueta, ayuda y error. Recibe el control como función para conectar
 * id y aria-* automáticamente: <Campo etiqueta="Correo">{(p) => <input {...p} />}</Campo>
 */
export default function Campo({ etiqueta, ayuda, error, children, className = '' }) {
  const id = useId();
  const idAyuda = `${id}-ayuda`;
  const idError = `${id}-error`;
  const props = {
    id,
    className: 'control',
    'aria-invalid': error ? 'true' : undefined,
    'aria-describedby': [ayuda && idAyuda, error && idError].filter(Boolean).join(' ') || undefined,
  };
  return (
    <div className={`campo ${className}`}>
      <label className="campo__etiqueta" htmlFor={id}>{etiqueta}</label>
      {children(props)}
      {ayuda && !error && <span id={idAyuda} className="campo__ayuda">{ayuda}</span>}
      {error && <span id={idError} className="campo__error" role="alert">{error}</span>}
    </div>
  );
}

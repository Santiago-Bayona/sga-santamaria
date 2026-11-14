import { useEffect, useRef } from 'react';
import { X } from 'lucide-react';

export default function Modal({ abierto, titulo, alCerrar, children, pie }) {
  const ref = useRef(null);
  // alCerrar suele llegar como función nueva en cada render; guardarla en una ref evita que el efecto de
  // abajo se repita en cada tecla (eso devolvía el foco al primer botón y no dejaba escribir).
  const alCerrarRef = useRef(alCerrar);
  useEffect(() => { alCerrarRef.current = alCerrar; });

  useEffect(() => {
    if (!abierto) return undefined;
    const previo = document.activeElement;
    const cuerpo = ref.current?.querySelector('.modal__cuerpo');
    (cuerpo?.querySelector('input, select, textarea') ?? ref.current?.querySelector('button'))?.focus();
    const tecla = (e) => e.key === 'Escape' && alCerrarRef.current?.();
    document.addEventListener('keydown', tecla);
    return () => { document.removeEventListener('keydown', tecla); previo?.focus?.(); };
  }, [abierto]);

  if (!abierto) return null;
  return (
    <div className="modal-fondo" onMouseDown={(e) => e.target === e.currentTarget && alCerrar()}>
      <div className="modal" role="dialog" aria-modal="true" aria-labelledby="modal-titulo" ref={ref}>
        <div className="modal__cabecera">
          <h2 id="modal-titulo" className="modal__titulo">{titulo}</h2>
          <button type="button" className="toast__cerrar" onClick={alCerrar} aria-label="Cerrar"><X size={20} /></button>
        </div>
        <div className="modal__cuerpo">{children}</div>
        {pie && <div className="modal__pie">{pie}</div>}
      </div>
    </div>
  );
}

import { createContext, useCallback, useContext, useMemo, useState } from 'react';
import { CheckCircle2, AlertCircle, X } from 'lucide-react';

const ToastContext = createContext(null);
let siguienteId = 0;

/** Avisos flotantes de éxito y error. Los 409 muestran el mensaje del dominio tal cual. */
export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);
  const quitar = useCallback((id) => setToasts((t) => t.filter((x) => x.id !== id)), []);
  const mostrar = useCallback((tipo, titulo, detalle) => {
    const id = ++siguienteId;
    setToasts((t) => [...t.slice(-3), { id, tipo, titulo, detalle }]);
    setTimeout(() => quitar(id), tipo === 'error' ? 7000 : 4000);
  }, [quitar]);

  const api = useMemo(() => ({
    exito: (titulo, detalle) => mostrar('exito', titulo, detalle),
    error: (errorOTitulo) => {
      if (typeof errorOTitulo === 'string') return mostrar('error', errorOTitulo);
      const e = errorOTitulo;
      const titulo = e?.status === 409 ? 'El negocio no lo permite' : e?.status === 400 ? 'Revisa los datos' : 'No se pudo completar';
      const detalle = [e?.message, ...(e?.detalles ?? [])].filter(Boolean).join(' · ');
      return mostrar('error', titulo, detalle);
    },
  }), [mostrar]);

  return (
    <ToastContext.Provider value={api}>
      {children}
      <div className="toasts" role="status" aria-live="polite">
        {toasts.map((t) => (
          <div key={t.id} className={`toast toast--${t.tipo}`}>
            {t.tipo === 'exito'
              ? <CheckCircle2 size={20} color="var(--verde)" aria-hidden="true" />
              : <AlertCircle size={20} color="var(--rojo)" aria-hidden="true" />}
            <div>
              <p className="toast__titulo">{t.titulo}</p>
              {t.detalle && <p className="toast__detalle">{t.detalle}</p>}
            </div>
            <button type="button" className="toast__cerrar" onClick={() => quitar(t.id)} aria-label="Cerrar aviso"><X size={16} /></button>
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
}

// eslint-disable-next-line react-refresh/only-export-components -- el hook acompaña a su proveedor
export function useToast() {
  return useContext(ToastContext);
}

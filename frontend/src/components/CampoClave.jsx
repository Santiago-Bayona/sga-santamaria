import { useState } from 'react';
import { Eye, EyeOff } from 'lucide-react';
import Campo from './Campo';

/** Campo de contraseña con botón para mostrarla u ocultarla. */
export default function CampoClave({ etiqueta = 'Contraseña', ayuda, error, valor, alCambiar, autoComplete = 'current-password' }) {
  const [visible, setVisible] = useState(false);
  return (
    <Campo etiqueta={etiqueta} ayuda={ayuda} error={error}>
      {(p) => (
        <div className="campo-clave">
          <input {...p} className="control control--con-boton" type={visible ? 'text' : 'password'}
            autoComplete={autoComplete} value={valor} onChange={(e) => alCambiar(e.target.value)} placeholder="Ingresa tu contraseña" />
          <button type="button" className="campo-clave__ojo" onClick={() => setVisible((v) => !v)}
            aria-label={visible ? 'Ocultar contraseña' : 'Mostrar contraseña'}>
            {visible ? <EyeOff size={19} /> : <Eye size={19} />}
          </button>
        </div>
      )}
    </Campo>
  );
}

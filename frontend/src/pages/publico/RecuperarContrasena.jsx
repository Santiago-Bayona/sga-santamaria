import { useState } from 'react';
import { Link } from 'react-router';
import { MailCheck } from 'lucide-react';
import LayoutAcceso from '../../layouts/LayoutAcceso';
import Campo from '../../components/Campo';
import Boton from '../../components/Boton';
import { solicitarRecuperacion } from '../../api/auth';

export default function RecuperarContrasena() {
  const [correo, setCorreo] = useState('');
  const [error, setError] = useState('');
  const [errorGeneral, setErrorGeneral] = useState('');
  const [enviando, setEnviando] = useState(false);
  const [enviado, setEnviado] = useState(false);

  async function enviar(e) {
    e.preventDefault();
    const limpio = correo.trim().toLowerCase();
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(limpio)) { setError('Escribe un correo válido.'); return; }
    setError('');
    setErrorGeneral('');
    setEnviando(true);
    try {
      await solicitarRecuperacion(limpio);
      setEnviado(true);
    } catch (err) {
      setErrorGeneral(err.message);
    } finally {
      setEnviando(false);
    }
  }

  if (enviado) {
    return (
      <LayoutAcceso>
        <div className="acceso__formulario">
          <div className="acceso__encabezado entra entra-1">
            <span className="acceso__icono-exito"><MailCheck size={28} aria-hidden="true" /></span>
            <h1>Revisa tu correo</h1>
            <p>
              Si <strong>{correo.trim().toLowerCase()}</strong> tiene una cuenta, te enviamos un enlace para elegir una
              contraseña nueva. Vence en 30 minutos y sirve una sola vez.
            </p>
          </div>
          <div className="aviso aviso--info entra entra-2">¿No llega? Mira en spam o promociones, o vuelve a pedirlo en un minuto.</div>
          <div className="entra entra-3"><Link className="boton boton--primario boton--bloque" to="/login">Volver a iniciar sesión</Link></div>
          <p className="acceso__pie entra entra-4">
            <button type="button" className="enlace-boton" onClick={() => setEnviado(false)}>Usar otro correo</button>
          </p>
        </div>
      </LayoutAcceso>
    );
  }

  return (
    <LayoutAcceso>
      <form className="acceso__formulario" onSubmit={enviar} noValidate>
        <div className="acceso__encabezado entra entra-1">
          <h1>¿Olvidaste tu contraseña?</h1>
          <p>Escribe el correo de tu cuenta y te enviamos un enlace para restablecerla.</p>
        </div>
        {errorGeneral && <div className="aviso aviso--error" role="alert">{errorGeneral}</div>}
        <div className="entra entra-2">
          <Campo etiqueta="Correo electrónico" error={error}>
            {(p) => <input {...p} type="email" autoComplete="email" placeholder="tucorreo@ejemplo.com" autoFocus
              value={correo} onChange={(e) => setCorreo(e.target.value)} />}
          </Campo>
        </div>
        <div className="entra entra-3"><Boton type="submit" bloque cargando={enviando}>Enviar enlace</Boton></div>
        <div className="acceso__separador entra entra-4" />
        <p className="acceso__pie entra entra-5">
          ¿La recordaste? <Link to="/login"><strong>Inicia sesión</strong></Link>
        </p>
      </form>
    </LayoutAcceso>
  );
}

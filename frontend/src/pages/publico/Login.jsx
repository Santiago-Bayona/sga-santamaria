import { useState } from 'react';
import { Link, Navigate, useLocation, useNavigate } from 'react-router';
import LayoutAcceso from '../../layouts/LayoutAcceso';
import Campo from '../../components/Campo';
import CampoClave from '../../components/CampoClave';
import Boton from '../../components/Boton';
import { useAuth } from '../../auth/AuthContext';
import { inicioPorRol } from '../../utils/negocio';

export default function Login() {
  const { autenticado, usuario, iniciarSesion, sesionExpirada } = useAuth();
  const navegar = useNavigate();
  const { state } = useLocation();
  const [form, setForm] = useState({ usuario: state?.registrado ?? '', clave: '' });
  const [errores, setErrores] = useState({});
  const [enviando, setEnviando] = useState(false);
  const [errorGeneral, setErrorGeneral] = useState('');

  if (autenticado) return <Navigate to={inicioPorRol(usuario.rol)} replace />;

  async function enviar(e) {
    e.preventDefault();
    const nuevos = {};
    if (!form.usuario.trim()) nuevos.usuario = 'Escribe tu usuario o correo.';
    if (!form.clave) nuevos.clave = 'Escribe tu contraseña.';
    setErrores(nuevos);
    if (Object.keys(nuevos).length) return;

    setEnviando(true);
    setErrorGeneral('');
    try {
      const rol = await iniciarSesion(form.usuario, form.clave);
      navegar(state?.desde ?? inicioPorRol(rol), { replace: true });
    } catch (error) {
      // Mensaje genérico: no revela si el usuario existe (Guía 12)
      setErrorGeneral(error.status === 401 || error.status === 400 ? 'Usuario o contraseña incorrectos.' : error.message);
    } finally {
      setEnviando(false);
    }
  }

  return (
    <LayoutAcceso>
      <form className="acceso__formulario" onSubmit={enviar} noValidate>
        <div className="acceso__encabezado entra entra-1">
          <h1>Bienvenido de nuevo</h1>
          <p>Ingresa con tu usuario o correo.</p>
        </div>

        {sesionExpirada && !errorGeneral && (
          <div className="aviso aviso--alerta entra entra-2" role="status">Tu sesión expiró. Vuelve a iniciar sesión.</div>
        )}
        {state?.claveCambiada && !errorGeneral && (
          <div className="aviso aviso--info entra entra-2" role="status">Listo, cambiaste tu contraseña. Ya puedes entrar con la nueva.</div>
        )}
        {state?.registrado && !errorGeneral && (
          <div className="aviso aviso--info entra entra-2" role="status">¡Cuenta creada! Ya puedes iniciar sesión con tu correo.</div>
        )}
        {errorGeneral && <div className="aviso aviso--error" role="alert">{errorGeneral}</div>}

        <div className="entra entra-2">
          <Campo etiqueta="Usuario o correo electrónico" error={errores.usuario}>
            {(p) => <input {...p} type="text" autoComplete="username" placeholder="Ingresa tu usuario o correo"
              value={form.usuario} onChange={(e) => setForm({ ...form, usuario: e.target.value })} />}
          </Campo>
        </div>
        <div className="entra entra-3">
          <CampoClave error={errores.clave} valor={form.clave} alCambiar={(clave) => setForm({ ...form, clave })} />
          <Link className="acceso__olvido" to="/recuperar-contrasena">¿Olvidaste tu contraseña?</Link>
        </div>
        <div className="entra entra-4">
          <Boton type="submit" bloque cargando={enviando}>Entrar</Boton>
        </div>
        <div className="acceso__separador entra entra-5" />
        <p className="acceso__pie entra entra-6">
          ¿Primera vez en Santamaría? <Link to="/registro"><strong>Crea tu cuenta de huésped</strong></Link>
        </p>
      </form>
    </LayoutAcceso>
  );
}

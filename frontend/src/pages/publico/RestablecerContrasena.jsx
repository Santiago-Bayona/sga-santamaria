import { useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router';
import LayoutAcceso from '../../layouts/LayoutAcceso';
import CampoClave from '../../components/CampoClave';
import Boton from '../../components/Boton';
import { restablecerContrasena } from '../../api/auth';

export default function RestablecerContrasena() {
  const [parametros] = useSearchParams();
  const token = parametros.get('token') ?? '';
  const navegar = useNavigate();
  const [form, setForm] = useState({ clave: '', confirmacion: '' });
  const [errores, setErrores] = useState({});
  const [errorGeneral, setErrorGeneral] = useState('');
  const [enviando, setEnviando] = useState(false);

  async function enviar(e) {
    e.preventDefault();
    const nuevos = {};
    if (!/^(?=.*[A-Z])(?=.*\d).{8,}$/.test(form.clave)) nuevos.clave = 'Mínimo 8 caracteres, con al menos una mayúscula y un número.';
    if (form.confirmacion !== form.clave) nuevos.confirmacion = 'Las contraseñas no coinciden.';
    setErrores(nuevos);
    if (Object.keys(nuevos).length) return;
    setEnviando(true);
    setErrorGeneral('');
    try {
      await restablecerContrasena(token, form.clave);
      navegar('/login', { replace: true, state: { claveCambiada: true } });
    } catch (err) {
      setErrorGeneral(err.message);
    } finally {
      setEnviando(false);
    }
  }

  if (!token) {
    return (
      <LayoutAcceso>
        <div className="acceso__formulario">
          <div className="acceso__encabezado entra entra-1">
            <h1>Enlace incompleto</h1>
            <p>Abre el enlace tal como llegó a tu correo, o pide uno nuevo.</p>
          </div>
          <Link className="boton boton--primario boton--bloque entra entra-2" to="/recuperar-contrasena">Pedir un enlace nuevo</Link>
        </div>
      </LayoutAcceso>
    );
  }

  return (
    <LayoutAcceso>
      <form className="acceso__formulario" onSubmit={enviar} noValidate>
        <div className="acceso__encabezado entra entra-1">
          <h1>Nueva contraseña</h1>
          <p>Elige una contraseña que no hayas usado antes.</p>
        </div>
        {errorGeneral && (
          <div className="aviso aviso--error" role="alert">
            <span>{errorGeneral} <Link to="/recuperar-contrasena"><strong>Pedir otro enlace</strong></Link></span>
          </div>
        )}
        <div className="entra entra-2">
          <CampoClave etiqueta="Nueva contraseña" autoComplete="new-password" error={errores.clave}
            ayuda="Mínimo 8 caracteres, una mayúscula y un número." valor={form.clave}
            alCambiar={(clave) => setForm({ ...form, clave })} />
        </div>
        <div className="entra entra-3">
          <CampoClave etiqueta="Confirma la contraseña" autoComplete="new-password" error={errores.confirmacion}
            valor={form.confirmacion} alCambiar={(confirmacion) => setForm({ ...form, confirmacion })} />
        </div>
        <div className="entra entra-4"><Boton type="submit" bloque cargando={enviando}>Guardar contraseña</Boton></div>
      </form>
    </LayoutAcceso>
  );
}

import { useState } from 'react';
import { Link, useNavigate } from 'react-router';
import LayoutAcceso from '../../layouts/LayoutAcceso';
import Campo from '../../components/Campo';
import CampoClave from '../../components/CampoClave';
import Boton from '../../components/Boton';
import { registrarHuesped } from '../../api/auth';
import { hoyIso } from '../../utils/formato';

const VACIO = { nombre: '', correo: '', contrasena: '', telefono: '', documento: '', fechaNacimiento: '' };

/** Validación de forma (igual que el Bean Validation del backend). Las reglas de negocio las valida la API. */
function validar(f) {
  const e = {};
  if (!f.nombre.trim()) e.nombre = 'Escribe tu nombre completo.';
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(f.correo.trim())) e.correo = 'Escribe un correo válido.';
  if (!/^(?=.*[A-Z])(?=.*\d).{8,}$/.test(f.contrasena)) e.contrasena = 'Mínimo 8 caracteres, con al menos una mayúscula y un número.';
  if (!f.telefono.trim()) e.telefono = 'Escribe tu teléfono.';
  if (!/^[0-9A-Za-z-]{5,20}$/.test(f.documento.trim())) e.documento = 'Entre 5 y 20 caracteres, sin espacios.';
  if (!f.fechaNacimiento) e.fechaNacimiento = 'Elige tu fecha de nacimiento.';
  else if (f.fechaNacimiento >= hoyIso()) e.fechaNacimiento = 'Debe ser una fecha pasada.';
  return e;
}

/** Convierte los detalles del 400 ("campo: mensaje") en errores por campo. */
function erroresDelServidor(detalles = []) {
  return Object.fromEntries(detalles.map((d) => d.split(/:\s(.+)/)).filter(([c, m]) => c && m));
}

export default function Registro() {
  const navegar = useNavigate();
  const [form, setForm] = useState(VACIO);
  const [errores, setErrores] = useState({});
  const [errorGeneral, setErrorGeneral] = useState('');
  const [enviando, setEnviando] = useState(false);
  const cambiar = (campo) => (e) => setForm({ ...form, [campo]: e.target.value });

  async function enviar(e) {
    e.preventDefault();
    const nuevos = validar(form);
    setErrores(nuevos);
    if (Object.keys(nuevos).length) return;
    setEnviando(true);
    setErrorGeneral('');
    try {
      const respuesta = await registrarHuesped({ ...form, correo: form.correo.trim().toLowerCase() });
      navegar('/login', { state: { registrado: respuesta.username } });
    } catch (error) {
      if (error.status === 400) setErrores(erroresDelServidor(error.detalles));
      setErrorGeneral(error.message);
    } finally {
      setEnviando(false);
    }
  }

  return (
    <LayoutAcceso>
      <form className="acceso__formulario acceso__formulario--ancho" onSubmit={enviar} noValidate>
        <div className="acceso__encabezado entra entra-1">
          <h1>Crea tu cuenta</h1>
          <p>Regístrate como huésped para reservar y seguir tus estancias.</p>
        </div>
        {errorGeneral && <div className="aviso aviso--error" role="alert">{errorGeneral}</div>}

        <div className="entra entra-2">
          <Campo etiqueta="Nombre completo" error={errores.nombre}>
            {(p) => <input {...p} autoComplete="name" value={form.nombre} onChange={cambiar('nombre')} placeholder="Como aparece en tu documento" />}
          </Campo>
        </div>
        <div className="rejilla rejilla--2 entra entra-3">
          <Campo etiqueta="Correo electrónico" error={errores.correo}>
            {(p) => <input {...p} type="email" autoComplete="email" value={form.correo} onChange={cambiar('correo')} placeholder="tu@correo.com" />}
          </Campo>
          <Campo etiqueta="Teléfono" error={errores.telefono}>
            {(p) => <input {...p} type="tel" autoComplete="tel" value={form.telefono} onChange={cambiar('telefono')} placeholder="300 123 4567" />}
          </Campo>
        </div>
        <div className="rejilla rejilla--2 entra entra-4">
          <Campo etiqueta="Documento de identidad" error={errores.documento}>
            {(p) => <input {...p} inputMode="numeric" value={form.documento} onChange={cambiar('documento')} placeholder="1094000222" />}
          </Campo>
          <Campo etiqueta="Fecha de nacimiento" error={errores.fechaNacimiento}>
            {(p) => <input {...p} type="date" max={hoyIso()} value={form.fechaNacimiento} onChange={cambiar('fechaNacimiento')} />}
          </Campo>
        </div>
        <div className="entra entra-5">
          <CampoClave autoComplete="new-password" ayuda="Mínimo 8 caracteres, con una mayúscula y un número."
            error={errores.contrasena} valor={form.contrasena} alCambiar={(contrasena) => setForm({ ...form, contrasena })} />
        </div>
        <div className="entra entra-6 pila" style={{ gap: 18 }}>
          <Boton type="submit" bloque cargando={enviando}>Crear cuenta</Boton>
          <div className="acceso__separador" />
          <p className="acceso__pie">¿Ya tienes cuenta? <Link to="/login"><strong>Inicia sesión</strong></Link></p>
        </div>
      </form>
    </LayoutAcceso>
  );
}

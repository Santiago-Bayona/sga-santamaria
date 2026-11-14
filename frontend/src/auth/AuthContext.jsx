import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { alVencerSesion, establecerToken } from '../api/cliente';
import { iniciarSesion as loginApi } from '../api/auth';

const CLAVE = 'sga.sesion';
const AuthContext = createContext(null);

/** Se guarda en sessionStorage (no localStorage): sobrevive a un F5 y se borra al cerrar la pestaña. */
function leerSesionGuardada() {
  let sesion = null;
  try {
    const texto = sessionStorage.getItem(CLAVE);
    sesion = texto ? JSON.parse(texto) : null;
  } catch { /* almacenamiento bloqueado o dañado */ }
  // El cliente necesita el token desde el primer render: las páginas piden datos antes de otros efectos.
  establecerToken(sesion?.token);
  return sesion;
}

export function AuthProvider({ children }) {
  const [sesion, setSesion] = useState(leerSesionGuardada);
  const [sesionExpirada, setSesionExpirada] = useState(false);

  const guardar = useCallback((nueva) => {
    establecerToken(nueva?.token);
    setSesion(nueva);
    try {
      if (nueva) sessionStorage.setItem(CLAVE, JSON.stringify(nueva));
      else sessionStorage.removeItem(CLAVE);
    } catch { /* almacenamiento bloqueado: la sesión vive solo en memoria */ }
  }, []);

  const cerrarSesion = useCallback(() => guardar(null), [guardar]);

  const iniciarSesion = useCallback(async (usuario, contrasena) => {
    const { token, username, rol, nombre, codigoTitular } = await loginApi(usuario.trim(), contrasena);
    setSesionExpirada(false);
    guardar({ token, username, rol, nombre, codigoTitular: codigoTitular ?? null });
    return rol;
  }, [guardar]);

  useEffect(() => {
    alVencerSesion(() => { setSesionExpirada(true); guardar(null); });
  }, [guardar]);

  const valor = useMemo(() => ({
    usuario: sesion,
    autenticado: Boolean(sesion?.token),
    sesionExpirada,
    iniciarSesion,
    cerrarSesion,
  }), [sesion, sesionExpirada, iniciarSesion, cerrarSesion]);

  return <AuthContext.Provider value={valor}>{children}</AuthContext.Provider>;
}

// eslint-disable-next-line react-refresh/only-export-components -- el hook acompaña a su proveedor
export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth debe usarse dentro de <AuthProvider>');
  return ctx;
}

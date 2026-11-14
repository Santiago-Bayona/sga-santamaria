import { useState } from 'react';
import Tarjeta from '../../components/Tarjeta';
import Boton from '../../components/Boton';
import Esqueleto from '../../components/Esqueleto';
import Paginacion from '../../components/Paginacion';
import { useToast } from '../../components/Toasts';
import useConsulta from '../../hooks/useConsulta';
import { useAuth } from '../../auth/AuthContext';
import { cambiarRol, desactivarUsuario, listarUsuarios } from '../../api/usuarios';
import { ROLES } from '../../utils/negocio';

const ROLES_ASIGNABLES = Object.keys(ROLES);

/** Administración de cuentas (7.2): filtro, cambio de rol y baja lógica. */
export default function Usuarios() {
  const toast = useToast();
  const { usuario } = useAuth();
  const [rol, setRol] = useState('');
  const [activo, setActivo] = useState('');
  const [pagina, setPagina] = useState(0);
  const consulta = useConsulta(
    () => listarUsuarios({ rol, activo: activo === '' ? undefined : activo === 'true', pagina }),
    [rol, activo, pagina],
  );

  async function aplicarRol(username, nuevoRol) {
    try {
      await cambiarRol(username, nuevoRol);
      toast.exito('Rol actualizado', 'Aplica en el próximo inicio de sesión.');
      await consulta.recargar();
    } catch (e) {
      toast.error(e);
    }
  }

  async function desactivar(username) {
    try {
      await desactivarUsuario(username);
      toast.exito('Usuario desactivado');
      await consulta.recargar();
    } catch (e) {
      toast.error(e);
    }
  }

  return (
    <div className="pagina">
      <div className="pagina__cabecera">
        <div>
          <h1 className="pagina__titulo">Usuarios</h1>
          <p className="pagina__subtitulo">La baja es lógica. Quien tenga reservas activas no se puede desactivar.</p>
        </div>
        <div className="fila">
          <label className="campo"><span className="campo__etiqueta">Rol</span>
            <select className="control" value={rol} onChange={(e) => { setPagina(0); setRol(e.target.value); }}>
              <option value="">Todos</option>
              {ROLES_ASIGNABLES.map((r) => <option key={r} value={r}>{ROLES[r]}</option>)}
            </select>
          </label>
          <label className="campo"><span className="campo__etiqueta">Estado</span>
            <select className="control" value={activo} onChange={(e) => { setPagina(0); setActivo(e.target.value); }}>
              <option value="">Todos</option>
              <option value="true">Activos</option>
              <option value="false">Inactivos</option>
            </select>
          </label>
        </div>
      </div>
      {consulta.error && <div className="aviso aviso--error">{consulta.error.message}</div>}
      <Tarjeta sinRelleno>
        {consulta.cargando && !consulta.datos ? <Esqueleto filas={5} /> : (
          <table className="tabla">
            <thead>
              <tr><th>Usuario</th><th>Nombre</th><th>Rol</th><th>Estado</th><th></th></tr>
            </thead>
            <tbody>
              {(consulta.datos?.contenido ?? []).map((u) => (
                <tr key={u.username}>
                  <td>{u.username}</td>
                  <td>{u.nombreCompleto}</td>
                  <td>
                    <select className="control" value={u.rol} disabled={!u.activo}
                      onChange={(e) => aplicarRol(u.username, e.target.value)}>
                      {ROLES_ASIGNABLES.map((r) => <option key={r} value={r}>{ROLES[r]}</option>)}
                    </select>
                  </td>
                  <td>{u.activo ? 'Activo' : 'Inactivo'}</td>
                  <td>
                    {u.activo && u.username !== usuario.username && (
                      <Boton variante="peligro" tamano="pequeno" onClick={() => desactivar(u.username)}>Desactivar</Boton>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
        <Paginacion pagina={consulta.datos} alCambiar={setPagina} />
      </Tarjeta>
    </div>
  );
}

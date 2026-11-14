import { useState } from 'react';
import Tarjeta from '../../components/Tarjeta';
import Campo from '../../components/Campo';
import Boton from '../../components/Boton';
import Esqueleto from '../../components/Esqueleto';
import { useToast } from '../../components/Toasts';
import useConsulta from '../../hooks/useConsulta';
import { useAuth } from '../../auth/AuthContext';
import { actualizarPerfil, cambiarContrasena, miPerfil, subirFotoPerfil } from '../../api/usuarios';

/** Perfil propio: nombre, teléfono, foto y cambio voluntario de contraseña. */
export default function Perfil() {
  const toast = useToast();
  const { actualizarNombre } = useAuth();
  const { datos, cargando, error, recargar } = useConsulta(miPerfil, []);
  const [nombre, setNombre] = useState('');
  const [telefono, setTelefono] = useState('');
  const [listo, setListo] = useState(false);
  const [guardando, setGuardando] = useState(false);
  const [claves, setClaves] = useState({ actual: '', nueva: '' });
  const [cambiando, setCambiando] = useState(false);

  if (datos && !listo) {
    setNombre(datos.nombreCompleto ?? '');
    setTelefono(datos.telefono ?? '');
    setListo(true);
  }

  if (cargando && !datos) return <div className="pagina"><Tarjeta><Esqueleto filas={4} /></Tarjeta></div>;
  if (error) return <div className="pagina"><div className="aviso aviso--error">{error.message}</div></div>;

  async function guardarPerfil(evento) {
    evento.preventDefault();
    setGuardando(true);
    try {
      const actualizado = await actualizarPerfil(nombre.trim(), telefono.trim());
      actualizarNombre(actualizado.nombreCompleto);
      toast.exito('Perfil actualizado');
      await recargar();
    } catch (e) {
      toast.error(e);
    } finally {
      setGuardando(false);
    }
  }

  async function subirFoto(evento) {
    const archivo = evento.target.files?.[0];
    evento.target.value = '';
    if (!archivo) return;
    try {
      await subirFotoPerfil(archivo);
      toast.exito('Foto actualizada');
      await recargar();
    } catch (e) {
      toast.error(e);
    }
  }

  async function guardarClave(evento) {
    evento.preventDefault();
    setCambiando(true);
    try {
      await cambiarContrasena(claves.actual, claves.nueva);
      setClaves({ actual: '', nueva: '' });
      toast.exito('Contraseña actualizada');
    } catch (e) {
      toast.error(e);
    } finally {
      setCambiando(false);
    }
  }

  return (
    <div className="pagina">
      <div className="pagina__cabecera">
        <div>
          <h1 className="pagina__titulo">Mi perfil</h1>
          <p className="pagina__subtitulo">{datos.username} · {datos.rol}</p>
        </div>
      </div>
      <div className="rejilla rejilla--2">
        <Tarjeta titulo="Datos de contacto">
          <form className="pila" onSubmit={guardarPerfil}>
            {datos.fotoUrl && <img src={datos.fotoUrl} alt="" style={{ width: 96, height: 96, objectFit: 'cover', borderRadius: 12 }} />}
            <Campo etiqueta="Foto">
              {(p) => <input {...p} type="file" accept="image/jpeg,image/png,image/webp" onChange={subirFoto} />}
            </Campo>
            <Campo etiqueta="Nombre">
              {(p) => <input {...p} value={nombre} onChange={(e) => setNombre(e.target.value)} required />}
            </Campo>
            <Campo etiqueta="Teléfono" ayuda="Entre 7 y 30 dígitos.">
              {(p) => <input {...p} value={telefono} onChange={(e) => setTelefono(e.target.value)} required />}
            </Campo>
            <Boton type="submit" cargando={guardando}>Guardar</Boton>
          </form>
        </Tarjeta>
        <Tarjeta titulo="Cambiar contraseña">
          <form className="pila" onSubmit={guardarClave}>
            <Campo etiqueta="Contraseña actual">
              {(p) => <input {...p} type="password" value={claves.actual} onChange={(e) => setClaves({ ...claves, actual: e.target.value })} required />}
            </Campo>
            <Campo etiqueta="Contraseña nueva" ayuda="Mínimo 8 caracteres, una mayúscula y un número.">
              {(p) => <input {...p} type="password" value={claves.nueva} onChange={(e) => setClaves({ ...claves, nueva: e.target.value })} required />}
            </Campo>
            <Boton type="submit" cargando={cambiando}>Cambiar contraseña</Boton>
          </form>
        </Tarjeta>
      </div>
    </div>
  );
}

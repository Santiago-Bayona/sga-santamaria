import { useRef, useState } from 'react';
import { ImageOff, ImagePlus, Star, Trash2 } from 'lucide-react';
import Tarjeta from './Tarjeta';
import Boton from './Boton';
import { useToast } from './Toasts';
import { guardarGaleria, subirImagen } from '../api/apartamentos';

const TIPOS = ['image/jpeg', 'image/png', 'image/webp'];
const MAXIMO_BYTES = 5 * 1024 * 1024;
const MAXIMO_IMAGENES = 10;

/**
 * Galería del apartamento para el administrador (7.3): subir fotos, elegir la principal y quitar las que sobran.
 * El dominio exige entre 1 y 10 imágenes, así que la última no se puede quitar.
 */
export default function GaleriaAdmin({ apartamento, alCambiar }) {
  const toast = useToast();
  const entrada = useRef(null);
  const [ocupado, setOcupado] = useState(null);
  const [rotas, setRotas] = useState([]);
  const imagenes = apartamento.imagenes ?? [];
  const indicePrincipal = Math.max(imagenes.indexOf(apartamento.imagenPrincipal), 0);
  const lleno = imagenes.length >= MAXIMO_IMAGENES;

  async function subir(e) {
    const archivo = e.target.files?.[0];
    e.target.value = '';
    if (!archivo) return;
    if (!TIPOS.includes(archivo.type)) { toast.error('La imagen debe ser JPG, PNG o WebP.'); return; }
    if (archivo.size > MAXIMO_BYTES) { toast.error('La imagen supera los 5 MB.'); return; }
    setOcupado('subir');
    try {
      await subirImagen(apartamento.identificacion, archivo);
      toast.exito('Imagen agregada a la galería');
      await alCambiar();
    } catch (error) {
      toast.error(error);
    } finally {
      setOcupado(null);
    }
  }

  async function guardar(clave, nuevas, principal, mensaje) {
    setOcupado(clave);
    try {
      await guardarGaleria(apartamento, nuevas, principal);
      toast.exito(mensaje);
      await alCambiar();
    } catch (error) {
      toast.error(error);
    } finally {
      setOcupado(null);
    }
  }

  const hacerPrincipal = (i) => guardar(`principal-${i}`, imagenes, i, 'Imagen principal actualizada');
  const quitar = (i) => {
    const nuevas = imagenes.filter((_, j) => j !== i);
    const principal = i === indicePrincipal ? 0 : i < indicePrincipal ? indicePrincipal - 1 : indicePrincipal;
    guardar(`quitar-${i}`, nuevas, principal, 'Imagen quitada de la galería');
  };

  return (
    <Tarjeta titulo={`Galería (${imagenes.length} de ${MAXIMO_IMAGENES})`} acciones={
      <>
        <input ref={entrada} type="file" accept={TIPOS.join(',')} hidden onChange={subir} />
        <Boton variante="secundario" tamano="pequeno" disabled={lleno} cargando={ocupado === 'subir'} onClick={() => entrada.current?.click()}>
          <ImagePlus size={16} aria-hidden="true" /> Subir imagen
        </Boton>
      </>
    }>
      <div className="galeria-admin">
        {imagenes.map((url, i) => (
          <figure key={url} className={`galeria-admin__item${i === indicePrincipal ? ' galeria-admin__item--principal' : ''}`}>
            <div className="galeria-admin__foto">
              {rotas.includes(url)
                ? <span className="galeria-admin__rota"><ImageOff size={22} aria-hidden="true" /> No carga</span>
                : <img src={url} alt={`Foto ${i + 1} de ${apartamento.nombre}`} loading="lazy" onError={() => setRotas((r) => [...r, url])} />}
              {i === indicePrincipal && <span className="galeria-admin__sello"><Star size={13} aria-hidden="true" /> Principal</span>}
            </div>
            <figcaption className="galeria-admin__acciones">
              {i !== indicePrincipal && (
                <Boton variante="fantasma" tamano="pequeno" cargando={ocupado === `principal-${i}`} disabled={Boolean(ocupado)} onClick={() => hacerPrincipal(i)}>
                  <Star size={14} aria-hidden="true" /> Principal
                </Boton>
              )}
              <Boton variante="fantasma" tamano="pequeno" cargando={ocupado === `quitar-${i}`} disabled={Boolean(ocupado) || imagenes.length <= 1}
                onClick={() => quitar(i)} title={imagenes.length <= 1 ? 'El apartamento necesita al menos una imagen' : undefined}>
                <Trash2 size={14} aria-hidden="true" /> Quitar
              </Boton>
            </figcaption>
          </figure>
        ))}
      </div>
      <p className="campo__ayuda" style={{ marginTop: 14 }}>
        JPG, PNG o WebP de hasta 5 MB. Entre 1 y 10 fotos; la principal es la que se ve en las tarjetas del sitio.
        {rotas.length > 0 && ' Las fotos que no cargan son direcciones de ejemplo: sube fotos reales y quítalas.'}
      </p>
    </Tarjeta>
  );
}

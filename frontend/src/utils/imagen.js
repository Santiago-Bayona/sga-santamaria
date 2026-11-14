/**
 * Las ilustraciones de demostración viven en /galeria del frontend. Las direcciones sga.local
 * eran un relleno que el navegador no puede abrir; se traducen al archivo que sí está publicado.
 */
export function resolverImagen(url) {
  if (typeof url !== 'string' || !url.includes('sga.local')) return url;
  const demo = url.match(/apt-\d+-\d+/i);
  return demo ? `/galeria/${demo[0].toLowerCase()}.svg` : url;
}

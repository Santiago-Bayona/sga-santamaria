import { useEffect, useRef, useState } from 'react';
import { ExternalLink, MapPin } from 'lucide-react';

const TOKEN = import.meta.env.VITE_MAPBOX_TOKEN;

/**
 * Mapa del alojamiento (7.5). Con VITE_MAPBOX_TOKEN usa Mapbox y carga la librería solo entonces.
 * Sin token, o si Mapbox falla, muestra OpenStreetMap. Si tampoco hay coordenadas, queda la dirección.
 */
export default function MapaUbicacion({ latitud, longitud, nombre, direccion, ciudad, alto = 300 }) {
  const contenedor = useRef(null);
  const [fallo, setFallo] = useState(false);
  const lat = Number(latitud);
  const lng = Number(longitud);
  const valido = Number.isFinite(lat) && Number.isFinite(lng);
  const yaTraeCiudad = ciudad && direccion?.toLowerCase().includes(ciudad.toLowerCase());
  const lugar = (yaTraeCiudad ? [direccion] : [direccion, ciudad]).filter(Boolean).join(', ');
  const enlace = valido ? `https://www.openstreetmap.org/?mlat=${lat}&mlon=${lng}#map=15/${lat}/${lng}` : null;
  const delta = 0.018;
  const mapaOsm = valido
    ? `https://www.openstreetmap.org/export/embed.html?bbox=${lng - delta}%2C${lat - delta}%2C${lng + delta}%2C${lat + delta}&layer=mapnik&marker=${lat}%2C${lng}`
    : null;

  useEffect(() => {
    if (!TOKEN || !valido || !contenedor.current) return undefined;
    let mapa;
    let cancelado = false;
    Promise.all([import('mapbox-gl'), import('mapbox-gl/dist/mapbox-gl.css')])
      .then(([{ default: mapboxgl }]) => {
        if (cancelado || !contenedor.current) return;
        mapboxgl.accessToken = TOKEN;
        mapa = new mapboxgl.Map({
          container: contenedor.current,
          style: 'mapbox://styles/mapbox/outdoors-v12',
          center: [lng, lat],
          zoom: 13.5,
          cooperativeGestures: true,
          attributionControl: true,
        });
        mapa.addControl(new mapboxgl.NavigationControl({ showCompass: false }), 'top-right');
        new mapboxgl.Marker({ color: '#1E8449' })
          .setLngLat([lng, lat])
          .setPopup(new mapboxgl.Popup({ offset: 24 }).setText(nombre ?? 'Santamaría'))
          .addTo(mapa);
        mapa.on('error', () => setFallo(true));
      })
      .catch(() => setFallo(true));
    return () => { cancelado = true; mapa?.remove(); };
  }, [lat, lng, valido, nombre]);

  const mostrarMapbox = Boolean(TOKEN) && valido && !fallo;
  return (
    <div className="mapa">
      {mostrarMapbox ? (
        <div ref={contenedor} className="mapa__lienzo" style={{ height: alto }} role="region" aria-label={`Mapa de ${nombre ?? 'el alojamiento'}`} />
      ) : mapaOsm ? (
        <iframe className="mapa__lienzo" style={{ height: alto }} title={`Mapa de ${nombre ?? 'el alojamiento'}`} src={mapaOsm} loading="lazy" />
      ) : (
        <div className="mapa__respaldo" style={{ height: alto }}>
          <MapPin size={34} aria-hidden="true" />
          <p>{lugar || 'Ubicación del alojamiento'}</p>
        </div>
      )}
      <div className="mapa__pie">
          <span className="fila" style={{ gap: 6 }}><MapPin size={15} aria-hidden="true" /> {lugar}</span>        {enlace && <a href={enlace} target="_blank" rel="noreferrer" className="fila" style={{ gap: 4 }}>Abrir en el mapa <ExternalLink size={14} aria-hidden="true" /></a>}
      </div>
    </div>
  );
}

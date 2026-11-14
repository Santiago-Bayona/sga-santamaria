import { useEffect, useState } from 'react';
import { listarApartamentos } from '../api/alojamiento';

let cache = null;

/** Mapa identificación → apartamento, para mostrar "Orquídea" en vez de "APT-02". Se pide una sola vez. */
export default function useApartamentos() {
  const [apartamentos, setApartamentos] = useState(cache ?? []);
  useEffect(() => {
    if (cache) return;
    listarApartamentos(0)
      .then((pagina) => { cache = pagina?.contenido ?? []; setApartamentos(cache); })
      .catch(() => {});
  }, []);
  const nombre = (id) => apartamentos.find((a) => a.identificacion === id)?.nombre ?? id;
  return { apartamentos, nombre };
}

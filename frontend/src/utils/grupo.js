const FECHA_ADULTO = '1990-01-01';

/**
 * Composición del grupo (7.5). El backend necesita fechas de nacimiento, no edades (3.2):
 * los adultos se envían con una fecha genérica y los menores con la fecha real, porque de ella
 * depende si son facturables.
 */
export function fechasDelGrupo({ adultos, ninos }) {
  return [...Array.from({ length: adultos }, () => FECHA_ADULTO), ...ninos.filter(Boolean)];
}

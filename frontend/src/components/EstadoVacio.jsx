import { Inbox } from 'lucide-react';

export default function EstadoVacio({ titulo, children, icono: Icono = Inbox }) {
  return (
    <div className="vacio">
      <span className="vacio__icono"><Icono size={24} aria-hidden="true" /></span>
      <p className="vacio__titulo">{titulo}</p>
      {children && <p>{children}</p>}
    </div>
  );
}

import { ArrowRight } from 'lucide-react';
import Marca from '../components/Marca';
import './acceso.css';

/** Marco de login y registro: ilustración del mirador a la izquierda y el formulario a la derecha. */
export default function LayoutAcceso({ children }) {
  return (
    <main className="acceso">
      <section className="acceso__escena">
        <div className="acceso__ilustracion">
          <img src="/mirador.svg" alt="Ilustración del mirador de Montenegro, Quindío, al atardecer entre montañas y palmas de cera" />
        </div>
        <div className="acceso__textos">
          <div className="entra entra-1"><Marca /></div>
          <div className="acceso__lema">
            <span className="acceso__linea entra entra-2" />
            <h2 className="acceso__titulo entra entra-3">El Quindío se ve mejor desde lo alto</h2>
            <p className="acceso__bajada entra entra-4">
              Reserva tu apartamento, consulta tu folio y prepara tu llegada a Santamaría.
            </p>
          </div>
        </div>
        <div className="acceso__insignia" aria-hidden="true">
          <span className="acceso__insignia-pulso" />
          <span className="acceso__insignia-circulo"><ArrowRight size={30} /></span>
        </div>
      </section>
      <section className="acceso__panel">{children}</section>
    </main>
  );
}

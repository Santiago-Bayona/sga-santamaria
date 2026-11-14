/** Logo de Santamaría: la "A" del mirador dentro de un círculo. */
export default function Marca({ claro = false, subtitulo = 'Montenegro · Quindío' }) {
  return (
    <div className="marca">
      <svg width="40" height="40" viewBox="0 0 42 42" aria-hidden="true">
        <circle cx="21" cy="21" r="21" fill={claro ? '#FFFFFF' : 'var(--verde)'} />
        <path d="M13 29 L21 13 L29 29 M16 23 H26" fill="none" stroke={claro ? 'var(--verde)' : '#FFFFFF'}
          strokeWidth="2.6" strokeLinecap="round" strokeLinejoin="round" />
      </svg>
      <div className="marca__texto">
        <span className="marca__nombre">Santamaría</span>
        {subtitulo && <span className="marca__sub">{subtitulo}</span>}
      </div>
    </div>
  );
}

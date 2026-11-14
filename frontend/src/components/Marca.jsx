/** Logo de Santamaría: la "A" del mirador dentro de un círculo. */
export default function Marca({ claro = false, subtitulo = 'Montenegro · Quindío' }) {
  return (
    <div className="marca">

        <img src="/logo-completo.png" alt="Logo de Santamaría"
             className={`marca__logo${claro ? ' marca__logo--claro' : ''}`} />

      <div className="marca__texto">
        <span className="marca__nombre">Santamaría</span>
        {subtitulo && <span className="marca__sub">{subtitulo}</span>}
      </div>
    </div>
  );
}

export default function Tarjeta({ titulo, acciones, children, sinRelleno, className = '' }) {
  return (
    <section className={`tarjeta ${className}`}>
      {(titulo || acciones) && (
        <header className="tarjeta__cabecera">
          {titulo && <h2 className="tarjeta__titulo">{titulo}</h2>}
          {acciones && <div className="fila">{acciones}</div>}
        </header>
      )}
      {sinRelleno ? children : <div className="tarjeta__cuerpo">{children}</div>}
    </section>
  );
}

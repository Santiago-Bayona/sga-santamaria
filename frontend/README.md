# SGA Santamaría — Frontend

Interfaz web del Sistema de Gestión de Alojamiento, en **React 19 + Vite**, con la línea visual E1
(mirador de Montenegro ilustrado, verde y blanco, tipografía Poppins).

## Cómo correrlo

Requisitos: Node 20 o superior y el backend arriba en `http://localhost:8080`
(su CORS ya permite `http://localhost:5173`).

```bash
cd frontend
npm install
npm run dev        # http://localhost:5173
```

Por defecto el frontend llama a `http://localhost:8080`. Para otra URL, copie `.env.deveploment`
como `.env.development` (o `.env.production` antes de `npm run build`) y cambie `VITE_API_URL`.
Esos archivos no se suben: están en el `.gitignore`.

El mapa del alojamiento usa **Mapbox**: ponga su token público (`pk....`) en `VITE_MAPBOX_TOKEN` del mismo archivo
(`.env.development`). Sin token, la página muestra la dirección con un enlace a OpenStreetMap y no se rompe nada.

| Comando | Para qué |
|---|---|
| `npm run dev` | Servidor de desarrollo con recarga en caliente |
| `npm run build` | Compila a `dist/` |
| `npm run lint` | Revisa el código con ESLint |

## Usuarios de prueba (datos semilla del backend)

| Usuario | Contraseña | Rol | Qué ve hoy |
|---|---|---|---|
| `recepcion` | `recepcion123` | Recepción | Operación, reservas, folios, calendario y bloqueos |
| `admin` | `admin123` | Administrador | Todo lo de recepción + administración y reportes |
| `huesped` / `huesped2` | `huesped123` | Huésped | Sus reservas, reservar por el portal, su folio, cancelar |
| `servicio` | `servicio123` | Personal de servicio | Preparación de apartamentos y novedades |

## Pantallas

| Ruta | Pantalla | API que usa |
|---|---|---|
| `/login` | Inicio de sesión | `POST /api/auth/login` |
| `/registro` | Registro público de huéspedes (7.2) | `POST /api/auth/registro` |
| `/recuperar-contrasena` · `/restablecer?token=` | Recuperar contraseña por correo (Brevo) | `POST /api/auth/recuperar-contrasena`, `POST /api/auth/restablecer-contrasena` |
| `/recepcion` | Operación del día: llegadas, salidas y resumen con IA (10.2) | `GET /api/operacion/resumen-diario` |
| `/recepcion/reservas` | Listado con filtros y paginación de 10 (7.5) | `GET /api/reservas` |
| `/recepcion/reservas/nueva` | Reserva por canal DIRECTO y registro de titular sin cuenta | `POST /api/reservas`, `POST /api/titulares` |
| `/recepcion/reservas/:codigo` | Detalle y acciones según el estado (sección 8) | confirmación, llegada, salida, cancelación, no-show |
| `/recepcion/folios/:codigo` | Folio: cargos, pagos, saldo, reversos y cierre (7.7) | `/api/folios/...` |
| `/recepcion/calendario` | Calendario mensual por apartamento | `GET /api/operacion/calendario` |
| `/recepcion/bloqueos` | Registrar, reprogramar y levantar bloqueos | `/api/bloqueos` |
| `/` · `/disponibilidad` · `/apartamentos/:id` | Sitio público: buscador, resultados y cotizador | `GET /api/disponibilidad`, `POST /api/cotizaciones` |
| `/huesped` · `/huesped/reservas/nueva` · `/huesped/reservas/:codigo` | Mis reservas, reserva por PORTAL, detalle con folio y cancelación | `/api/titulares/{codigo}/reservas`, `/api/reservas` |
| `/servicio` · `/servicio/novedades` | Preparación (iniciar/terminar) y novedades | `/api/apartamentos/preparacion`, `/api/novedades` |
| `/admin/alojamiento` · `/admin/politica` | Configuración, normas, servicios y política de cancelación | `/api/alojamiento` |
| `/admin/apartamentos` · `/admin/apartamentos/:id` | Alta, tarifas por temporada, capacidad, fuera de servicio, retiro | `/api/apartamentos` |
| `/admin/temporadas` · `/admin/reportes` | Temporadas y reportes de ocupación, ingresos y canales | `/api/temporadas`, `/api/reportes` |

## Estructura

```
src/
├── api/          Único lugar que habla con el backend (un archivo por recurso)
│   └── cliente.js  fetch + token + traducción del ErrorResponse
├── auth/         Sesión (AuthContext) y rutas protegidas por rol
├── components/   Piezas visuales reutilizables: Boton, Campo, Tarjeta, Modal, Toasts, Paginacion…
├── hooks/        useConsulta (cargar datos) y useApartamentos (nombres de los apartamentos)
├── layouts/      Marco de acceso (login/registro) y panel con menú lateral
├── pages/        Una pantalla por ruta, agrupadas por rol
├── utils/        Formato de pesos/fechas y textos del lenguaje ubicuo
└── styles/       Variables de color y estilos globales
```

## Decisiones

- **Ninguna regla de negocio vive aquí.** El frontend valida solo la forma (campos llenos, formato
  de correo, contraseña segura). Precios, solapes, capacidad, anticipo y transiciones los decide el
  dominio; si algo no procede, el backend responde 409 y la pantalla muestra su mensaje tal cual.
- **Errores con significado:** 400 marca los campos del formulario, 401 cierra la sesión y lleva al
  login ("Tu sesión expiró"), 403 muestra una página de permiso, 404 un estado vacío y 409 el mensaje
  del dominio.
- **Token en `sessionStorage`**, no en `localStorage`: sobrevive a un F5 y se borra al cerrar la
  pestaña (Guía 12, 3.8).
- **Ocultar botones es comodidad, no seguridad.** La protección real son los `@PreAuthorize` y la
  verificación por propietario del backend.

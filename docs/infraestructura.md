# Infraestructura del SGA

En la arquitectura hexagonal, **infraestructura** es el borde que habla con el mundo exterior. El dominio no conoce Spring, HTTP ni la base de datos. Esta capa implementa los puertos (repositorios, reloj, generador de códigos, notificaciones) y traduce entre HTTP, JPA y los tipos del dominio.

El paquete es `co.edu.uniquindio.sga.infrastructure`.

## Cómo entra una petición

1. Spring Security deja pasar la ruta o exige un JWT.
2. El controller del recurso recibe el JSON o los query params.
3. `SolicitudApiMapper` (MapStruct) convierte el DTO en tipos del dominio: `Estancia`, `Dinero`, `Ocupante`, códigos.
4. El caso de uso, anotado con `@Service` y `@Transactional`, aplica la regla de negocio.
5. El adaptador del repositorio guarda o lee con JPA y `PersistenciaMappers` reconstruye el agregado.
6. `RespuestaApiMapper` arma el JSON de salida. Un alta responde **201** con cabecera `Location`.
7. Si algo falla, `ManejadorExcepcionesGlobal` elige el código HTTP. El detalle de un error no previsto se escribe en el log y el cliente recibe un mensaje genérico.

Los `@Bean` de esta capa existen solo para lo que el dominio no puede anotar: los servicios de dominio y el `Clock`. Los casos de uso no se registran aquí.

## Paquetes

| Paquete | Rol |
| --- | --- |
| `config` | Beans de dominio, reloj, caché, ficha (`sga.ficha`) y OpenAPI |
| `security` | JWT sin sesión, roles y usuarios semilla |
| `web` | Controllers, MapStruct de entrada, mapeo de salida y cabecera `Location` |
| `web.exception` | Traducción de excepciones a HTTP |
| `persistence` | Entidades JPA, consultas, adaptadores, mapeo y semilla |
| `notification` | Vencimiento de reservas pendientes y envío de recordatorios |
| `integration` | Pago simulado, geocodificación, eventos de Spring y SMS/correo |
| `filesystem` | Galería en disco local |
| `observability` | Salud de negocio: alojamiento y política vigente |
| `exception` | Fallos técnicos (no son reglas de dominio) |

## Persistencia

Flyway crea el esquema (`V1__esquema_sga.sql`) y Hibernate lo valida (`ddl-auto=validate`). No se usa `ddl-auto=update` cuando hay un perfil activo.

Las colecciones ya no se guardan como JSON en un `@Lob`. Cada lista es una `@ElementCollection` y cada valor compuesto es un `@Embeddable`:

- `DineroEmbeddable` guarda pesos enteros en `DECIMAL(19,0)`. El nombre de la columna cambia en cada uso.
- `EstanciaEmbeddable` guarda el rango `[entrada, salida)`. En una reserva las columnas son `fecha_entrada` y `fecha_salida`. En un bloqueo se renombran a `fecha_inicio` y `fecha_fin`.
- Ocupantes, desglose, tarifas, temporadas, normas, servicios, cargos, pagos y tramos tienen su propia tabla. `@OrderColumn(name = "indice")` conserva el orden de la lista.
- Los periodos de una temporada no van anidados dentro de otra colección. Viven en `PeriodoTemporadaEmbeddable` y el mapper los agrupa por `idTemporada`.

Las colecciones son perezosas. Por eso el adaptador abre una transacción de solo lectura en la clase: la sesión sigue abierta mientras el mapper recorre la lista. `guardar` declara su propia `@Transactional` de escritura y pisa la de lectura.

El listado de reservas usa `Pageable`, orden `fechaCreacion DESC` y 10 elementos (`Pagina.TAMANO`). El puerto del dominio devuelve `Pagina`. El controller lo envuelve en `PaginaResponse` para no sacar un DTO de aplicación hacia el dominio. Si el filtro no trae fechas, el adaptador usa 1900-01-01 y 9999-12-31 para no dejar el parámetro en nulo.

La consulta JPQL filtra por `r.estancia.fechaEntrada` porque la fecha ya no es una columna suelta de `ReservaJpaEntity`.

## Semilla

`DatosInicialesLoader` corre al arrancar, antes que los usuarios. Si no hay alojamiento, crea Santamaría con las temporadas BASE, ALTA y MITAD, la política `POL-SGA` y los apartamentos APT-101 y APT-102. Los horarios y plazos salen de `FichaProperties`, no de números escritos en el loader.

`run` no lleva `@Transactional`. La comprobación de “¿ya existe?” llama a un adaptador transaccional que lanza `EntidadNoEncontradaException` cuando la tabla está vacía. Si esa llamada ocurriera dentro de la misma transacción del sembrado, Spring marcaría la transacción para rollback y el arranque fallaría aunque la semilla se hubiera insertado.

## Configuración de la ficha

`application.yml`, prefijo `sga.ficha`:

| Propiedad | Valor de desarrollo | Significado |
| --- | --- | --- |
| `hora-entrada` | 15:00 | Check-in |
| `hora-salida` | 11:00 | Check-out |
| `tiempo-preparacion-horas` | 3 | Tiempo entre una salida y la siguiente entrada |
| `plazo-confirmacion-horas` | 24 | Plazo para confirmar una reserva pendiente |
| `hora-limite-no-show` | 18:00 | Tope de no-show; debe ser posterior al check-in |
| `umbral-edad-facturable` | 5 | Edad desde la que un ocupante cuenta para la tarifa |
| `porcentaje-anticipo` | 30 | Anticipo exigido |
| `antelacion-recordatorio-entrada-horas` | 24 | Recordatorio de llegada |
| `antelacion-recordatorio-salida-horas` | 4 | Recordatorio de salida |

`FichaProperties.configuracion()` arma el `ConfiguracionAlojamiento` del dominio. Al actualizar la ficha por la API, la preparación viaja en minutos (`tiempoPreparacionMinutos`) porque una duración de 2 h 30 min no cabe en un entero de horas.

## Errores HTTP

| Situación | Código | Qué ve el cliente |
| --- | --- | --- |
| Cuerpo o parámetros inválidos | 400 | Campos que fallaron |
| Sin JWT o JWT inválido | 401 | Debe iniciar sesión |
| Rol insuficiente | 403 | Acceso denegado |
| No existe el recurso | 404 | Mensaje de la excepción de aplicación |
| Regla de dominio o conflicto (duplicado, solape) | 409 | Mensaje de la regla |
| Fallo técnico controlado | 500 | Mensaje de `InfraestructuraException` |
| Cualquier otra excepción | 500 | “Ocurrió un error interno…”. El stack queda solo en el log |

## Seguridad

La sesión es stateless. El filtro JWT corre antes del filtro de usuario y contraseña. CSRF está apagado porque no hay cookie de sesión. CORS expone `Authorization` y `Location` para que el navegador pueda leer el alta.

Rutas públicas:

- `POST /api/auth/**`
- `GET /api/disponibilidad`, `GET /api/alojamiento`, `GET /api/apartamentos` (la ruta exacta, no los subcaminos)
- `POST /api/cotizaciones`
- Swagger, `/v3/api-docs`, `/api/docs`, salud de Actuator y la consola H2

El resto exige un usuario autenticado. `@PreAuthorize` afina el rol: reportes y canales son de ADMIN, la preparación del apartamento también la hace SERVICIO, el huésped consulta su historial y su folio, y el canal concilia con la cabecera `X-Canal-Credencial`.

Usuarios de desarrollo, creados solo si la tabla está vacía: `admin` / `admin123`, `recepcion` / `recepcion123`, `servicio` / `servicio123`, `canal` / `canal123`, `huesped` / `huesped123` (titular `TIT-001`).

## API por recurso

Hay un controller por recurso. Las consultas van en GET con query params. El código del recurso va en el path (`POST /api/reservas/{codigo}/confirmacion`). Lo que crea un recurso responde 201 y `Location`.

| Recurso | Base | Operaciones |
| --- | --- | --- |
| Reservas | `/api/reservas` | Crear, consultar, listar, filtrar por estado, historial del titular, llegadas, salidas, confirmar, modificar, cancelar, llegada, salida, no-show y vencer pendientes |
| Disponibilidad | `GET /api/disponibilidad` | Pública. Fechas, nacimientos y filtros opcionales. Los nombres de ocupante no viajan en el GET: el mapper usa “Ocupante N” para conservar edad y cantidad |
| Cotización | `POST /api/cotizaciones` | Pública. Sigue en POST porque el cuerpo no cabe bien en una query |
| Calendario | `GET /api/calendario` | Identificación del apartamento y rango |
| Apartamentos | `/api/apartamentos` | Alta, listado activo, pendientes de preparación, ficha, datos, activación, retiro, capacidad, tarifas, preparación, fuera de servicio y reintegro |
| Alojamiento | `/api/alojamiento` | Ficha, datos, configuración, normas, servicios y temporadas |
| Folios | `/api/folios` | Consulta, folio propio, pagos, servicios, reverso de cargo o pago, y cierre |
| Titulares | `/api/titulares` | Alta y actualización de contacto |
| Canales | `/api/canales` | Alta, listado, activar, desactivar, rotar credencial, conciliar, cancelar reserva externa y conflictos |
| Bloqueos | `/api/bloqueos` | Alta, consulta por apartamento, reprogramar y levantar |
| Novedades | `/api/novedades` | Alta (cabecera `X-Autor`) y consulta por apartamento |
| Políticas | `/api/politicas` | Vigente y alta de una versión nueva |
| Recordatorios | `/api/reservas/{codigo}/recordatorios` | Programar y listar |
| Reportes | `/api/reportes` | Ingresos, ocupación y reservas por canal. Solo ADMIN |
| Auth | `/api/auth` | Login. Devuelve token, usuario, rol y nombre |
| Docs | `GET /api/docs` | Texto que apunta a Swagger |

Swagger UI: `/swagger-ui.html`. Especificación generada: `/v3/api-docs`. `docs/api/openapi.yaml` es una referencia; la fuente viva es springdoc. `docs/api/sga-reservas.http` recorre login, 401, 403, alta, solape 409 y disponibilidad pública.

El cierre de folio y la salida piden el motivo en el cuerpo y el autor en `X-Autor`.

## Tareas programadas

- `VencimientoReservasScheduler` llama `VencerReservasPendientesUseCase` con `LocalDateTime.now(reloj)`. El caso de uso no tiene un reloj propio: quien lo dispara entrega el instante.
- `EnvioRecordatoriosScheduler` toma los recordatorios programados cuya fecha ya llegó y los manda por el puerto de correo. Hoy el correo y el SMS escriben en el log. El pago y la geocodificación son sustitutos listos para cambiarse por un cliente HTTP.

## Qué no hace esta capa

No decide si una reserva se puede solapar, cuánto se cobra ni cuándo vence. Eso está en el dominio. Tampoco convierte DTOs dentro del caso de uso: esa conversión se queda en el controller.

# SGA Santamaría · Sistema de Gestión de Alojamiento

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-6DB33F)
![React](https://img.shields.io/badge/React-19-61DAFB)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1)
![Arquitectura](https://img.shields.io/badge/arquitectura-hexagonal-1E8449)

Sistema web para administrar **Santamaría**, un alojamiento turístico de apartamentos en Montenegro, Quindío:
reservas, folios, operación diaria, preparación de apartamentos, canales externos y administración, con un
sitio público para que los huéspedes consulten disponibilidad y reserven.

Proyecto final de **Programación Avanzada**, Universidad del Quindío.

| | Enlace |
|---|---|
| Aplicación | https://sga-front-santamaria.vercel.app |
| API | https://sga-api-santamaria.onrender.com |
| Estado del API | https://sga-api-santamaria.onrender.com/actuator/health |

> El API corre en el plan gratuito de Render: si lleva un rato sin uso, la primera petición puede tardar
> cerca de un minuto mientras el servicio despierta.

---

## Contenido

- [El alojamiento](#el-alojamiento)
- [Funcionalidades](#funcionalidades)
- [Arquitectura](#arquitectura)
- [Tecnologías](#tecnologías)
- [Ejecución local](#ejecución-local)
- [Variables de entorno](#variables-de-entorno)
- [Pruebas y documentación del API](#pruebas-y-documentación-del-api)
- [Despliegue](#despliegue)
- [Equipo](#equipo)

---

## El alojamiento

Santamaría está orientado a familias, parejas y grupos que buscan una estancia cómoda y tranquila: apartamentos
independientes con cocina y zona social, piscina y áreas comunes en medio del paisaje cultural cafetero. El cobro
es por persona y por noche según la temporada, y los menores de 3 años no pagan.

## Funcionalidades

| Rol | Qué puede hacer |
|---|---|
| **Visitante** | Buscar disponibilidad por fechas y grupo, ver cada apartamento con galería y mapa, cotizar noche por noche, registrarse y recuperar la contraseña por correo |
| **Huésped** | Reservar por el portal, ver sus reservas y su folio, cancelar según la política vigente, editar su perfil |
| **Recepción** | Operación del día con resumen asistido por IA, reservas por canal directo, confirmación, llegada, salida, no-show, folios (cargos, pagos, reversos, cierre), calendario y bloqueos |
| **Personal de servicio** | Preparar los apartamentos después de cada salida y registrar novedades |
| **Administrador** | Configuración del alojamiento, apartamentos y galería, temporadas y tarifas, política de cancelación, usuarios y roles, bitácora de canales externos y reportes de ocupación, ingresos y canales |
| **Canal externo** | Enviar reservas que se concilian contra la disponibilidad real, sin duplicar mensajes repetidos |

Integraciones: correo transaccional con **Brevo**, imágenes en **Cloudinary** con respaldo local, mapa con
**Mapbox** u **OpenStreetMap**, y resumen diario con un **modelo de lenguaje** que, si falla, no detiene la operación.

## Arquitectura

El backend sigue una **arquitectura hexagonal** (puertos y adaptadores): las reglas del negocio viven en el dominio
y no dependen de Spring, de la base de datos ni de los servicios externos.

```
src/main/java/co/edu/uniquindio/sga
├── domain/           Entidades, objetos de valor, servicios de dominio, repositorios y puertos
├── application/      Casos de uso, DTOs y excepciones de aplicación
└── infrastructure/   Adaptadores: REST, persistencia JPA, seguridad JWT, correo, imágenes, IA, configuración

frontend/src
├── api/              Cliente HTTP, un archivo por recurso
├── auth/             Sesión y rutas protegidas por rol
├── components/       Componentes reutilizables
├── pages/            Pantallas agrupadas por rol
└── layouts/          Sitio público, acceso y panel
```

El frontend no repite reglas de negocio: valida solo la forma de los datos y muestra tal cual los mensajes del
dominio que llegan del API.

## Tecnologías

| Capa | Tecnologías |
|---|---|
| Backend | Java 21, Spring Boot 4.1, Spring Security con JWT, Spring Data JPA, Flyway, MapStruct, SpringDoc OpenAPI |
| Base de datos | MySQL 8 (Docker en local) · TiDB Cloud, compatible con MySQL, en producción · H2 para desarrollo y pruebas |
| Frontend | React 19, Vite, React Router, Lucide, Mapbox GL |
| Infraestructura | Docker, Render (API), Vercel (frontend) |

## Ejecución local

### Requisitos

- JDK 21
- Node.js 20 o superior
- Docker Desktop (solo para usar MySQL)

### 1. Clonar

```bash
git clone https://github.com/Santiago-Bayona/sga-santamaria.git
cd sga-santamaria
```

### 2. Backend

**Opción rápida, con H2 en memoria.** No necesita Docker ni configuración:

```bash
./gradlew bootRun
```

**Con MySQL en Docker.** Cree un archivo `.env` en la raíz con las credenciales de MySQL
(`MYSQL_ROOT_PASSWORD`, `MYSQL_DATABASE`, `MYSQL_USER`, `MYSQL_PASSWORD`, `MYSQL_PORT`), levante el contenedor y
ejecute el backend con el perfil `mysql`:

```bash
docker compose up -d
```

```powershell
$env:SGA_PERFIL="mysql"
.\gradlew bootRun
```

El API queda en http://localhost:8080. Flyway crea las tablas y, la primera vez, se cargan el alojamiento, los
apartamentos, las temporadas y los usuarios de prueba.

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

La aplicación queda en http://localhost:5173.

### Usuarios de prueba (solo en local)

| Usuario | Contraseña | Rol |
|---|---|---|
| `admin` | `admin123` | Administrador |
| `recepcion` | `recepcion123` | Recepción |
| `servicio` | `servicio123` | Personal de servicio |
| `huesped` / `huesped2` | `huesped123` | Huésped |
| `canal` | `canal123` | Canal externo |

## Variables de entorno

Ninguna clave ni URL de producción está en el repositorio: todo se configura con variables de entorno o con el
archivo local `application-secrets.yml` (ignorado por Git; la plantilla es `application-secrets.yml.example`).

| Variable | Para qué |
|---|---|
| `SGA_PERFIL` | Perfil de Spring: `dev` (H2, por defecto), `mysql` o `prod` |
| `SGA_DB_URL`, `SGA_DB_USER`, `SGA_DB_PASSWORD` | Conexión a MySQL |
| `SGA_JWT_SECRET`, `SGA_JWT_EXP_MIN` | Firma y duración del token |
| `SGA_CORS_ORIGINS` | Orígenes del frontend permitidos |
| `SGA_CORREO_PROVEEDOR`, `SGA_CORREO_REMITENTE`, `SGA_BREVO_CLAVE` | Correo real con Brevo (sin ellas, el correo se escribe en el log) |
| `SGA_IMAGENES_PROVEEDOR`, `SGA_CLOUDINARY_*` | Imágenes en Cloudinary (sin ellas, se guardan en disco) |
| `SGA_IA_*` | Proveedor, modelo y clave del resumen diario con IA |
| `SGA_FRONTEND_URL` | URL del frontend para los enlaces de los correos |
| `SGA_ADMIN_*` | Cuenta de administración inicial en producción |

En el frontend, `frontend/.env.development` define `VITE_API_URL` y, opcionalmente, `VITE_MAPBOX_TOKEN`
(plantilla en `frontend/.env.example`).

## Pruebas y documentación del API

```bash
./gradlew test
```

Las pruebas corren con H2 en memoria: no necesitan Docker ni claves.

- **Swagger UI** (perfil local): http://localhost:8080/swagger-ui.html
- **Contrato OpenAPI:** [`docs/api/openapi.yaml`](docs/api/openapi.yaml)
- **Peticiones de prueba:** [`docs/api/sga-reservas.http`](docs/api/sga-reservas.http), incluido el simulador de
  canal externo con sus tres escenarios: reserva aceptada, conflicto rechazado y mensaje repetido.

## Despliegue

| Pieza | Plataforma | Notas |
|---|---|---|
| Frontend | Vercel (carpeta `frontend`) | Llama al API con `VITE_API_URL` |
| API | Render (Docker, rama `main`) | Imagen con Java 21 definida en el `Dockerfile` |
| Base de datos | TiDB Cloud Starter (compatible con MySQL) | Conexión por `SGA_DB_URL` con TLS; se crea con las migraciones de Flyway |

En producción Swagger y la consola de H2 quedan desactivados.

## Equipo

| Integrante |
|---|
| Laura Sofía Osorio Medina |
| Cesar Santiago Bayona Peñuela |
| Sofía Santamaría Rendón |

Universidad del Quindío · Ingeniería de Sistemas y Computación · Programación Avanzada

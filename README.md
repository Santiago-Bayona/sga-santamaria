# SGA - Santamaria (Sistema de Gestión de Alojamiento)

Proyecto desarrollado para el curso de **Programación Avanzada** de la Universidad del Quindío.

## Descripción del Alojamiento
Santamaria es un alojamiento turístico de Montenegro, Quindío, orientado a familias, parejas y grupos que buscan una estancia cómoda y tranquila, con apartamentos independientes, piscina y espacios adecuados para disfrutar del entorno natural del Quindío.

##  Integrantes del Equipo
* Laura Sofia Osorio Medina
* Cesar Santiago Bayona Peñuela
* Sofia Santamaria Rendon

##  Requisitos para ejecutar
* **Java Development Kit (JDK):** Versión 21.
* **Sistema de construcción:** Gradle Wrapper (incluido en el proyecto).

##  Instrucciones de ejecución

1. Clonar el repositorio:
   ```bash
   git clone [https://github.com/TU-USUARIO/sga-santamaria.git](https://github.com/TU-USUARIO/sga-santamaria.git)
   cd sga-santamaria
   ```

## Despliegue

El mismo código corre en local y en la nube. Lo que cambia son variables de entorno: no hay URLs ni secretos en el repositorio.

| Pieza | Dónde | URL prevista |
| --- | --- | --- |
| Aplicación | Vercel, carpeta `frontend` | https://sga-front-santamaria.vercel.app |
| API | Render, Docker, rama `main` | https://sga-api-santamaria.onrender.com |
| Salud | Render | https://sga-api-santamaria.onrender.com/actuator/health |

Esas URLs usan el nombre sugerido del servicio. Si al crearlos en la plataforma queda otro nombre, hay que actualizar esta tabla y la variable `VITE_API_URL`.

El plan gratuito de Render duerme el servicio tras unos 15 minutos sin uso: la primera petición después puede tardar cerca de un minuto, y la base H2 en archivo vuelve al estado inicial (se pierden las reservas creadas). Por eso en Render hay que dejar `SGA_USUARIOS_INICIALES_HABILITADOS=true`, para que al despertar exista de nuevo la cuenta de administración. La contraseña de esa cuenta solo vive en las variables de Render.

En producción Swagger y la consola H2 quedan apagados: `/swagger-ui.html` responde 401. El perfil local `dev` sigue con H2 en memoria, usuarios de prueba y Swagger en `/swagger-ui.html`.

La imagen de Docker usa Java 21, la misma versión del toolchain de Gradle.

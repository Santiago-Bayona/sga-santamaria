-- Esquema H2 del SGA. Flyway es el dueño de la BD; Hibernate solo valida.

CREATE TABLE titulares (
    codigo            VARCHAR(64)  NOT NULL PRIMARY KEY,
    nombre            VARCHAR(255) NOT NULL,
    fecha_nacimiento  DATE         NOT NULL,
    telefono          VARCHAR(40)  NOT NULL,
    correo            VARCHAR(255) NOT NULL
);

CREATE TABLE apartamentos (
    identificacion          VARCHAR(64)   NOT NULL PRIMARY KEY,
    nombre                  VARCHAR(255)  NOT NULL,
    descripcion             VARCHAR(2000) NOT NULL,
    dormitorios             INT           NOT NULL,
    capacidad               INT           NOT NULL,
    estado_operativo        VARCHAR(40)   NOT NULL,
    activo                  BOOLEAN       NOT NULL,
    indice_principal        INT           NOT NULL
);

CREATE TABLE alojamientos (
    codigo                                   VARCHAR(64)    NOT NULL PRIMARY KEY,
    nombre                                   VARCHAR(255)   NOT NULL,
    descripcion                              VARCHAR(2000)  NOT NULL,
    ciudad                                   VARCHAR(255)   NOT NULL,
    direccion                                VARCHAR(255)   NOT NULL,
    latitud                                  DECIMAL(10, 7) NOT NULL,
    longitud                                 DECIMAL(10, 7) NOT NULL,
    hora_entrada                             TIME           NOT NULL,
    hora_salida                              TIME           NOT NULL,
    tiempo_preparacion_horas                 BIGINT         NOT NULL,
    plazo_confirmacion_horas                 INT            NOT NULL,
    hora_limite_noshow                       TIME           NOT NULL,
    umbral_edad_facturable                   INT            NOT NULL,
    porcentaje_anticipo                      INT            NOT NULL,
    antelacion_recordatorio_entrada_horas    BIGINT         NOT NULL,
    antelacion_recordatorio_salida_horas     BIGINT         NOT NULL
);

CREATE TABLE reservas (
    codigo                      VARCHAR(64)  NOT NULL PRIMARY KEY,
    codigo_titular              VARCHAR(64)  NOT NULL,
    nombre_titular              VARCHAR(255) NOT NULL,
    nacimiento_titular          DATE         NOT NULL,
    version_politica            INT          NOT NULL,
    canal_origen                VARCHAR(20)  NOT NULL,
    identificador_externo       VARCHAR(120),
    fecha_creacion              TIMESTAMP    NOT NULL,
    identificacion_apartamento  VARCHAR(64)  NOT NULL,
    fecha_entrada               DATE         NOT NULL,
    fecha_salida                DATE         NOT NULL,
    hora_llegada_estimada       TIME,
    estado                      VARCHAR(20)  NOT NULL,
    mascotas                    INT          NOT NULL DEFAULT 0,
    momento_registro            TIMESTAMP,
    momento_salida              TIMESTAMP,
    motivo_cancelacion          VARCHAR(500)
);

CREATE INDEX idx_reserva_estado ON reservas (estado);
CREATE INDEX idx_reserva_apartamento ON reservas (identificacion_apartamento);
CREATE INDEX idx_reserva_titular ON reservas (codigo_titular);
CREATE INDEX idx_reserva_ext ON reservas (identificador_externo);

CREATE TABLE folios (
    codigo          VARCHAR(64) NOT NULL PRIMARY KEY,
    codigo_reserva  VARCHAR(64) NOT NULL UNIQUE,
    cerrado         BOOLEAN     NOT NULL,
    autor_cierre    VARCHAR(255),
    motivo_cierre   VARCHAR(500)
);

CREATE UNIQUE INDEX idx_folio_reserva ON folios (codigo_reserva);

CREATE TABLE bloqueos (
    codigo                      VARCHAR(64)  NOT NULL PRIMARY KEY,
    identificacion_apartamento  VARCHAR(64)  NOT NULL,
    fecha_registro              TIMESTAMP    NOT NULL,
    fecha_inicio                DATE         NOT NULL,
    fecha_fin                   DATE         NOT NULL,
    motivo                      VARCHAR(500) NOT NULL,
    vigente                     BOOLEAN      NOT NULL
);

CREATE TABLE canales_externos (
    codigo      VARCHAR(64)  NOT NULL PRIMARY KEY,
    tipo        VARCHAR(255) NOT NULL,
    credencial  VARCHAR(255) NOT NULL,
    activo      BOOLEAN      NOT NULL
);

CREATE TABLE conflictos_canal (
    codigo                      VARCHAR(64)  NOT NULL PRIMARY KEY,
    canal                       VARCHAR(20)  NOT NULL,
    identificador_externo       VARCHAR(120) NOT NULL,
    identificacion_apartamento  VARCHAR(64)  NOT NULL,
    fecha_entrada               DATE         NOT NULL,
    fecha_salida                DATE         NOT NULL,
    codigo_reserva_vigente      VARCHAR(64)  NOT NULL,
    fecha_deteccion             TIMESTAMP    NOT NULL,
    revisado                    BOOLEAN      NOT NULL,
    nota_revision               VARCHAR(1000)
);

CREATE TABLE novedades (
    codigo                      VARCHAR(64)   NOT NULL PRIMARY KEY,
    identificacion_apartamento  VARCHAR(64)   NOT NULL,
    fecha                       TIMESTAMP     NOT NULL,
    autor                       VARCHAR(255)  NOT NULL,
    descripcion                 VARCHAR(2000) NOT NULL,
    gravedad                    VARCHAR(20)   NOT NULL,
    resuelta                    BOOLEAN       NOT NULL,
    fecha_resolucion            TIMESTAMP
);

CREATE TABLE politicas_cancelacion (
    codigo            VARCHAR(64) NOT NULL,
    version           INT         NOT NULL,
    vigente           BOOLEAN     NOT NULL,
    retencion_noshow  INT         NOT NULL,
    creada_en         TIMESTAMP   NOT NULL,
    PRIMARY KEY (codigo, version)
);

CREATE TABLE recordatorios (
    codigo              VARCHAR(120) NOT NULL PRIMARY KEY,
    codigo_reserva      VARCHAR(64)  NOT NULL,
    tipo                VARCHAR(20)  NOT NULL,
    momento_evento      TIMESTAMP    NOT NULL,
    fecha_recordatorio  TIMESTAMP    NOT NULL,
    estado              VARCHAR(20)  NOT NULL,
    ultimo_error        VARCHAR(1000)
);

CREATE TABLE secuencias_codigo (
    nombre  VARCHAR(64) NOT NULL PRIMARY KEY,
    valor   BIGINT      NOT NULL
);

CREATE TABLE apartamento_imagenes (
    identificacion  VARCHAR(64)  NOT NULL,
    indice          INT          NOT NULL,
    url             VARCHAR(500) NOT NULL,
    PRIMARY KEY (identificacion, indice),
    CONSTRAINT fk_imagen_apartamento FOREIGN KEY (identificacion) REFERENCES apartamentos (identificacion)
);

CREATE TABLE apartamento_dotacion (
    identificacion  VARCHAR(64)  NOT NULL,
    indice          INT          NOT NULL,
    elemento        VARCHAR(255) NOT NULL,
    PRIMARY KEY (identificacion, indice),
    CONSTRAINT fk_dotacion_apartamento FOREIGN KEY (identificacion) REFERENCES apartamentos (identificacion)
);

CREATE TABLE apartamento_tarifas (
    identificacion      VARCHAR(64) NOT NULL,
    indice              INT         NOT NULL,
    temporada           VARCHAR(64) NOT NULL,
    valor_por_ocupante  DECIMAL(19, 0) NOT NULL,
    vigente_desde       TIMESTAMP   NOT NULL,
    PRIMARY KEY (identificacion, indice),
    CONSTRAINT fk_tarifa_apartamento FOREIGN KEY (identificacion) REFERENCES apartamentos (identificacion)
);

CREATE TABLE alojamiento_temporadas (
    codigo_alojamiento  VARCHAR(64)  NOT NULL,
    indice              INT          NOT NULL,
    id_temporada        VARCHAR(64)  NOT NULL,
    nombre              VARCHAR(255) NOT NULL,
    base                BOOLEAN      NOT NULL,
    PRIMARY KEY (codigo_alojamiento, indice),
    CONSTRAINT fk_temporada_alojamiento FOREIGN KEY (codigo_alojamiento) REFERENCES alojamientos (codigo)
);

CREATE TABLE alojamiento_periodos (
    codigo_alojamiento  VARCHAR(64) NOT NULL,
    indice              INT         NOT NULL,
    id_temporada        VARCHAR(64) NOT NULL,
    mes_inicio          INT         NOT NULL,
    dia_inicio          INT         NOT NULL,
    mes_fin             INT         NOT NULL,
    dia_fin             INT         NOT NULL,
    PRIMARY KEY (codigo_alojamiento, indice),
    CONSTRAINT fk_periodo_alojamiento FOREIGN KEY (codigo_alojamiento) REFERENCES alojamientos (codigo)
);

CREATE TABLE alojamiento_normas (
    codigo_alojamiento  VARCHAR(64)  NOT NULL,
    indice              INT          NOT NULL,
    texto               VARCHAR(500) NOT NULL,
    PRIMARY KEY (codigo_alojamiento, indice),
    CONSTRAINT fk_norma_alojamiento FOREIGN KEY (codigo_alojamiento) REFERENCES alojamientos (codigo)
);

CREATE TABLE alojamiento_servicios (
    codigo_alojamiento  VARCHAR(64)    NOT NULL,
    indice              INT            NOT NULL,
    nombre              VARCHAR(255)   NOT NULL,
    valor               DECIMAL(19, 0) NOT NULL,
    genera_cargo        BOOLEAN        NOT NULL,
    PRIMARY KEY (codigo_alojamiento, indice),
    CONSTRAINT fk_servicio_alojamiento FOREIGN KEY (codigo_alojamiento) REFERENCES alojamientos (codigo)
);

CREATE TABLE reserva_ocupantes (
    codigo_reserva    VARCHAR(64)  NOT NULL,
    indice            INT          NOT NULL,
    nombre            VARCHAR(255) NOT NULL,
    fecha_nacimiento  DATE         NOT NULL,
    PRIMARY KEY (codigo_reserva, indice),
    CONSTRAINT fk_ocupante_reserva FOREIGN KEY (codigo_reserva) REFERENCES reservas (codigo)
);

CREATE TABLE reserva_desglose (
    codigo_reserva          VARCHAR(64)    NOT NULL,
    indice                  INT            NOT NULL,
    fecha                   DATE           NOT NULL,
    temporada               VARCHAR(64)    NOT NULL,
    tarifa_aplicada         DECIMAL(19, 0) NOT NULL,
    ocupantes_facturables   INT            NOT NULL,
    subtotal                DECIMAL(19, 0) NOT NULL,
    PRIMARY KEY (codigo_reserva, indice),
    CONSTRAINT fk_desglose_reserva FOREIGN KEY (codigo_reserva) REFERENCES reservas (codigo)
);

CREATE TABLE folio_cargos (
    codigo_folio  VARCHAR(64)    NOT NULL,
    indice        INT            NOT NULL,
    numero        INT            NOT NULL,
    tipo          VARCHAR(40)    NOT NULL,
    concepto      VARCHAR(255)   NOT NULL,
    monto         DECIMAL(19, 0) NOT NULL,
    fecha         DATE           NOT NULL,
    resta         BOOLEAN        NOT NULL,
    revierte_a    INT,
    PRIMARY KEY (codigo_folio, indice),
    CONSTRAINT fk_cargo_folio FOREIGN KEY (codigo_folio) REFERENCES folios (codigo)
);

CREATE TABLE folio_pagos (
    codigo_folio  VARCHAR(64)    NOT NULL,
    indice        INT            NOT NULL,
    numero        INT            NOT NULL,
    monto         DECIMAL(19, 0) NOT NULL,
    medio         VARCHAR(40)    NOT NULL,
    fecha         DATE           NOT NULL,
    revierte_a    INT,
    PRIMARY KEY (codigo_folio, indice),
    CONSTRAINT fk_pago_folio FOREIGN KEY (codigo_folio) REFERENCES folios (codigo)
);

CREATE TABLE politica_tramos (
    codigo                   VARCHAR(64) NOT NULL,
    version                  INT         NOT NULL,
    indice                   INT         NOT NULL,
    dias_antelacion_minima   INT         NOT NULL,
    retencion                INT         NOT NULL,
    PRIMARY KEY (codigo, version, indice),
    CONSTRAINT fk_tramo_politica FOREIGN KEY (codigo, version) REFERENCES politicas_cancelacion (codigo, version)
);

CREATE TABLE usuarios (
    username          VARCHAR(80)  NOT NULL PRIMARY KEY,
    password_hash     VARCHAR(255) NOT NULL,
    rol               VARCHAR(30)  NOT NULL,
    activo            BOOLEAN      NOT NULL,
    codigo_titular    VARCHAR(64),
    nombre_completo   VARCHAR(255) NOT NULL
);

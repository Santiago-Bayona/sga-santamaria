CREATE TABLE bitacora_canal (
    codigo                  VARCHAR(80)  NOT NULL PRIMARY KEY,
    codigo_canal            VARCHAR(64)  NOT NULL,
    identificador_externo   VARCHAR(120) NOT NULL,
    ocurrido_en             TIMESTAMP    NOT NULL,
    payload                 VARCHAR(500) NOT NULL,
    resultado               VARCHAR(30)  NOT NULL,
    motivo                  VARCHAR(500) NOT NULL
);

CREATE INDEX idx_bitacora_canal_ocurrido ON bitacora_canal (ocurrido_en);

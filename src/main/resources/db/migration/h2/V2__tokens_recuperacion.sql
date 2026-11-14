-- Enlaces de recuperación de contraseña. Se guarda el SHA-256 del token (64 hex), nunca el token.
CREATE TABLE tokens_recuperacion (
    token_hash  VARCHAR(64) NOT NULL PRIMARY KEY,
    username    VARCHAR(80) NOT NULL,
    creado_en   TIMESTAMP   NOT NULL,
    expira_en   TIMESTAMP   NOT NULL,
    usado       BOOLEAN     NOT NULL,
    CONSTRAINT fk_token_recuperacion_usuario FOREIGN KEY (username) REFERENCES usuarios (username)
);

CREATE INDEX idx_token_recuperacion_usuario ON tokens_recuperacion (username);

CREATE TABLE IF NOT EXISTS configurations (
    id               BIGSERIAL    PRIMARY KEY,
    cle              VARCHAR(100) NOT NULL UNIQUE,
    valeur           TEXT         NOT NULL DEFAULT 'false',
    description      VARCHAR(255),
    date_modification TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO configurations (cle, valeur, description)
VALUES ('SCAN_CAMERA_ENABLED', 'false', 'Activer le scanner caméra dans la page contrôle accès réfectoire')
ON CONFLICT (cle) DO NOTHING;

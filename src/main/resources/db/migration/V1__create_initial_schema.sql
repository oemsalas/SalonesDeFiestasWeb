-- V1__create_initial_schema.sql

CREATE TYPE estado_reserva AS ENUM ('PENDIENTE', 'CONFIRMADA', 'CANCELADA', 'FINALIZADA');
CREATE TYPE estado_salon AS ENUM ('ACTIVO', 'INACTIVO', 'MANTENIMIENTO');
CREATE TYPE tipo_bloqueo AS ENUM ('MANTENIMIENTO', 'FERIADO', 'INTERNO', 'OTRO');
CREATE TYPE metodo_pago AS ENUM ('EFECTIVO', 'TRANSFERENCIA', 'TARJETA_DEBITO', 'TARJETA_CREDITO');
CREATE TYPE tipo_pago AS ENUM ('SEÑA', 'SALDO', 'REEMBOLSO');
CREATE TYPE categoria_servicio AS ENUM ('CATERING', 'DECORACION', 'MUSICA', 'FOTOGRAFIA', 'LIMPIEZA', 'SEGURIDAD', 'OTRO');

-- Cliente
CREATE TABLE cliente (
    id          BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    apellido    VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    telefono    VARCHAR(20)  NOT NULL,
    dni         VARCHAR(20)  NOT NULL UNIQUE,
    creado_en   TIMESTAMP    NOT NULL DEFAULT NOW(),
    activo      BOOLEAN      NOT NULL DEFAULT TRUE
);

-- Salón
CREATE TABLE salon (
    id              BIGSERIAL PRIMARY KEY,
    nombre          VARCHAR(150) NOT NULL,
    capacidad_max   INT          NOT NULL CHECK (capacidad_max > 0),
    descripcion     TEXT,
    precio_hora     NUMERIC(10,2) NOT NULL CHECK (precio_hora >= 0),
    estado          estado_salon NOT NULL DEFAULT 'ACTIVO',
    creado_en       TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Configuración del salón
CREATE TABLE configuracion_salon (
    id                      BIGSERIAL PRIMARY KEY,
    salon_id                BIGINT NOT NULL UNIQUE REFERENCES salon(id),
    min_horas_reserva       INT    NOT NULL DEFAULT 2,
    max_horas_reserva       INT    NOT NULL DEFAULT 12,
    anticipacion_min_dias   INT    NOT NULL DEFAULT 1,
    anticipacion_max_dias   INT    NOT NULL DEFAULT 365,
    permite_solapamiento    BOOLEAN NOT NULL DEFAULT FALSE
);

-- Horarios operativos del salón (0=Domingo, 1=Lunes ... 6=Sábado)
CREATE TABLE horario_salon (
    id          BIGSERIAL PRIMARY KEY,
    salon_id    BIGINT  NOT NULL REFERENCES salon(id),
    dia_semana  INT     NOT NULL CHECK (dia_semana BETWEEN 0 AND 6),
    apertura    TIME    NOT NULL,
    cierre      TIME    NOT NULL,
    activo      BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE (salon_id, dia_semana)
);

-- Bloqueos manuales del salón
CREATE TABLE bloqueo_salon (
    id          BIGSERIAL PRIMARY KEY,
    salon_id    BIGINT      NOT NULL REFERENCES salon(id),
    fecha_desde DATE        NOT NULL,
    fecha_hasta DATE        NOT NULL,
    hora_desde  TIME,
    hora_hasta  TIME,
    motivo      VARCHAR(255),
    tipo        tipo_bloqueo NOT NULL DEFAULT 'OTRO',
    creado_en   TIMESTAMP   NOT NULL DEFAULT NOW(),
    CHECK (fecha_hasta >= fecha_desde)
);

-- Reserva
CREATE TABLE reserva (
    id              BIGSERIAL       PRIMARY KEY,
    cliente_id      BIGINT          NOT NULL REFERENCES cliente(id),
    salon_id        BIGINT          NOT NULL REFERENCES salon(id),
    fecha_evento    DATE            NOT NULL,
    hora_inicio     TIME            NOT NULL,
    hora_fin        TIME            NOT NULL,
    estado          estado_reserva  NOT NULL DEFAULT 'PENDIENTE',
    seña_pagada     NUMERIC(10,2)   NOT NULL DEFAULT 0,
    precio_total    NUMERIC(10,2)   NOT NULL CHECK (precio_total >= 0),
    observaciones   TEXT,
    creado_en       TIMESTAMP       NOT NULL DEFAULT NOW(),
    CHECK (hora_fin > hora_inicio)
);

-- Servicios del catálogo
CREATE TABLE servicio (
    id          BIGSERIAL           PRIMARY KEY,
    nombre      VARCHAR(150)        NOT NULL,
    descripcion TEXT,
    precio      NUMERIC(10,2)       NOT NULL CHECK (precio >= 0),
    categoria   categoria_servicio  NOT NULL,
    activo      BOOLEAN             NOT NULL DEFAULT TRUE
);

-- Servicios incluidos en una reserva
CREATE TABLE reserva_servicio (
    reserva_id      BIGINT        NOT NULL REFERENCES reserva(id),
    servicio_id     BIGINT        NOT NULL REFERENCES servicio(id),
    cantidad        INT           NOT NULL DEFAULT 1 CHECK (cantidad > 0),
    precio_unitario NUMERIC(10,2) NOT NULL,
    PRIMARY KEY (reserva_id, servicio_id)
);

-- Pagos
CREATE TABLE pago (
    id          BIGSERIAL     PRIMARY KEY,
    reserva_id  BIGINT        NOT NULL REFERENCES reserva(id),
    fecha_pago  DATE          NOT NULL,
    monto       NUMERIC(10,2) NOT NULL CHECK (monto > 0),
    metodo      metodo_pago   NOT NULL,
    tipo        tipo_pago     NOT NULL,
    comprobante VARCHAR(100),
    creado_en   TIMESTAMP     NOT NULL DEFAULT NOW()
);

-- Personal
CREATE TABLE personal (
    id          BIGSERIAL    PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    apellido    VARCHAR(100) NOT NULL,
    rol         VARCHAR(80)  NOT NULL,
    telefono    VARCHAR(20),
    activo      BOOLEAN      NOT NULL DEFAULT TRUE
);

-- Asignación de personal a reservas
CREATE TABLE asignacion (
    reserva_id  BIGINT       NOT NULL REFERENCES reserva(id),
    personal_id BIGINT       NOT NULL REFERENCES personal(id),
    turno       VARCHAR(50),
    PRIMARY KEY (reserva_id, personal_id)
);

-- Proveedores
CREATE TABLE proveedor (
    id          BIGSERIAL    PRIMARY KEY,
    nombre      VARCHAR(150) NOT NULL,
    rubro       VARCHAR(100) NOT NULL,
    telefono    VARCHAR(20),
    email       VARCHAR(150),
    activo      BOOLEAN      NOT NULL DEFAULT TRUE
);

-- Contratos con proveedores por reserva
CREATE TABLE contrato_proveedor (
    id              BIGSERIAL     PRIMARY KEY,
    reserva_id      BIGINT        NOT NULL REFERENCES reserva(id),
    proveedor_id    BIGINT        NOT NULL REFERENCES proveedor(id),
    costo           NUMERIC(10,2) NOT NULL CHECK (costo >= 0),
    estado          VARCHAR(50)   NOT NULL DEFAULT 'PENDIENTE',
    notas           TEXT,
    creado_en       TIMESTAMP     NOT NULL DEFAULT NOW()
);

-- Índices para búsquedas frecuentes de disponibilidad
CREATE INDEX idx_reserva_salon_fecha  ON reserva(salon_id, fecha_evento, estado);
CREATE INDEX idx_bloqueo_salon_rango  ON bloqueo_salon(salon_id, fecha_desde, fecha_hasta);
CREATE INDEX idx_horario_salon_dia    ON horario_salon(salon_id, dia_semana);

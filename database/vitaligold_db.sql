DROP DATABASE IF EXISTS vitaligold_db;

CREATE DATABASE vitaligold_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE vitaligold_db;

-- 1. Tabla de roles
--    Los distintos tipos de acceso dentro del centro de salud.
CREATE TABLE roles (
    id_rol      INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol  VARCHAR(50) NOT NULL,

    CONSTRAINT uq_roles_nombre UNIQUE (nombre_rol)
);

-- 2. Tabla de usuarios
--    Guarda los datos personales y las credenciales de acceso,
--    ligados a un rol.
CREATE TABLE usuarios (
    id_usuario       INT AUTO_INCREMENT PRIMARY KEY,
    nombre           VARCHAR(80)  NOT NULL,
    apellido         VARCHAR(80)  NOT NULL,
    usuario          VARCHAR(50)  NOT NULL,
    email            VARCHAR(120) NOT NULL,
    contrasena_hash  VARCHAR(60)  NOT NULL,   -- hash de BCrypt (60 caracteres)
    id_rol           INT          NOT NULL,
    fecha_registro   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_usuarios_email   UNIQUE (email),
    CONSTRAINT uq_usuarios_usuario UNIQUE (usuario),

    CONSTRAINT fk_usuarios_rol
        FOREIGN KEY (id_rol) REFERENCES roles (id_rol)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

-- Índice extra para acelerar la búsqueda por correo (login)
CREATE INDEX idx_usuarios_email ON usuarios (email);

-- ==========================================================
-- 3. Roles iniciales
--    "Empleado" es el rol que se asigna por defecto a quien se
--    registra desde la pantalla de Crear Cuenta.
-- ==========================================================
INSERT INTO roles (nombre_rol) VALUES
    ('Administrador'),
    ('Empleado');

-- ==========================================================
-- 4. Un registro de usuario de ejemplo, para poder iniciar
--    sesión sin tener que registrarte primero desde la app.
--
--    Correo:     juan.ignacio@vitaligold.org
--    Usuario:    juan.ignacio
--    Contraseña: 123456   (ya va cifrada abajo con BCrypt)
--    Rol:        Administrador
-- ==========================================================
INSERT INTO usuarios (nombre, apellido, usuario, email, contrasena_hash, id_rol)
VALUES (
    'Juan Ignacio',
    'Pérez',
    'juan.ignacio',
    'juan.ignacio@vitaligold.org',
    '$2a$10$pPotaiZb4BlcEMi6TMsRv.MlzEaLyjMb9sL9shSGMN2wkualaUAOq',
    (SELECT id_rol FROM roles WHERE nombre_rol = 'Administrador')
);

-- ==========================================================
-- Escenario 8. Salud Pública: Centro de Atención Médica
-- 5. Tabla de pacientes
--    Módulo de Admisión de Pacientes del centro de salud.
--    dpi es la llave primaria natural: es el Código de
--    Seguridad Social/DPI de cada ciudadano y ya es único
--    por definición, así que no se usa un id autoincremental.
-- ==========================================================
CREATE TABLE pacientes (
    dpi                VARCHAR(13)  NOT NULL,
    nombres             VARCHAR(50)  NOT NULL,
    apellidos           VARCHAR(50)  NOT NULL,
    fecha_nacimiento    DATE         NOT NULL,
    genero              VARCHAR(15)  NOT NULL,
    tipo_sangre         VARCHAR(5)   NOT NULL,
    fecha_registro      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_pacientes_dpi PRIMARY KEY (dpi),
    CONSTRAINT ck_pacientes_dpi_formato CHECK (CHAR_LENGTH(dpi) BETWEEN 8 AND 13),
    CONSTRAINT ck_pacientes_genero CHECK (genero IN ('Masculino', 'Femenino', 'Otro')),
    CONSTRAINT ck_pacientes_tipo_sangre CHECK (tipo_sangre IN ('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'))
);

-- Índice extra para acelerar la búsqueda por apellidos en recepción
CREATE INDEX idx_pacientes_apellidos ON pacientes (apellidos);

-- ==========================================================
-- 6. Pacientes de prueba, para poder ver el TableView con
--    datos apenas se abre el módulo de Admisión de Pacientes.
-- ==========================================================
INSERT INTO pacientes (dpi, nombres, apellidos, fecha_nacimiento, genero, tipo_sangre) VALUES
    ('1234567890101', 'María José',      'Gómez López',     '1990-04-12', 'Femenino',  'O+'),
    ('2345678901012', 'Carlos Andrés',   'Ramírez Díaz',    '1985-11-02', 'Masculino', 'A+'),
    ('3456789010123', 'Ana Lucía',       'Santos Morales',  '2001-07-25', 'Femenino',  'B-'),
    ('4567890101234', 'Pedro',           'Xocop Ixchop',    '1978-01-30', 'Masculino', 'AB+'),
    ('5678901012345', 'Valentina',       'Chávez Rosales',  '2015-09-09', 'Otro',      'O-');

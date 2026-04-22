-- ============================================
-- BASE DE DATOS SOLINX
-- ============================================
-- Plataforma de vinculación entre estudiantes,
-- empresas y supervisores para prácticas profesionales
-- y proyectos académicos.
--
-- Incluye:
--   * Esquema completo de tablas
--   * Datos seed de prueba (usuarios, empresas, proyectos, solicitudes)
--
-- Para ejecutar:
--   1. Asegúrate de tener MySQL corriendo (puerto 3306 por defecto)
--   2. Conéctate como root o un usuario con permisos CREATE DATABASE
--   3. Corre este script completo: source solinxDB.sql
-- ============================================

DROP DATABASE IF EXISTS solinx;
CREATE DATABASE IF NOT EXISTS solinx;
USE solinx;

-- ============================================
-- 1. TABLAS PRINCIPALES
-- ============================================

CREATE TABLE Estudiante (
    boleta INT NOT NULL PRIMARY KEY,
    carrera VARCHAR(100) NOT NULL DEFAULT '',
    escuela VARCHAR(100) NOT NULL DEFAULT ''
);

CREATE TABLE Empresa (
    idEmpresa INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombreEmpresa VARCHAR(100) NOT NULL DEFAULT '',
    telefono VARCHAR(20) DEFAULT NULL
);

CREATE TABLE Supervisor (
    idSupervisor INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    area VARCHAR(100) NOT NULL DEFAULT '',
    idEmpresa INT NOT NULL,
    FOREIGN KEY (idEmpresa) REFERENCES Empresa(idEmpresa) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE Usuario (
    idUsuario INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(150) UNIQUE NOT NULL,
    telefono VARCHAR(20) DEFAULT NULL,
    userPassword VARCHAR(255) NOT NULL,
    rol ENUM('estudiante', 'empresa', 'supervisor', 'administrador') NOT NULL,
    verificado BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE TokenVerificacion (
    idToken INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(36) NOT NULL UNIQUE,
    fechaExpira TIMESTAMP NOT NULL,
    usado BOOLEAN NOT NULL DEFAULT FALSE,
    idUsuario INT NOT NULL,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE Horario (
    idHorario INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    lunInicio TIME NULL,
    lunFinal TIME NULL,
    marInicio TIME NULL,
    marFinal TIME NULL,
    mierInicio TIME NULL,
    mierFinal TIME NULL,
    jueInicio TIME NULL,
    jueFinal TIME NULL,
    vieInicio TIME NULL,
    vieFinal TIME NULL,
    sabInicio TIME NULL,
    sabFinal TIME NULL,
    domInicio TIME NULL,
    domFinal TIME NULL
);

-- ============================================
-- 2. TABLAS DE RELACIÓN (Usuario - Rol)
-- ============================================

CREATE TABLE UsuarioEstudiante (
    idUsuario INT NOT NULL,
    boleta INT NOT NULL,
    PRIMARY KEY (idUsuario),
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (boleta) REFERENCES Estudiante(boleta) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE UsuarioEmpresa (
    idUsuario INT NOT NULL,
    idEmpresa INT NOT NULL,
    PRIMARY KEY (idUsuario),
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (idEmpresa) REFERENCES Empresa(idEmpresa) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE UsuarioSupervisor (
    idUsuario INT NOT NULL,
    idSupervisor INT NOT NULL,
    PRIMARY KEY (idUsuario),
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (idSupervisor) REFERENCES Supervisor(idSupervisor) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE HorarioEstudiante (
    boleta INT NOT NULL,
    idHorario INT NOT NULL,
    PRIMARY KEY (boleta),
    FOREIGN KEY (boleta) REFERENCES Estudiante(boleta) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (idHorario) REFERENCES Horario(idHorario) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE Notificacion (
    idNotificacion INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    mensaje TEXT NOT NULL,
    fechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    leida BOOLEAN DEFAULT FALSE,
    idUsuario INT NOT NULL,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario) ON UPDATE CASCADE ON DELETE CASCADE
);

-- ============================================
-- 3. TABLAS DE NEGOCIO (Proyectos, Solicitudes, Perfiles)
-- ============================================

CREATE TABLE Proyecto (
    idProyecto INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    carreraEnfocada VARCHAR(150) NOT NULL DEFAULT '',
    objetivo TEXT NOT NULL,
    fechaInicio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    vacantes INT NOT NULL DEFAULT 1,
    ubicacion VARCHAR(100) NOT NULL DEFAULT '',
    nombreProyecto VARCHAR(255) NOT NULL DEFAULT '',
    fechaTermino TIMESTAMP NULL DEFAULT NULL,
    imagenRef VARCHAR(100) DEFAULT 'img_default_proyecto',
    imagenProyecto LONGTEXT DEFAULT NULL,
    idEmpresa INT NOT NULL,
    FOREIGN KEY (idEmpresa) REFERENCES Empresa(idEmpresa) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE Solicitud (
    idSolicitud INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    fechaSolicitud TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fechaAceptacion DATETIME NULL DEFAULT NULL,
    estadoSolicitud ENUM(
        'enviada',
        'aprobada_supervisor',
        'rechazada_supervisor',
        'aceptada',
        'rechazada_empresa',
        'aprobada',
        'rechazada'
    ) NOT NULL DEFAULT 'enviada',
    boleta INT NOT NULL,
    idProyecto INT NOT NULL,
    FOREIGN KEY (boleta) REFERENCES Estudiante(boleta) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (idProyecto) REFERENCES Proyecto(idProyecto) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE Perfil (
    idPerfil INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    foto LONGTEXT DEFAULT NULL,
    tema ENUM('claro', 'oscuro') NOT NULL DEFAULT 'claro',
    idUsuario INT NOT NULL,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE Documento (
    idDocumento INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    periodo INT NOT NULL CHECK (periodo BETWEEN 1 AND 8),
    archivo LONGBLOB NOT NULL,
    nombreArchivo VARCHAR(255) NOT NULL,
    fechaSubida TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    boleta INT NOT NULL,
    FOREIGN KEY (boleta) REFERENCES Estudiante(boleta) ON UPDATE CASCADE ON DELETE CASCADE,
    UNIQUE KEY unique_periodo_boleta (boleta, periodo)
);

-- ============================================
-- DATOS SEED (DATOS DE PRUEBA)
-- ============================================

-- ─── 1. ESTUDIANTES ────────────────────────────────────
INSERT INTO Estudiante (boleta, carrera, escuela) VALUES
(2023000001, 'Ingeniería en Software',   'ESCOM'),
(2023000002, 'Ingeniería Industrial',    'UPIICSA'),
(2023000003, 'Ingeniería Mecatrónica',   'UPIITA'),
(2023000004, 'Ingeniería Informática',   'UPIICSA'),
(2023000005, 'Ingeniería Aeronáutica',   'ESIA Ticomán');

-- ─── 2. EMPRESAS (Con teléfonos de tu versión) ─────────
INSERT INTO Empresa (nombreEmpresa, telefono) VALUES
('TechNova', '5588991122'),
('AeroDynamics MX', '5544556677'),
('SoftSolutions', '5533445566'),
('ElectroCorp', '5522334455'),
('Aa', '5511111111'),
('Ooo', '5522222222');

-- ─── 3. SUPERVISORES ───────────────────────────────────
INSERT INTO Supervisor (area, idEmpresa) VALUES
('Recursos Humanos', 1);

-- ─── 4. USUARIOS (Con verificación TRUE por defecto) ───
INSERT INTO Usuario (nombre, correo, telefono, userPassword, rol, verificado) VALUES
('Mauro López', 'mauro@correo.com', '5512345678', 'pass123', 'estudiante', TRUE),
('Laura Tech', 'laura@technova.com', '5588991122', 'empresa123', 'empresa', TRUE),
('Carlos Supervisor', 'carlos@technova.com', '5599001122', 'sup123', 'supervisor', TRUE),
('Sofía Ramírez', 'sofia@correo.com', '5544332211', 'pass123', 'estudiante', TRUE),
('Empresa Aa', 'aa@gmail.com', '5511111111', '111', 'empresa', TRUE),
('Empresa Ooo', 'oo@gmail.com', '5522222222', '222', 'empresa', TRUE),
('Luis Herrera', 'luis@correo.com', '5533334444', 'pass123', 'estudiante', TRUE),
('Ana Martínez', 'ana@correo.com', '5555556666', 'pass123', 'estudiante', TRUE),
('Pedro Jiménez', 'pedro@correo.com', '5577778888', 'pass123', 'estudiante', TRUE);

-- ─── 5. VINCULACIÓN USUARIOS ↔ ROLES ───────────────────
INSERT INTO UsuarioEstudiante (idUsuario, boleta) VALUES
(1, 2023000001),
(4, 2023000005),
(7, 2023000002),
(8, 2023000003),
(9, 2023000004);

INSERT INTO UsuarioEmpresa (idUsuario, idEmpresa) VALUES
(2, 1),
(5, 2),
(6, 3);

INSERT INTO UsuarioSupervisor (idUsuario, idSupervisor) VALUES
(3, 1);

-- ─── 6. PROYECTOS ──────────────────────────────────────
INSERT INTO Proyecto (carreraEnfocada, nombreProyecto, objetivo, vacantes, ubicacion, imagenRef, idEmpresa) VALUES
('Ingeniería en Software',  'Sistema de Gestión Escolar v2.0', 'Crear un sistema web.',          3, 'CDMX',      'img_gestion',          1),
('Ingeniería Aeronáutica',  'Proyecto Icarus',                 'Desarrollo de un dron.',         2, 'Querétaro', 'img_dron',             1),
('Logística Industrial',    'App Inventarios FastTrack',       'App móvil para inventarios.',    4, 'CDMX',      'img_inventario',       2),
('Ingeniería Eléctrica',    'EcoMonitor Inteligente',          'Monitoreo de consumo.',          2, 'Edomex',    'img_energia',          3),
('Desarrollo Backend',      'Sistema de Pagos Aa',             'Crear API segura.',              2, 'Remoto',    'img_default_proyecto', 2),
('Inteligencia Artificial', 'Chatbot Ooo V2',                  'Asistente virtual.',             3, 'CDMX',      'img_default_proyecto', 3);

-- ─── 7. PERFILES ───────────────────────────────────────
INSERT INTO Perfil (tema, idUsuario) VALUES
('claro',  1),
('claro',  2),
('claro',  3),
('claro',  4),
('claro',  5),
('claro',  6),
('claro',  7),
('claro',  8),
('claro',  9);

-- ─── 8. HORARIOS ───────────────────────────────────────
INSERT INTO Horario (lunInicio, lunFinal, marInicio, marFinal, mierInicio, mierFinal, jueInicio, jueFinal, vieInicio, vieFinal) VALUES
('08:00:00', '14:00:00', '08:00:00', '14:00:00', '08:00:00', '14:00:00', '08:00:00', '14:00:00', '08:00:00', '14:00:00');

INSERT INTO Horario (lunInicio, lunFinal, marInicio, marFinal, mierInicio, mierFinal, jueInicio, jueFinal, vieInicio, vieFinal) VALUES
('14:00:00', '20:00:00', '14:00:00', '20:00:00', '14:00:00', '20:00:00', '14:00:00', '20:00:00', '14:00:00', '20:00:00');

INSERT INTO Horario (lunInicio, lunFinal, marInicio, marFinal, mierInicio, mierFinal, jueInicio, jueFinal, vieInicio, vieFinal, sabInicio, sabFinal) VALUES
('09:00:00', '13:00:00', '09:00:00', '13:00:00', '09:00:00', '13:00:00', '09:00:00', '13:00:00', '09:00:00', '13:00:00', '09:00:00', '13:00:00');

-- ─── 9. HORARIOS DE ESTUDIANTES ────────────────────────
INSERT INTO HorarioEstudiante (boleta, idHorario) VALUES
(2023000001, 1),
(2023000002, 2),
(2023000003, 3),
(2023000004, 1),
(2023000005, 2);

-- ─── 10. SOLICITUDES ───────────────────────────────────
INSERT INTO Solicitud (estadoSolicitud, boleta, idProyecto) VALUES
('enviada', 2023000001, 1),
('enviada', 2023000002, 3),
('enviada', 2023000003, 5),
('enviada', 2023000004, 6),
('enviada', 2023000005, 2);

-- ─── 11. NOTIFICACIONES ────────────────────────────────
INSERT INTO Notificacion (titulo, mensaje, leida, idUsuario) VALUES
('Recordatorio: Documentos pendientes',
 'Han pasado más de 5 días sin que subas tu documentación del periodo. Por favor ingresa a la plataforma y sube tus documentos.',
 FALSE, 1);

-- ============================================
-- VERIFICACIÓN
-- ============================================
SELECT * FROM Empresa;
SELECT * FROM Proyecto;
SELECT * FROM Perfil;
SELECT * FROM Usuario;
SELECT * FROM Solicitud;

UPDATE Usuario SET verificado = 0 WHERE idUsuario = 11;

/* ===========================================================
TABLA DE REFERENCIA RÁPIDA - USUARIOS DE PRUEBA (SoLinX)
===========================================================

 [ ESTUDIANTES ]
 ---------------------------------------------------
 Correo: mauro@correo.com     | Password: pass123
 Correo: sofia@correo.com     | Password: pass123
 Correo: luis@correo.com      | Password: pass123
 Correo: ana@correo.com       | Password: pass123
 Correo: pedro@correo.com     | Password: pass123

 [ EMPRESAS ]
 ---------------------------------------------------
 Correo: laura@technova.com   | Password: empresa123
 Correo: aa@gmail.com         | Password: 111
 Correo: oo@gmail.com         | Password: 222

 [ SUPERVISORES / ADMIN ]
 ---------------------------------------------------
 Correo: carlos@technova.com  | Password: sup123
===========================================================
*/
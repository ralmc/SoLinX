-- ============================================
-- BASE DE DATOS SOLINX
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
-- 3. TABLAS DE NEGOCIO
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
    estadoProyecto ENUM('pendiente', 'aprobado', 'rechazado') NOT NULL DEFAULT 'pendiente',
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
    estadoDocumento ENUM('pendiente', 'aprobado', 'rechazado') NOT NULL DEFAULT 'pendiente',
    boleta INT NOT NULL,
    FOREIGN KEY (boleta) REFERENCES Estudiante(boleta) ON UPDATE CASCADE ON DELETE CASCADE,
    UNIQUE KEY unique_periodo_boleta (boleta, periodo)
);

-- ============================================
-- DATOS SEED
-- ============================================
-- ─── 1. ESTUDIANTES ────────────────────────────────────
INSERT INTO Estudiante (boleta, carrera, escuela) VALUES
(2023000001, 'Ingeniería en Software',               'ESCOM'),
(2023000002, 'Ingeniería Industrial',                'UPIICSA'),
(2023000003, 'Ingeniería Mecatrónica',               'UPIITA'),
(2023000004, 'Ingeniería Informática',               'UPIICSA'),
(2023000005, 'Ingeniería Aeronáutica',               'ESIA Ticomán'),
(2023000006, 'Ingeniería en Inteligencia Artificial','ESCOM'),
(2023000007, 'Ingeniería Biónica',                   'UPIBI');

-- ─── 2. EMPRESAS ───────────────────────────────────────
INSERT INTO Empresa (nombreEmpresa, telefono) VALUES
('TechNova Solutions', '5512348765'),
('AeroDynamics MX',    '5598761234'),
('SoftSolutions Corp', '5534129876'),
('ElectroCorp SA',     '5567893412');

-- ─── 3. SUPERVISORES ───────────────────────────────────
INSERT INTO Supervisor (area, idEmpresa) VALUES
('Recursos Humanos', 1); 

-- ─── 4. USUARIOS ───────────────────────────────────────
INSERT INTO Usuario (nombre, correo, telefono, userPassword, rol, verificado) VALUES
-- Estudiantes (idUsuario 1-7)
('Mauro López',       'mauro@correo.com',    '5512345678', 'pass123',    'estudiante', TRUE),
('Sofía Ramírez',     'sofia@correo.com',    '5544332211', 'pass123',    'estudiante', TRUE),
('Luis Herrera',      'luis@correo.com',     '5533334444', 'pass123',    'estudiante', TRUE),
('Ana Martínez',      'ana@correo.com',      '5555556666', 'pass123',    'estudiante', TRUE),
('Pedro Jiménez',     'pedro@correo.com',    '5577778888', 'pass123',    'estudiante', TRUE),
('Carlos Mendoza',    'carlos@correo.com',   '5511223344', 'pass123',    'estudiante', TRUE),
('Diana Torres',      'diana@correo.com',    '5599887766', 'pass123',    'estudiante', TRUE),
-- Empresas (idUsuario 8-11)
('Laura Tech',        'laura@technova.com',  '5588991122', 'empresa123', 'empresa',    TRUE),
('Empresa Aero',      'aero@aero.com',       '5598761234', 'empresa123', 'empresa',    TRUE),
('Empresa Soft',      'soft@soft.com',       '5534129876', 'empresa123', 'empresa',    TRUE),
('Empresa Electro',   'electro@electro.com', '5567893412', 'empresa123', 'empresa',    TRUE),
-- Supervisor (idUsuario 12)
('Carlos Supervisor', 'carlos@technova.com', '5599001122', 'sup123',     'supervisor', TRUE);

-- ─── 5. VINCULACIÓN USUARIOS ↔ ROLES ───────────────────
INSERT INTO UsuarioEstudiante (idUsuario, boleta) VALUES
(1,  2023000001),
(2,  2023000005),
(3,  2023000002),
(4,  2023000003),
(5,  2023000004),
(6,  2023000006),
(7,  2023000007);

INSERT INTO UsuarioEmpresa (idUsuario, idEmpresa) VALUES
(8,  1),
(9,  2),
(10, 3),
(11, 4);

INSERT INTO UsuarioSupervisor (idUsuario, idSupervisor) VALUES
(12, 1);

-- ─── 6. PROYECTOS ──────────────────────────────────────
INSERT INTO Proyecto (carreraEnfocada, nombreProyecto, objetivo, vacantes, ubicacion, fechaInicio, fechaTermino, imagenRef, estadoProyecto, idEmpresa) VALUES
-- TechNova (idProyecto 1-2)
('Ingeniería en Software',
 'Sistema de Gestión Escolar v2.0',
 'Crear un sistema web moderno para la gestión de datos escolares.',
 3, 'Av. IPN 2508, Zacatenco, CDMX',
 '2026-04-23 09:00:00', '2026-07-31 18:00:00', 'img_gestion', 'aprobado', 1),
('Ingeniería Aeronáutica',
 'Proyecto Icarus',
 'Desarrollo e implementación de un dron de uso académico.',
 2, 'Blvd. Miguel de Cervantes 120, Querétaro',
 '2026-04-23 09:00:00', '2026-07-31 18:00:00', 'img_dron', 'aprobado', 1),
-- AeroDynamics (idProyecto 3-4)
('Logística Industrial',
 'App Inventarios FastTrack',
 'App móvil para gestión de inventarios en tiempo real.',
 4, 'Calz. Vallejo 1003, Gustavo A. Madero, CDMX',
 '2026-04-23 09:00:00', '2026-07-31 18:00:00', 'img_inventario', 'aprobado', 2),
('Ingeniería Mecatrónica',
 'Robot Ensamblador v3',
 'Diseño y programación de brazo robótico para línea de ensamble.',
 2, 'Periférico Sur 4349, Jardines del Pedregal, CDMX',
 '2026-04-23 09:00:00', '2026-07-31 18:00:00', 'img_default_proyecto', 'aprobado', 2),
-- SoftSolutions (idProyecto 5-6)
('Ingeniería Eléctrica',
 'EcoMonitor Inteligente',
 'Sistema de monitoreo de consumo eléctrico con alertas automáticas.',
 2, 'Av. Insurgentes Sur 800, Del Valle, Edomex',
 '2026-04-23 09:00:00', '2026-07-31 18:00:00', 'img_energia', 'aprobado', 3),
('Ingeniería en Inteligencia Artificial',
 'Chatbot SoftAssist V2',
 'Asistente virtual con procesamiento de lenguaje natural para soporte técnico.',
 3, 'Calle Tokio 35, Juárez, CDMX',
 '2026-04-23 09:00:00', '2026-07-31 18:00:00', 'img_default_proyecto', 'aprobado', 3),
-- ElectroCorp (idProyecto 7-8)
('Desarrollo Backend',
 'API de Pagos Seguros',
 'Crear una API segura para procesamiento de pagos en línea.',
 2, 'Lago Zurich 245, Ampliación Granada, CDMX',
 '2026-04-23 09:00:00', '2026-07-31 18:00:00', 'img_default_proyecto', 'aprobado', 4),
('Ingeniería Informática',
 'Dashboard Analytics Pro',
 'Panel de análisis de datos en tiempo real para toma de decisiones empresariales.',
 3, 'Av. Santa Fe 505, Cuajimalpa, CDMX',
 '2026-04-23 09:00:00', '2026-07-31 18:00:00', 'img_default_proyecto', 'aprobado', 4);

-- ─── 7. PERFILES ───────────────────────────────────────
INSERT INTO Perfil (tema, idUsuario) VALUES
('claro',  1), ('claro',  2), ('claro',  3), ('claro',  4),
('claro',  5), ('claro',  6), ('claro',  7), ('claro',  8),
('claro',  9), ('claro', 10), ('claro', 11), ('claro', 12);

-- ─── 8. HORARIOS ───────────────────────────────────────
INSERT INTO Horario (lunInicio, lunFinal, marInicio, marFinal, mierInicio, mierFinal, jueInicio, jueFinal, vieInicio, vieFinal) VALUES
('08:00:00', '14:00:00', '08:00:00', '14:00:00', '08:00:00', '14:00:00', '08:00:00', '14:00:00', '08:00:00', '14:00:00'),
('14:00:00', '20:00:00', '14:00:00', '20:00:00', '14:00:00', '20:00:00', '14:00:00', '20:00:00', '14:00:00', '20:00:00');

INSERT INTO Horario (lunInicio, lunFinal, marInicio, marFinal, mierInicio, mierFinal, jueInicio, jueFinal, vieInicio, vieFinal, sabInicio, sabFinal) VALUES
('09:00:00', '13:00:00', '09:00:00', '13:00:00', '09:00:00', '13:00:00', '09:00:00', '13:00:00', '09:00:00', '13:00:00', '09:00:00', '13:00:00');

-- ─── 9. HORARIOS DE ESTUDIANTES ────────────────────────
INSERT INTO HorarioEstudiante (boleta, idHorario) VALUES
(2023000001, 1), (2023000002, 2), (2023000003, 3),
(2023000004, 1), (2023000005, 2), (2023000006, 1), (2023000007, 3);

-- ─── 10. SOLICITUDES ───────────────────────────────────
-- Mauro (idUsuario 1, boleta 2023000001) → aceptado en proyecto 1
INSERT INTO Solicitud (estadoSolicitud, fechaAceptacion, boleta, idProyecto) VALUES
('aceptada', '2026-04-23 10:00:00', 2023000001, 1);

-- Sofía (idUsuario 2, boleta 2023000005) → enviada al supervisor (pendiente de revisión)
INSERT INTO Solicitud (estadoSolicitud, boleta, idProyecto) VALUES
('enviada', 2023000005, 2);

-- Luis (idUsuario 3, boleta 2023000002) → rechazada por empresa
INSERT INTO Solicitud (estadoSolicitud, boleta, idProyecto) VALUES
('rechazada_empresa', 2023000002, 3);

-- Ana (idUsuario 4, boleta 2023000003) → aprobada por supervisor, esperando empresa
INSERT INTO Solicitud (estadoSolicitud, boleta, idProyecto) VALUES
('aprobada_supervisor', 2023000003, 1);

-- Pedro (idUsuario 5, boleta 2023000004) → rechazada por supervisor
INSERT INTO Solicitud (estadoSolicitud, boleta, idProyecto) VALUES
('rechazada_supervisor', 2023000004, 2);

-- ─── 11. NOTIFICACIONES ────────────────────────────────
INSERT INTO Notificacion (titulo, mensaje, leida, idUsuario) VALUES
-- Mauro: aceptado por empresa
('¡Felicidades! Fuiste aceptado en un proyecto 🎉',
 'La empresa Laura Tech aceptó tu solicitud en el proyecto Sistema de Gestión Escolar v2.0. ¡Ya puedes subir tus documentos!',
 FALSE, 1),

-- Sofía: solicitud en revisión por supervisor
('Solicitud en proceso',
 'Tu solicitud para el proyecto "Proyecto Icarus" está siendo revisada por el supervisor. Te notificaremos cuando haya una actualización.',
 FALSE, 2),

-- Luis: rechazada por empresa
('Solicitud no aceptada por la empresa',
 'La empresa Empresa Aero no aceptó tu solicitud en este momento. Puedes postularte a otro proyecto.',
 FALSE, 3),

-- Ana: aprobada por supervisor, esperando empresa
('Solicitud aprobada por el Supervisor ✓',
 'Tu solicitud fue aprobada por el supervisor y enviada a la empresa. ¡Espera su respuesta!',
 FALSE, 4),

-- Pedro: rechazada por supervisor
('Solicitud rechazada por el Supervisor',
 'Tu solicitud fue rechazada por el supervisor. Puedes postularte a otro proyecto.',
 FALSE, 5);

-- ============================================
-- VERIFICACIÓN
-- ============================================
SELECT * FROM Empresa;
SELECT * FROM Proyecto;
SELECT * FROM Perfil;
SELECT * FROM Usuario;
SELECT * FROM Solicitud;
SELECT * FROM Documento;
SELECT * FROM Notificacion;

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
 Correo: carlos@correo.com    | Password: pass123
 Correo: diana@correo.com     | Password: pass123
 [ EMPRESAS ]
 ---------------------------------------------------
 Correo: laura@technova.com   | Password: empresa123
 Correo: aero@aero.com        | Password: empresa123
 Correo: soft@soft.com        | Password: empresa123
 Correo: electro@electro.com  | Password: empresa123
 [ SUPERVISORES ]
 ---------------------------------------------------
 Correo: carlos@technova.com  | Password: sup123
=========================================================== */
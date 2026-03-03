-- ============================================
-- BASE DE DATOS SOLINX
-- ============================================
DROP DATABASE IF EXISTS solinx;
CREATE DATABASE IF NOT EXISTS solinx;
USE solinx;

-- 1. TABLAS PRINCIPALES
CREATE TABLE Estudiante (
    boleta INT NOT NULL PRIMARY KEY,
    carrera VARCHAR(100) NOT NULL DEFAULT '',
    escuela VARCHAR(100) NOT NULL DEFAULT ''
);

CREATE TABLE Empresa (
    idEmpresa INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombreEmpresa VARCHAR(100) NOT NULL DEFAULT ''
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
    rol ENUM('estudiante', 'empresa', 'supervisor', 'administrador') NOT NULL
);

CREATE TABLE Horario(
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

-- 2. TABLAS DE RELACIÓN (Usuario - Rol) y horario
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

-- 3. TABLAS DE NEGOCIO (Proyectos, Solicitudes)
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
    idEmpresa INT NOT NULL,
    FOREIGN KEY (idEmpresa) REFERENCES Empresa(idEmpresa) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE Solicitud (
    idSolicitud INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    fechaSolicitud TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estadoSolicitud ENUM('enviada', 'aceptada', 'rechazada') NOT NULL DEFAULT 'enviada',
    boleta INT NOT NULL,
    idProyecto INT NOT NULL,
    FOREIGN KEY (boleta) REFERENCES Estudiante(boleta) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (idProyecto) REFERENCES Proyecto(idProyecto) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE Perfil (
    idPerfil INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    foto BLOB,
    tema ENUM('claro', 'oscuro') NOT NULL DEFAULT 'claro',
    idUsuario INT NOT NULL,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario) ON UPDATE CASCADE ON DELETE CASCADE
);

-- ============================================
-- INSERCIÓN DE DATOS
-- ============================================

-- 1. ESTUDIANTES
INSERT INTO Estudiante (boleta, carrera, escuela) VALUES
(20230001, 'Ingeniería en Software', 'ESCOM'),
(20230002, 'Ingeniería Industrial', 'UPIICSA'),
(20230003, 'Ingeniería Mecatrónica', 'UPIITA'),
(20230004, 'Ingeniería Informática', 'UPIICSA'),
(20230005, 'Ingeniería Aeronáutica', 'ESIA Ticomán');

-- 2. EMPRESAS
INSERT INTO Empresa (nombreEmpresa) VALUES
('TechNova'),        -- ID 1
('AeroDynamics MX'), -- ID 2
('SoftSolutions'),   -- ID 3
('ElectroCorp'),     -- ID 4
('Aa'),              -- ID 5
('Ooo');             -- ID 6

-- 3. SUPERVISORES
INSERT INTO Supervisor (area, idEmpresa) VALUES
('Recursos Humanos', 1),
('Desarrollo de Software', 1),
('Proyectos Aeronáuticos', 2),
('Sistemas', 3);

-- 4. USUARIOS (Sin administrador)
INSERT INTO Usuario (nombre, correo, telefono, userPassword, rol) VALUES
('Mauro López', 'mauro@correo.com', '5512345678', 'pass123', 'estudiante'), -- ID 1
('Laura Tech', 'laura@technova.com', '5588991122', 'empresa123', 'empresa'), -- ID 2 (TechNova)
('Carlos Supervisor', 'carlos@technova.com', '5599001122', 'sup123', 'supervisor'), -- ID 3
('Sofía Ramírez', 'sofia@correo.com', '5544332211', 'pas123', 'estudiante'), -- ID 4
('Empresa Aa', 'aa@gmail.com', '5511111111', '111', 'empresa'), -- ID 5 (Para empresa Aa)
('Empresa Ooo', 'oo@gmail.com', '5522222222', '222', 'empresa'); -- ID 6 (Para empresa Ooo)

-- 5. VINCULAR USUARIOS CON ROLES
-- Estudiantes
INSERT INTO UsuarioEstudiante (idUsuario, boleta) VALUES
(1, 20230001),
(4, 20230005);

-- Empresas
INSERT INTO UsuarioEmpresa (idUsuario, idEmpresa) VALUES
(2, 1), -- Laura -> TechNova
(5, 5), -- aa@gmail.com -> Empresa Aa (ID 5)
(6, 6); -- oo@gmail.com -> Empresa Ooo (ID 6)

-- Supervisores
INSERT INTO UsuarioSupervisor (idUsuario, idSupervisor) VALUES
(3, 1);

-- 6. PROYECTOS (Datos iniciales)
INSERT INTO Proyecto (carreraEnfocada, nombreProyecto, objetivo, vacantes, ubicacion, imagenRef, idEmpresa) VALUES
('Ingeniería en Software', 'Sistema de Gestión Escolar v2.0', 'Crear un sistema web.', 3, 'CDMX', 'img_gestion', 1),
('Ingeniería Aeronáutica', 'Proyecto Icarus', 'Desarrollo de un dron.', 2, 'Querétaro', 'img_dron', 2),
('Logística Industrial', 'App Inventarios FastTrack', 'App móvil para inventarios.', 4, 'CDMX', 'img_inventario', 3),
('Ingeniería Eléctrica', 'EcoMonitor Inteligente', 'Monitoreo de consumo.', 2, 'Edomex', 'img_energia', 4);

-- 7. PERFILES
INSERT INTO Perfil (tema, idUsuario) VALUES
('claro', 1), ('oscuro', 2), ('claro', 3), ('claro', 4), ('claro', 5), ('claro', 6);

-- 8. HORARIOS
-- Horario 1: Mañana (08:00 - 12:00)
INSERT INTO Horario ( 
	lunInicio, lunFinal,
	marInicio, marFinal,
	mierInicio, mierFinal,
	jueInicio, jueFinal,
	vieInicio, vieFinal
) VALUES (
  '08:00:00','12:00:00',
  '08:00:00','12:00:00',
  '08:00:00','12:00:00',
  '08:00:00','12:00:00',
  '08:00:00','12:00:00'
);

-- Horario 2: Tarde (14:00 - 18:00)
INSERT INTO Horario (
	lunInicio, lunFinal,
	marInicio, marFinal,
	mierInicio, mierFinal,
	jueInicio, jueFinal,
	vieInicio, vieFinal
) VALUES (
  '14:00:00','18:00:00',
  '14:00:00','18:00:00',
  '14:00:00','18:00:00',
  '14:00:00','18:00:00',
  '14:00:00','18:00:00'
);

-- 9. HORARIOS-ALUMNOS
-- Estudiante 1 → Horario Mañana
INSERT INTO HorarioEstudiante (boleta, idHorario)
VALUES (20230001, 1);

-- Estudiante 2 → Horario Tarde
INSERT INTO HorarioEstudiante (boleta, idHorario)
VALUES (20230005, 2);

-- ============================================
-- DATOS DE PRUEBA: PROYECTOS Y SOLICITUDES PARA Aa Y Ooo
-- ============================================

-- Estudiantes Extra para pruebas
INSERT IGNORE INTO Estudiante (boleta, carrera, escuela) VALUES 
(20250001, 'Ingeniería en Sistemas', 'ESCOM'),
(20250002, 'Ingeniería Mecatrónica', 'UPIITA'),
(20250003, 'Licenciatura en Datos', 'ESFM');

-- Proyecto para Empresa 5 (Aa)
INSERT INTO Proyecto (carreraEnfocada, nombreProyecto, objetivo, vacantes, ubicacion, idEmpresa, imagenRef) 
VALUES ('Desarrollo Backend', 'Sistema de Pagos Aa', 'Crear API segura', 2, 'Remoto', 5, 'img_default_proyecto');

-- Proyecto para Empresa 6 (Ooo)
INSERT INTO Proyecto (carreraEnfocada, nombreProyecto, objetivo, vacantes, ubicacion, idEmpresa, imagenRef) 
VALUES ('Inteligencia Artificial', 'Chatbot Ooo V2', 'Asistente virtual', 3, 'CDMX', 6, 'img_default_proyecto');

-- Solicitud para Aa (Empresa 5)
INSERT INTO Solicitud (fechaSolicitud, estadoSolicitud, boleta, idProyecto)
VALUES (NOW(), 'enviada', 20250001, (SELECT idProyecto FROM Proyecto WHERE idEmpresa = 5 LIMIT 1));

-- Solicitudes para Ooo (Empresa 6) - Dos solicitudes
INSERT INTO Solicitud (fechaSolicitud, estadoSolicitud, boleta, idProyecto)
VALUES (NOW(), 'enviada', 20250002, (SELECT idProyecto FROM Proyecto WHERE idEmpresa = 6 LIMIT 1));

INSERT INTO Solicitud (fechaSolicitud, estadoSolicitud, boleta, idProyecto)
VALUES (NOW(), 'enviada', 20250003, (SELECT idProyecto FROM Proyecto WHERE idEmpresa = 6 LIMIT 1));

SELECT * FROM Usuario WHERE correo IN ('aa@gmail.com', 'oo@gmail.com');
SELECT * FROM Solicitud;
SELECT * FROM Proyecto;
SELECT * FROM Estudiante;
SELECT * FROM HorarioEstudiante;
SELECT * FROM Horario;
SELECT * FROM usuario;
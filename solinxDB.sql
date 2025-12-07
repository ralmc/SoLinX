-- SoLinX
DROP DATABASE IF EXISTS solinx;
CREATE DATABASE IF NOT EXISTS solinx;
USE solinx;

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

CREATE TABLE Proyecto (
    idProyecto INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombreProyecto VARCHAR(150) NOT NULL DEFAULT '',
    objetivo TEXT NOT NULL,
    fechaInicio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    vacantes INT NOT NULL DEFAULT 1,
    ubicacion VARCHAR(100) NOT NULL DEFAULT '',
    justificacion VARCHAR(255) NOT NULL DEFAULT '',
    fechaTermino TIMESTAMP NULL DEFAULT NULL,
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
-- EMPIEZA SISTEMA DE AUDITORÍA
-- ============================================
CREATE TABLE CAMBIOS (
    ID_aud INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    Fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    Usuario_BD VARCHAR(50),  -- Usuario de MySQL (angel, mauro, erick, adrian)
    ROL VARCHAR(25),  -- Rol en la aplicación (estudiante, empresa, etc.)
    Accion ENUM('INSERT','UPDATE','DELETE') NOT NULL,
    ID_afectado SMALLINT,
    Usuario_nombre VARCHAR(100)  -- Nombre del usuario afectado
);

-- ============================================
-- TRIGGERS
-- ============================================
DELIMITER $$

-- TRIGGER PARA INSERT EN USUARIO
DROP TRIGGER IF EXISTS usuario_insert $$
CREATE TRIGGER usuario_insert
AFTER INSERT ON Usuario
FOR EACH ROW
BEGIN
    DECLARE v_usuario_bd VARCHAR(50);
    SET v_usuario_bd = SUBSTRING_INDEX(USER(), '@', 1);
    
    INSERT INTO CAMBIOS (Usuario_BD, ROL, Accion, ID_afectado, Usuario_nombre)
    VALUES (
        v_usuario_bd,
        NEW.rol,
        'INSERT',
        NEW.idUsuario,
        NEW.nombre
    );
END $$

DROP TRIGGER IF EXISTS usuario_update $$
CREATE TRIGGER usuario_update
AFTER UPDATE ON Usuario
FOR EACH ROW
BEGIN
    DECLARE v_usuario_bd VARCHAR(50);
    SET v_usuario_bd = SUBSTRING_INDEX(USER(), '@', 1);
    
    INSERT INTO CAMBIOS (Usuario_BD, ROL, Accion, ID_afectado, Usuario_nombre)
    VALUES (
        v_usuario_bd,
        NEW.rol,
        'UPDATE',
        NEW.idUsuario,
        NEW.nombre
    );
END $$

DROP TRIGGER IF EXISTS usuario_delete $$
CREATE TRIGGER usuario_delete
AFTER DELETE ON Usuario
FOR EACH ROW
BEGIN
    DECLARE v_usuario_bd VARCHAR(50);
    SET v_usuario_bd = SUBSTRING_INDEX(USER(), '@', 1);
    
    INSERT INTO CAMBIOS (Usuario_BD, ROL, Accion, ID_afectado, Usuario_nombre)
    VALUES (
        v_usuario_bd,
        OLD.rol,
        'DELETE',
        OLD.idUsuario,
        OLD.nombre
    );
END $$
DELIMITER ;
-- ============================================
-- TERMINA SISTEMA DE AUDITORÍA
-- ============================================
-- ============================================
-- CREAR ROLES
-- ============================================
DROP ROLE IF EXISTS 'administrador';
DROP ROLE IF EXISTS 'empresa';
DROP ROLE IF EXISTS 'supervisor';
DROP ROLE IF EXISTS 'estudiante';
DROP USER IF EXISTS 'angel'@'localhost';
DROP USER IF EXISTS 'mauro'@'localhost';
DROP USER IF EXISTS 'erick'@'localhost';
DROP USER IF EXISTS 'adrian'@'localhost';

CREATE ROLE 'administrador';
CREATE ROLE 'empresa';
CREATE ROLE 'supervisor';
CREATE ROLE 'estudiante';

-- ============================================
-- PERMISOS POR ROL
-- ============================================
GRANT ALL PRIVILEGES ON solinx.* TO 'administrador';
GRANT SELECT ON solinx.* TO 'empresa';
GRANT INSERT, UPDATE, DELETE ON solinx.Proyecto TO 'empresa';
GRANT SELECT, UPDATE ON solinx.Solicitud TO 'empresa';
GRANT SELECT ON solinx.* TO 'supervisor';
GRANT SELECT ON solinx.Proyecto TO 'estudiante';
GRANT SELECT ON solinx.Empresa TO 'estudiante';
GRANT INSERT, SELECT ON solinx.Solicitud TO 'estudiante';
-- ============================================
-- CREAR USUARIOS
-- ============================================
CREATE USER 'angel'@'localhost' IDENTIFIED BY '12345';
CREATE USER 'mauro'@'localhost' IDENTIFIED BY '12345';
CREATE USER 'erick'@'localhost' IDENTIFIED BY '12345';
CREATE USER 'adrian'@'localhost' IDENTIFIED BY '12345';
-- ============================================
-- ASIGNAR ROLES A USUARIOS
-- ============================================
GRANT 'administrador' TO 'angel'@'localhost';
GRANT 'empresa' TO 'mauro'@'localhost';
GRANT 'supervisor' TO 'erick'@'localhost';
GRANT 'estudiante' TO 'adrian'@'localhost';
SET DEFAULT ROLE ALL TO 'angel'@'localhost';
SET DEFAULT ROLE ALL TO 'mauro'@'localhost';
SET DEFAULT ROLE ALL TO 'erick'@'localhost';
SET DEFAULT ROLE ALL TO 'adrian'@'localhost';
FLUSH PRIVILEGES;
-- ============================================
-- TERMINA CONFIGURACIÓN DE ROLES
-- ============================================
-- ESTUDIANTE
INSERT INTO Estudiante (boleta, carrera, escuela) VALUES
(20230001, 'Ingeniería en Software', 'ESCOM'),
(20230002, 'Ingeniería Industrial', 'UPIICSA'),
(20230003, 'Ingeniería Mecatrónica', 'UPIITA'),
(20230004, 'Ingeniería Informática', 'UPIICSA'),
(20230005, 'Ingeniería Aeronáutica', 'ESIA Ticomán');

-- EMPRESA
INSERT INTO Empresa (nombreEmpresa) VALUES
('TechNova'),
('AeroDynamics MX'),
('SoftSolutions'),
('ElectroCorp');

-- SUPERVISOR
INSERT INTO Supervisor (area, idEmpresa) VALUES
('Recursos Humanos', 1),
('Desarrollo de Software', 1),
('Proyectos Aeronáuticos', 2),
('Sistemas', 3);

-- USUARIO
INSERT INTO Usuario (nombre, correo, telefono, userPassword, rol) VALUES
('Mauro López', 'mauro@correo.com', '5512345678', 'pass123', 'estudiante'),
('Laura Tech', 'laura@technova.com', '5588991122', 'empresa123', 'empresa'),
('Carlos Supervisor', 'carlos@technova.com', '5599001122', 'sup123', 'supervisor'),
('Admin Master', 'admin@solinx.com', '5500112233', 'admin123', 'administrador'),
('Sofía Ramírez', 'sofia@correo.com', '5544332211', 'pas123', 'estudiante');

-- USUARIOESTUDIANTE
INSERT INTO UsuarioEstudiante (idUsuario, boleta) VALUES
(1, 20230001),
(5, 20230005);

-- USUARIOEMPRESA
INSERT INTO UsuarioEmpresa (idUsuario, idEmpresa) VALUES
(2, 1);

-- USUARIOSUPERVISOR
INSERT INTO UsuarioSupervisor (idUsuario, idSupervisor) VALUES
(3, 1),
(2, 2);

-- PROYECTO
INSERT INTO Proyecto (nombreProyecto, objetivo, vacantes, ubicacion, justificacion, idEmpresa) VALUES
('Sistema de Gestión Escolar', 'Crear un sistema web completo para control escolar.', 3, 'CDMX', 'Proyecto integral para ESCOM', 1),
('Dron Autónomo', 'Desarrollo de un dron con navegación automática.', 2, 'Querétaro', 'Aporta innovación aeronáutica', 2),
('App de Inventarios', 'Crear app móvil para control de inventarios.', 4, 'CDMX', 'Solución para empresas pequeñas', 3),
('Sistema de Energía Inteligente', 'Monitoreo de consumo energético.', 2, 'Edomex', 'Ahorro de energía', 4);

-- SOLICITUD
INSERT INTO Solicitud (estadoSolicitud, boleta, idProyecto) VALUES
('enviada', 20230001, 1),
('aceptada', 20230002, 1),
('rechazada', 20230003, 2),
('enviada', 20230004, 3),
('enviada', 20230005, 4),
('aceptada', 20230001, 2),
('rechazada', 20230005, 1);

-- PERFIL
INSERT INTO Perfil (tema, idUsuario) VALUES
('claro', 1),
('oscuro', 2),
('claro', 3),
('oscuro', 4),
('claro', 5);

-- ============================================
-- CONSULTA MEJORADA PARA VER AUDITORÍA
-- ============================================
SELECT * FROM CAMBIOS
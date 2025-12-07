-- ============================================
-- SOLINX - BASE DE DATOS FUSIONADA Y COMPLETA
-- ============================================
DROP DATABASE IF EXISTS solinx;
CREATE DATABASE IF NOT EXISTS solinx CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE solinx;

-- ============================================
-- TABLAS PRINCIPALES
-- ============================================

CREATE TABLE IF NOT EXISTS Estudiante (
    boleta INT NOT NULL PRIMARY KEY,
    carrera VARCHAR(100) NOT NULL DEFAULT '',
    escuela VARCHAR(100) NOT NULL DEFAULT ''
);

CREATE TABLE IF NOT EXISTS Empresa (
    idEmpresa INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombreEmpresa VARCHAR(100) NOT NULL DEFAULT ''
);

CREATE TABLE IF NOT EXISTS Supervisor (
    idSupervisor INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    area VARCHAR(100) NOT NULL DEFAULT '',
    idEmpresa INT NOT NULL,
    FOREIGN KEY (idEmpresa) REFERENCES Empresa(idEmpresa) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Usuario (
    idUsuario INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(150) UNIQUE NOT NULL,
    telefono VARCHAR(20) DEFAULT NULL,
    userPassword VARCHAR(255) NOT NULL,
    rol ENUM('estudiante', 'empresa', 'supervisor', 'administrador') NOT NULL
);

-- ============================================
-- TABLAS DE RELACIÓN (Usuario - Rol)
-- ============================================

CREATE TABLE IF NOT EXISTS UsuarioEstudiante (
    idUsuario INT NOT NULL,
    boleta INT NOT NULL,
    PRIMARY KEY (idUsuario),
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (boleta) REFERENCES Estudiante(boleta) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS UsuarioEmpresa (
    idUsuario INT NOT NULL,
    idEmpresa INT NOT NULL,
    PRIMARY KEY (idUsuario),
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (idEmpresa) REFERENCES Empresa(idEmpresa) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS UsuarioSupervisor (
    idUsuario INT NOT NULL,
    idSupervisor INT NOT NULL,
    PRIMARY KEY (idUsuario),
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (idSupervisor) REFERENCES Supervisor(idSupervisor) ON UPDATE CASCADE ON DELETE CASCADE
);

-- ============================================
-- TABLAS DE NEGOCIO
-- ============================================

CREATE TABLE IF NOT EXISTS Proyecto (
    idProyecto INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombreProyecto VARCHAR(255) NOT NULL DEFAULT '',
    carreraEnfocada VARCHAR(150) DEFAULT NULL,
    imagenRef VARCHAR(100) DEFAULT 'img_default_proyecto',
    objetivo TEXT NOT NULL,
    fechaInicio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    vacantes INT NOT NULL DEFAULT 1,
    ubicacion VARCHAR(100) NOT NULL DEFAULT '',
    justificacion VARCHAR(255) NOT NULL DEFAULT '',
    fechaTermino TIMESTAMP NULL DEFAULT NULL,
    idEmpresa INT NOT NULL,
    FOREIGN KEY (idEmpresa) REFERENCES Empresa(idEmpresa) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Solicitud (
    idSolicitud INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    fechaSolicitud TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estadoSolicitud ENUM('enviada', 'aceptada', 'rechazada', 'aprobada') NOT NULL DEFAULT 'enviada',
    boleta INT NOT NULL,
    idProyecto INT NOT NULL,
    FOREIGN KEY (boleta) REFERENCES Estudiante(boleta) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (idProyecto) REFERENCES Proyecto(idProyecto) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Perfil (
    idPerfil INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    foto BLOB,
    tema ENUM('claro', 'oscuro') NOT NULL DEFAULT 'claro',
    idUsuario INT NOT NULL,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario) ON UPDATE CASCADE ON DELETE CASCADE
);

-- ============================================
-- SISTEMA DE AUDITORÍA
-- ============================================

CREATE TABLE IF NOT EXISTS CAMBIOS (
    ID_aud INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    Fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    Usuario_BD VARCHAR(50),
    ROL VARCHAR(25),
    Accion ENUM('INSERT','UPDATE','DELETE') NOT NULL,
    ID_afectado SMALLINT,
    Usuario_nombre VARCHAR(100)
);

DELIMITER $$

DROP TRIGGER IF EXISTS usuario_insert $$
CREATE TRIGGER usuario_insert
AFTER INSERT ON Usuario FOR EACH ROW
BEGIN
    INSERT INTO CAMBIOS (Usuario_BD, ROL, Accion, ID_afectado, Usuario_nombre)
    VALUES (SUBSTRING_INDEX(USER(), '@', 1), NEW.rol, 'INSERT', NEW.idUsuario, NEW.nombre);
END $$

DROP TRIGGER IF EXISTS usuario_update $$
CREATE TRIGGER usuario_update
AFTER UPDATE ON Usuario FOR EACH ROW
BEGIN
    INSERT INTO CAMBIOS (Usuario_BD, ROL, Accion, ID_afectado, Usuario_nombre)
    VALUES (SUBSTRING_INDEX(USER(), '@', 1), NEW.rol, 'UPDATE', NEW.idUsuario, NEW.nombre);
END $$

DROP TRIGGER IF EXISTS usuario_delete $$
CREATE TRIGGER usuario_delete
AFTER DELETE ON Usuario FOR EACH ROW
BEGIN
    INSERT INTO CAMBIOS (Usuario_BD, ROL, Accion, ID_afectado, Usuario_nombre)
    VALUES (SUBSTRING_INDEX(USER(), '@', 1), OLD.rol, 'DELETE', OLD.idUsuario, OLD.nombre);
END $$

DELIMITER ;

-- ============================================
-- DATOS INICIALES - ESTUDIANTES (15 total)
-- ============================================

INSERT INTO Estudiante (boleta, carrera, escuela) VALUES
(20230001, 'Ingeniería en Software', 'ESCOM'),
(20230002, 'Ingeniería Industrial', 'UPIICSA'),
(20230003, 'Ingeniería Mecatrónica', 'UPIITA'),
(20230004, 'Ingeniería Informática', 'UPIICSA'),
(20230005, 'Ingeniería Aeronáutica', 'ESIA Ticomán'),
(20250001, 'Ingeniería en Sistemas Computacionales', 'ESCOM'),
(20250002, 'Ingeniería Mecatrónica', 'UPIITA'),
(20250003, 'Licenciatura en Ciencia de Datos', 'ESFM'),
(20250004, 'Ingeniería en Telecomunicaciones', 'ESCOM'),
(20250005, 'Ingeniería Industrial', 'UPIICSA'),
(20250006, 'Ingeniería Biomédica', 'UPIBI'),
(20250007, 'Ingeniería Automotriz', 'UPIITA'),
(20250008, 'Ingeniería en Computación', 'ESCOM'),
(20250009, 'Ingeniería Aeronáutica', 'ESIA Ticomán'),
(20250010, 'Ingeniería Electrónica', 'ESIME Culhuacán');

-- ============================================
-- EMPRESAS (6 empresas)
-- ============================================

INSERT INTO Empresa (nombreEmpresa) VALUES
('TechNova'),        -- ID 1
('AeroDynamics MX'), -- ID 2
('SoftSolutions'),   -- ID 3
('ElectroCorp'),     -- ID 4
('Aa'),              -- ID 5
('Ooo');             -- ID 6

-- ============================================
-- SUPERVISORES (6 supervisores)
-- ============================================

INSERT INTO Supervisor (area, idEmpresa) VALUES
('Recursos Humanos', 1),
('Desarrollo de Software', 1),
('Proyectos Aeronáuticos', 2),
('Sistemas', 3),
('Recursos Humanos', 5),
('Operaciones', 6);

-- ============================================
-- USUARIOS (20 usuarios totales)
-- ============================================

INSERT INTO Usuario (nombre, correo, telefono, userPassword, rol) VALUES
-- Usuarios base (4)
('Mauro López', 'mauro@correo.com', '5512345678', 'pass123', 'estudiante'),
('Laura Tech', 'laura@technova.com', '5588991122', 'empresa123', 'empresa'),
('Carlos Supervisor', 'carlos@technova.com', '5599001122', 'sup123', 'supervisor'),
('Admin Master', 'admin@solinx.com', '5500112233', 'admin123', 'administrador'),
-- Estudiantes adicionales (11)
('Sofía Ramírez', 'sofia@correo.com', '5544332211', 'pass123', 'estudiante'),
('Ana Martínez', 'ana.martinez@alumno.ipn.mx', '5512340001', 'ana123', 'estudiante'),
('Bruno Fernández', 'bruno.fernandez@alumno.ipn.mx', '5512340002', 'bruno123', 'estudiante'),
('Carla Jiménez', 'carla.jimenez@alumno.ipn.mx', '5512340003', 'carla123', 'estudiante'),
('Diego Torres', 'diego.torres@alumno.ipn.mx', '5512340004', 'diego123', 'estudiante'),
('Elena Vega', 'elena.vega@alumno.ipn.mx', '5512340005', 'elena123', 'estudiante'),
('Fernando López', 'fernando.lopez@alumno.ipn.mx', '5512340006', 'fernando123', 'estudiante'),
('Gabriela Ruiz', 'gabriela.ruiz@alumno.ipn.mx', '5512340007', 'gaby123', 'estudiante'),
('Héctor Morales', 'hector.morales@alumno.ipn.mx', '5512340008', 'hector123', 'estudiante'),
('Isabel Castro', 'isabel.castro@alumno.ipn.mx', '5512340009', 'isabel123', 'estudiante'),
('Jorge Méndez', 'jorge.mendez@alumno.ipn.mx', '5512340010', 'jorge123', 'estudiante'),
('María González', 'maria.gonzalez@alumno.ipn.mx', '5512340011', 'maria123', 'estudiante'),
('Pedro Sánchez', 'pedro.sanchez@alumno.ipn.mx', '5512340012', 'pedro123', 'estudiante'),
('Laura Ramírez', 'laura.ramirez@alumno.ipn.mx', '5512340013', 'laura123', 'estudiante'),
-- Empresas adicionales (2)
('Empresa Aa', 'aa@gmail.com', '5511111111', '111', 'empresa'),
('Empresa Ooo', 'oo@gmail.com', '5522222222', '222', 'empresa'),
-- Supervisores adicionales (2)
('Supervisor Aa', 'sup.aa@gmail.com', '5533333333', '333', 'supervisor'),
('Supervisor Ooo', 'sup.oo@gmail.com', '5544444444', '444', 'supervisor');

-- ============================================
-- VÍNCULOS USUARIO-ESTUDIANTE (15 vínculos)
-- ============================================

INSERT INTO UsuarioEstudiante (idUsuario, boleta) VALUES
(1, 20230001),   -- Mauro López
(5, 20230005),   -- Sofía Ramírez
(6, 20250001),   -- Ana Martínez
(7, 20250002),   -- Bruno Fernández
(8, 20250003),   -- Carla Jiménez
(9, 20250004),   -- Diego Torres
(10, 20250005),  -- Elena Vega
(11, 20250006),  -- Fernando López
(12, 20250007),  -- Gabriela Ruiz
(13, 20250008),  -- Héctor Morales
(14, 20250009),  -- Isabel Castro
(15, 20250010),  -- Jorge Méndez
(16, 20230002),  -- María González
(17, 20230003),  -- Pedro Sánchez
(18, 20230004);  -- Laura Ramírez

-- ============================================
-- VÍNCULOS USUARIO-EMPRESA (3 vínculos)
-- ============================================

INSERT INTO UsuarioEmpresa (idUsuario, idEmpresa) VALUES
(2, 1),  -- Laura Tech -> TechNova
(19, 5), -- Empresa Aa -> Aa
(20, 6); -- Empresa Ooo -> Ooo

-- ============================================
-- VÍNCULOS USUARIO-SUPERVISOR (3 vínculos)
-- ============================================

INSERT INTO UsuarioSupervisor (idUsuario, idSupervisor) VALUES
(3, 1),  -- Carlos Supervisor -> TechNova RH
(21, 5), -- Supervisor Aa -> Aa RH
(22, 6); -- Supervisor Ooo -> Ooo Operaciones

-- ============================================
-- PROYECTOS (6 proyectos)
-- ============================================

INSERT INTO Proyecto (nombreProyecto, carreraEnfocada, objetivo, vacantes, ubicacion, justificacion, imagenRef, idEmpresa) VALUES
('Sistema de Gestión Escolar', 'Ingeniería en Software', 'Crear un sistema web completo para control escolar.', 3, 'CDMX', 'Proyecto integral para ESCOM', 'img_gestion', 1),
('Dron Autónomo', 'Ingeniería Aeronáutica', 'Desarrollo de un dron con navegación automática.', 2, 'Querétaro', 'Aporta innovación aeronáutica', 'img_dron', 2),
('App de Inventarios', 'Logística Industrial', 'Crear app móvil para control de inventarios.', 4, 'CDMX', 'Solución para empresas pequeñas', 'img_inventario', 3),
('Sistema de Energía Inteligente', 'Ingeniería Eléctrica', 'Monitoreo de consumo energético.', 2, 'Edomex', 'Ahorro de energía', 'img_energia', 4),
('Sistema de Pagos Aa', 'Desarrollo Backend', 'Crear API segura para pagos', 2, 'Remoto', 'Modernización de sistemas', 'img_default_proyecto', 5),
('Chatbot Ooo V2', 'Inteligencia Artificial', 'Asistente virtual inteligente', 3, 'CDMX', 'Atención al cliente 24/7', 'img_default_proyecto', 6);

-- ============================================
-- PERFILES (22 perfiles)
-- ============================================

INSERT INTO Perfil (tema, idUsuario) VALUES
('claro', 1), ('oscuro', 2), ('claro', 3), ('oscuro', 4), ('claro', 5),
('claro', 6), ('oscuro', 7), ('claro', 8), ('oscuro', 9), ('claro', 10),
('oscuro', 11), ('claro', 12), ('oscuro', 13), ('claro', 14), ('oscuro', 15),
('claro', 16), ('oscuro', 17), ('claro', 18), ('oscuro', 19), ('claro', 20),
('claro', 21), ('oscuro', 22);

-- ============================================
-- SOLICITUDES PARA EMPRESA Aa (idEmpresa=5, idProyecto=5)
-- ============================================

-- 10 SOLICITUDES ENVIADAS (para "Aprobar Solicitudes de Alumnos")
INSERT INTO Solicitud (fechaSolicitud, estadoSolicitud, boleta, idProyecto) VALUES
(NOW(), 'enviada', 20250001, 5),  -- Ana Martínez
(NOW(), 'enviada', 20250002, 5),  -- Bruno Fernández
(NOW(), 'enviada', 20250003, 5),  -- Carla Jiménez
(NOW(), 'enviada', 20250004, 5),  -- Diego Torres
(NOW(), 'enviada', 20250005, 5),  -- Elena Vega
(NOW(), 'enviada', 20250006, 5),  -- Fernando López
(NOW(), 'enviada', 20250007, 5),  -- Gabriela Ruiz
(NOW(), 'enviada', 20250008, 5),  -- Héctor Morales
(NOW(), 'enviada', 20250009, 5),  -- Isabel Castro
(NOW(), 'enviada', 20250010, 5);  -- Jorge Méndez

-- 8 SOLICITUDES ACEPTADAS (para "Aprobar Aceptaciones de Empresas")
INSERT INTO Solicitud (fechaSolicitud, estadoSolicitud, boleta, idProyecto) VALUES
(DATE_SUB(NOW(), INTERVAL 5 DAY), 'aceptada', 20230001, 5),   -- Mauro López
(DATE_SUB(NOW(), INTERVAL 6 DAY), 'aceptada', 20230002, 5),   -- María González
(DATE_SUB(NOW(), INTERVAL 7 DAY), 'aceptada', 20230003, 5),   -- Pedro Sánchez
(DATE_SUB(NOW(), INTERVAL 8 DAY), 'aceptada', 20230004, 5),   -- Laura Ramírez
(DATE_SUB(NOW(), INTERVAL 9 DAY), 'aceptada', 20230005, 5),   -- Sofía Ramírez
(DATE_SUB(NOW(), INTERVAL 10 DAY), 'aceptada', 20250001, 5),  -- Ana Martínez
(DATE_SUB(NOW(), INTERVAL 11 DAY), 'aceptada', 20250002, 5),  -- Bruno Fernández
(DATE_SUB(NOW(), INTERVAL 12 DAY), 'aceptada', 20250003, 5);  -- Carla Jiménez

-- ============================================
-- SOLICITUDES PARA EMPRESA Ooo (idEmpresa=6, idProyecto=6)
-- ============================================

-- 5 SOLICITUDES ENVIADAS
INSERT INTO Solicitud (fechaSolicitud, estadoSolicitud, boleta, idProyecto) VALUES
(NOW(), 'enviada', 20230001, 6),
(NOW(), 'enviada', 20230002, 6),
(NOW(), 'enviada', 20230003, 6),
(NOW(), 'enviada', 20230004, 6),
(NOW(), 'enviada', 20230005, 6);

-- 3 SOLICITUDES ACEPTADAS
INSERT INTO Solicitud (fechaSolicitud, estadoSolicitud, boleta, idProyecto) VALUES
(DATE_SUB(NOW(), INTERVAL 5 DAY), 'aceptada', 20250004, 6),
(DATE_SUB(NOW(), INTERVAL 6 DAY), 'aceptada', 20250005, 6),
(DATE_SUB(NOW(), INTERVAL 7 DAY), 'aceptada', 20250006, 6);

-- ============================================
-- CONSULTAS DE VERIFICACIÓN
-- ============================================

-- Ver todos los supervisores con sus empresas
SELECT 
    u.idUsuario,
    u.nombre,
    u.correo,
    u.userPassword AS password,
    s.idSupervisor,
    s.area,
    e.idEmpresa,
    e.nombreEmpresa
FROM Usuario u
JOIN UsuarioSupervisor us ON u.idUsuario = us.idUsuario
JOIN Supervisor s ON us.idSupervisor = s.idSupervisor
JOIN Empresa e ON s.idEmpresa = e.idEmpresa
WHERE u.rol = 'supervisor'
ORDER BY u.idUsuario;

-- Conteo de solicitudes por empresa
SELECT 
    emp.nombreEmpresa,
    sol.estadoSolicitud,
    COUNT(*) AS cantidad
FROM Solicitud sol
JOIN Proyecto proj ON sol.idProyecto = proj.idProyecto
JOIN Empresa emp ON proj.idEmpresa = emp.idEmpresa
WHERE emp.idEmpresa IN (5, 6)
GROUP BY emp.nombreEmpresa, sol.estadoSolicitud
ORDER BY emp.nombreEmpresa, sol.estadoSolicitud;

-- Verificar solicitudes aceptadas de Aa con nombres
SELECT 
    sol.idSolicitud,
    sol.estadoSolicitud,
    sol.boleta,
    u.nombre AS nombreEstudiante,
    proj.nombreProyecto,
    emp.nombreEmpresa
FROM Solicitud sol
JOIN Estudiante est ON sol.boleta = est.boleta
JOIN UsuarioEstudiante ue ON est.boleta = ue.boleta
JOIN Usuario u ON ue.idUsuario = u.idUsuario
JOIN Proyecto proj ON sol.idProyecto = proj.idProyecto
JOIN Empresa emp ON proj.idEmpresa = emp.idEmpresa
WHERE emp.idEmpresa = 5 AND sol.estadoSolicitud = 'aceptada'
ORDER BY sol.fechaSolicitud DESC;

-- Verificar cuentas de login para empresas
SELECT * FROM Usuario WHERE correo IN ('aa@gmail.com', 'oo@gmail.com');

-- Ver todas las solicitudes
SELECT * FROM Solicitud ORDER BY fechaSolicitud DESC;

-- ============================================
-- RESUMEN DE CUENTAS DE PRUEBA
-- ============================================
/*
CUENTAS DE LOGIN DISPONIBLES:

ESTUDIANTES:
- mauro@correo.com / pass123
- sofia@correo.com / pass123
- ana.martinez@alumno.ipn.mx / ana123
- ... (más estudiantes)

EMPRESAS:
- laura@technova.com / empresa123 (TechNova)
- aa@gmail.com / 111 (Aa)
- oo@gmail.com / 222 (Ooo)

SUPERVISORES:
- carlos@technova.com / sup123 (TechNova)
- sup.aa@gmail.com / 333 (Aa)
- sup.oo@gmail.com / 444 (Ooo)

ADMINISTRADOR:
- admin@solinx.com / admin123
*/

-- FIN DEL SCRIPT
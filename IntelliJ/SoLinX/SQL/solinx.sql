USE SoLinX;
SELECT * FROM usuario;
SELECT * FROM perfil;
SELECT * FROM estudiante;
SELECT * FROM solicitud;

-- Si un campo tiene mayusculas hacer, lo siguiente --
ALTER TABLE usuario CHANGE idUsuario id_usuario INT NOT NULL AUTO_INCREMENT;
ALTER TABLE usuario CHANGE userPassword user_password VARCHAR(255) NOT NULL;
ALTER TABLE perfil CHANGE idPerfil id_perfil INT NOT NULL AUTO_INCREMENT;
ALTER TABLE perfil CHANGE idUsuario id_usuario INT NOT NULL;
ALTER TABLE solicitud CHANGE idSolicitud id_solicitud INT NOT NULL AUTO_INCREMENT;
ALTER TABLE solicitud CHANGE fechaSolicitud fecha_solicitud TIMESTAMP NOT NULL;
ALTER TABLE solicitud CHANGE estadoSolicitud estado_solicitud INT NOT NULL;
ALTER TABLE solicitud CHANGE idProyecto id_proyecto INT NOT NULL;

describe solicitud;
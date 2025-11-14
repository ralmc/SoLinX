USE SoLinX;
SELECT * FROM usuario;
INSERT INTO usuario (idUsuario, nombre, correo, telefono, userPassword, rol) VALUES (1,"1","1","1","1","1");

-- Si un campo tiene mayusculas hacer, lo siguiente --
ALTER TABLE usuario CHANGE idUsuario id_usuario INT NOT NULL AUTO_INCREMENT;
ALTER TABLE usuario CHANGE userPassword user_password VARCHAR(255) NOT NULL;

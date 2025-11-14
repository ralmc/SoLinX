USE SoLinX;
SELECT * FROM usuario;
SELECT * FROM perfil;

-- Si un campo tiene mayusculas hacer, lo siguiente --
ALTER TABLE usuario CHANGE idUsuario id_usuario INT NOT NULL AUTO_INCREMENT;
ALTER TABLE usuario CHANGE userPassword user_password VARCHAR(255) NOT NULL;
ALTER TABLE perfil CHANGE idPerfil id_perfil INT NOT NULL AUTO_INCREMENT;
ALTER TABLE perfil CHANGE idUsuario id_usuario INT NOT NULL;
describe perfil;
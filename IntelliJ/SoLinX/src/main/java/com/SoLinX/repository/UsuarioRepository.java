package com.SoLinX.repository;

import com.SoLinX.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Usuario findByCorreoAndUserPassword(String correo, String userPassword);
    Usuario findByCorreo(String correo);

    @Query("SELECT u FROM Usuario u " +
            "JOIN UsuarioEmpresa ue ON u.idUsuario = ue.idUsuario " +
            "WHERE ue.idEmpresa = :idEmpresa")
    Usuario findByEmpresaId(@Param("idEmpresa") Integer idEmpresa);
}
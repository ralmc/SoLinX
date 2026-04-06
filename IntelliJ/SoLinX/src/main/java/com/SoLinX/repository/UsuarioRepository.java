package com.SoLinX.repository;

import com.SoLinX.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByCorreoAndUserPassword(String correo, String userPassword);
    Optional<Usuario> findByCorreo(String correo);

    @Query(value = "SELECT u.* FROM usuario u " +
            "JOIN UsuarioEmpresa ue ON u.idUsuario = ue.idUsuario " +
            "WHERE ue.idEmpresa = :idEmpresa", nativeQuery = true)
    Optional<Usuario> findByEmpresaId(@Param("idEmpresa") Integer idEmpresa);
}
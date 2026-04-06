package com.SoLinX.repository;

import com.SoLinX.model.TokenVerificacion;
import com.SoLinX.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenVerificacionRepository extends JpaRepository<TokenVerificacion, Integer> {

    Optional<TokenVerificacion> findByToken(String token);

    Optional<TokenVerificacion> findByUsuarioAndUsadoFalseAndFechaExpiraAfter(
            Usuario usuario, LocalDateTime ahora);

    @Query("""
        SELECT DISTINCT t.usuario FROM TokenVerificacion t
        WHERE t.usuario.verificado = false
          AND LOWER(t.usuario.rol) = 'empresa'
          AND t.fechaExpira < :limite
    """)
    List<Usuario> findEmpresasNoVerificadasExpiradas(LocalDateTime limite);

    @Modifying
    @Query("DELETE FROM TokenVerificacion t WHERE t.usuario = :usuario")
    void deleteAllByUsuario(Usuario usuario);
}
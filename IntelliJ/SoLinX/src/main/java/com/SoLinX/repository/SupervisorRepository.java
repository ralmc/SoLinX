package com.SoLinX.repository;

import com.SoLinX.model.Supervisor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupervisorRepository extends JpaRepository<Supervisor, Integer> {

    @Query("SELECT s FROM Supervisor s " +
            "JOIN UsuarioSupervisor us ON s.idSupervisor = us.idSupervisor " +
            "WHERE us.idUsuario = :idUsuario")
    Optional<Supervisor> findByUsuarioId(@Param("idUsuario") Integer idUsuario);
}
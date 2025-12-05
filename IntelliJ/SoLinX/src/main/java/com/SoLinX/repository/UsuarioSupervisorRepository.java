package com.SoLinX.repository;

import com.SoLinX.model.UsuarioSupervisor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioSupervisorRepository extends JpaRepository<UsuarioSupervisor, Integer> {

    UsuarioSupervisor findByIdUsuario(Integer idUsuario);
}
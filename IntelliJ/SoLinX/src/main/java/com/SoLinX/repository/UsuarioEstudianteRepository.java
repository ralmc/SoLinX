package com.SoLinX.repository;

import com.SoLinX.model.UsuarioEstudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioEstudianteRepository extends JpaRepository<UsuarioEstudiante, Integer> {}

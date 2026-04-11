package com.SoLinX.repository;

import com.SoLinX.model.UsuarioEstudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioEstudianteRepository extends JpaRepository<UsuarioEstudiante, Integer> {
    Optional<UsuarioEstudiante> findByIdUsuario(Integer idUsuario);
    Optional<UsuarioEstudiante> findByBoleta(Integer boleta);
}
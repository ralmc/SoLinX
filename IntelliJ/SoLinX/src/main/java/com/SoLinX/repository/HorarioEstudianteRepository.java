package com.SoLinX.repository;

import com.SoLinX.model.HorarioEstudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HorarioEstudianteRepository extends JpaRepository<HorarioEstudiante, Integer> {
    Optional<HorarioEstudiante> findByBoleta(Integer boleta);
}
package com.SoLinX.repository;

import com.SoLinX.model.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Integer> {

    List<Documento> findByEstudiante_Boleta(Integer boleta);
    Optional<Documento> findByEstudiante_BoletaAndPeriodo(Integer boleta, Integer periodo);
    boolean existsByEstudiante_BoletaAndPeriodo(Integer boleta, Integer periodo);
}
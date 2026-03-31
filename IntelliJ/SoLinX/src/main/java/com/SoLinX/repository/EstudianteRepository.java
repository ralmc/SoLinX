package com.SoLinX.repository;

import com.SoLinX.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Integer> {

    @Query("""
        SELECT e FROM Estudiante e
        WHERE e.boleta NOT IN (SELECT d.estudiante.boleta FROM Documento d)
        """)
    List<Estudiante> findEstudiantesSinDocumentos();
}
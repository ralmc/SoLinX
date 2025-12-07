package com.SoLinX.repository;

import com.SoLinX.model.Solicitud;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Integer> {

    @Query(value = """
        SELECT s.* 
        FROM Solicitud s
        JOIN Proyecto p ON s.idProyecto = p.idProyecto
        WHERE p.idEmpresa = :idEmpresa
    """, nativeQuery = true)
    List<Solicitud> findSolicitudesByEmpresa(@Param("idEmpresa") Integer idEmpresa);
}

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

    @Query(value = "SELECT sol.* FROM Solicitud sol " +
            "JOIN Proyecto proj ON sol.idProyecto = proj.idProyecto " +
            "JOIN Empresa emp ON proj.idEmpresa = emp.idEmpresa " +
            "JOIN Supervisor sup ON emp.idEmpresa = sup.idEmpresa " +
            "WHERE sup.idSupervisor = :idSupervisor AND sol.estadoSolicitud = 'enviada'",
            nativeQuery = true)
    List<Solicitud> findSolicitudesEnviadasBySupervisor(@Param("idSupervisor") Integer idSupervisor);

    @Query(value = "SELECT sol.* FROM Solicitud sol " +
            "JOIN Proyecto proj ON sol.idProyecto = proj.idProyecto " +
            "WHERE proj.idEmpresa = :idEmpresa AND sol.estadoSolicitud = 'aceptada'",
            nativeQuery = true)
    List<Solicitud> findSolicitudesAceptadasByEmpresa(@Param("idEmpresa") Integer idEmpresa);

    @Query("SELECT s FROM Solicitud s " +
            "JOIN FETCH s.proyecto p " +
            "JOIN FETCH p.empresa e " +
            "JOIN FETCH s.estudiante est " +
            "WHERE est.boleta = :boleta")
    List<Solicitud> findByBoleta(@Param("boleta") Integer boleta);
}
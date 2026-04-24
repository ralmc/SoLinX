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
          AND s.estadoSolicitud IN ('aprobada_supervisor', 'aceptada', 'rechazada_empresa')
    """, nativeQuery = true)
    List<Solicitud> findSolicitudesByEmpresa(@Param("idEmpresa") Integer idEmpresa);

    /**
     * El supervisor ve TODAS las solicitudes en estado 'enviada' sin importar la empresa.
     * El parámetro idSupervisor se mantiene por compatibilidad del endpoint pero no se usa.
     */
    @Query(value = "SELECT sol.* FROM Solicitud sol " +
            "WHERE sol.estadoSolicitud = 'enviada'",
            nativeQuery = true)
    List<Solicitud> findSolicitudesEnviadasBySupervisor(@Param("idSupervisor") Integer idSupervisor);

    /**
     * El supervisor ve TODAS las solicitudes en estado 'aceptada' (las que la empresa ya admitió)
     * sin importar de qué empresa sean. El parámetro idEmpresa se mantiene por compatibilidad.
     */
    @Query(value = "SELECT sol.* FROM Solicitud sol " +
            "WHERE sol.estadoSolicitud = 'aceptada'",
            nativeQuery = true)
    List<Solicitud> findSolicitudesAceptadasByEmpresa(@Param("idEmpresa") Integer idEmpresa);

    @Query("SELECT s FROM Solicitud s " +
            "JOIN FETCH s.proyecto p " +
            "JOIN FETCH p.empresa e " +
            "JOIN FETCH s.estudiante est " +
            "WHERE est.boleta = :boleta")
    List<Solicitud> findByBoleta(@Param("boleta") Integer boleta);

    @Query("SELECT s FROM Solicitud s WHERE s.estudiante.boleta = :boleta " +
            "AND s.idSolicitud <> :idSolicitudAceptada " +
            "AND s.estadoSolicitud = 'enviada'")
    List<Solicitud> findSolicitudesPendientesByBoletaExcluding(
            @Param("boleta") Integer boleta,
            @Param("idSolicitudAceptada") Integer idSolicitudAceptada
    );

    @Query(value = "SELECT sol.idProyecto FROM Solicitud sol " +
            "WHERE sol.boleta = :boleta " +
            "AND sol.estadoSolicitud IN ('enviada', 'aprobada_supervisor', 'aceptada', 'aprobada')",
            nativeQuery = true)
    List<Integer> findIdProyectosEnProcesoPorBoleta(@Param("boleta") Integer boleta);

    @Query("SELECT s FROM Solicitud s " +
            "JOIN FETCH s.proyecto p " +
            "JOIN FETCH p.empresa e " +
            "JOIN FETCH s.estudiante est " +
            "WHERE est.boleta = :boleta AND s.estadoSolicitud = 'aceptada'")
    List<Solicitud> findSolicitudAprobadaByBoleta(@Param("boleta") Integer boleta);
}

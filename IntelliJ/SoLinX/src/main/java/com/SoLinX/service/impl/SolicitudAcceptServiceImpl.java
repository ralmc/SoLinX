package com.SoLinX.service.impl;

import com.SoLinX.dto.SolicitudAcceptDto;
import com.SoLinX.model.Proyecto;
import com.SoLinX.model.Solicitud;
import com.SoLinX.repository.ProyectoRepository;
import com.SoLinX.repository.SolicitudRepository;
import com.SoLinX.service.NotificacionService;
import com.SoLinX.service.SolicitudAcceptService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class SolicitudAcceptServiceImpl implements SolicitudAcceptService {
    private final SolicitudRepository solicitudRepository;
    private final ProyectoRepository proyectoRepository;
    private final NotificacionService notificacionService;

    public SolicitudAcceptServiceImpl(SolicitudRepository solicitudRepository,
                                      ProyectoRepository proyectoRepository,
                                      NotificacionService notificacionService) {
        this.solicitudRepository = solicitudRepository;
        this.proyectoRepository = proyectoRepository;
        this.notificacionService = notificacionService;
    }

    /**
     * La EMPRESA acepta o rechaza una solicitud.
     * - Acepta: pasa a 'aceptada' (esperando visto bueno final del supervisor)
     * - Rechaza: pasa a 'rechazada_empresa'
     *
     * En ambos casos se notifica al alumno.
     */
    @Override
    public Solicitud aceptar(SolicitudAcceptDto dto) {
        Solicitud solicitud = solicitudRepository.findById(dto.getIdSolicitud())
                .orElseThrow(() -> new RuntimeException("La solicitud no existe"));

        String nombreProyecto = solicitud.getProyecto() != null
                ? solicitud.getProyecto().getNombreProyecto()
                : "proyecto";

        Integer idUsuarioAlumno = null;
        try {
            idUsuarioAlumno = solicitud.getEstudiante().getUsuarioEstudiante().getIdUsuario();
        } catch (Exception e) {
            System.out.println("No se pudo obtener idUsuario del alumno: " + e.getMessage());
        }

        if (Boolean.TRUE.equals(dto.getAceptado())) {
            // ─── EMPRESA ADMITE ───
            solicitud.setEstadoSolicitud("aceptada");
            solicitud.setFechaAceptacion(new Timestamp(System.currentTimeMillis()));
            solicitudRepository.save(solicitud);

            System.out.println("============================================");
            System.out.println("Empresa admitió solicitud ID: " + solicitud.getIdSolicitud());
            System.out.println("Estado: aceptada (esperando visto bueno del supervisor)");
            System.out.println("============================================");

            // Notificar al alumno
            if (idUsuarioAlumno != null) {
                notificacionService.crear(
                        idUsuarioAlumno,
                        "Empresa aceptó tu solicitud",
                        "La empresa aceptó tu solicitud al proyecto \"" + nombreProyecto +
                                "\". Esperando visto bueno final del supervisor."
                );
            }

        } else {
            // ─── EMPRESA RECHAZA ───
            solicitud.setEstadoSolicitud("rechazada_empresa");
            solicitudRepository.save(solicitud);

            System.out.println("============================================");
            System.out.println("Empresa rechazó solicitud ID: " + solicitud.getIdSolicitud());
            System.out.println("============================================");

            // Notificar al alumno
            if (idUsuarioAlumno != null) {
                notificacionService.crear(
                        idUsuarioAlumno,
                        "Solicitud rechazada por la empresa",
                        "La empresa rechazó tu solicitud al proyecto \"" + nombreProyecto + "\"."
                );
            }
        }

        return solicitud;
    }
}

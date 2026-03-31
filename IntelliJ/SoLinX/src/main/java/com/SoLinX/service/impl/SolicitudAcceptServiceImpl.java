package com.SoLinX.service.impl;

import com.SoLinX.dto.SolicitudAcceptDto;
import com.SoLinX.model.Proyecto;
import com.SoLinX.model.Solicitud;
import com.SoLinX.repository.ProyectoRepository;
import com.SoLinX.repository.SolicitudRepository;
import com.SoLinX.service.NotificacionService;
import com.SoLinX.service.SolicitudAcceptService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SolicitudAcceptServiceImpl implements SolicitudAcceptService {
    private final SolicitudRepository solicitudRepository;
    private final ProyectoRepository proyectoRepository;
    private final NotificacionService notificacionService;

    public SolicitudAcceptServiceImpl(SolicitudRepository solicitudRepository,
                                      ProyectoRepository proyectoRepository, NotificacionService notificacionService) {
        this.solicitudRepository = solicitudRepository;
        this.proyectoRepository  = proyectoRepository;
        this.notificacionService  = notificacionService;
    }

    @Override
    public Solicitud aceptar(SolicitudAcceptDto dto) {
        Solicitud solicitud = solicitudRepository.findById(dto.getIdSolicitud())
                .orElseThrow(() -> new RuntimeException("La solicitud no existe"));

        if (Boolean.TRUE.equals(dto.getAceptado())) {
            Proyecto proyecto = solicitud.getProyecto();
            System.out.println("============================================");
            System.out.println("Vacantes antes: " + proyecto.getVacantes());
            proyecto.setVacantes(proyecto.getVacantes() - 1);
            proyectoRepository.save(proyecto);
            System.out.println("Vacantes después: " + proyecto.getVacantes());
            System.out.println("============================================");

            solicitud.setEstadoSolicitud("aceptada");
            solicitudRepository.save(solicitud);

            Integer boleta = solicitud.getEstudiante().getBoleta();
            List<Solicitud> pendientes = solicitudRepository
                    .findSolicitudesPendientesByBoletaExcluding(boleta, dto.getIdSolicitud());

            System.out.println("============================================");
            System.out.println("Solicitudes a rechazar automáticamente: " + pendientes.size());
            for (Solicitud pendiente : pendientes) {
                pendiente.setEstadoSolicitud("rechazada");
                System.out.println("Rechazando solicitud ID: " + pendiente.getIdSolicitud());
            }
            solicitudRepository.saveAll(pendientes);
            System.out.println("============================================");

            Integer idUsuario = solicitud.getEstudiante().getUsuarioEstudiante().getIdUsuario();
            notificacionService.crear(
                    idUsuario,
                    "¡Felicidades! Solicitud aceptada 🎉",
                    "Tu solicitud al proyecto \"" + solicitud.getProyecto().getNombreProyecto() +
                            "\" ha sido aceptada."
            );

        } else {
            solicitud.setEstadoSolicitud("rechazada");
            solicitudRepository.save(solicitud);
        }

        return solicitud;
    }
}

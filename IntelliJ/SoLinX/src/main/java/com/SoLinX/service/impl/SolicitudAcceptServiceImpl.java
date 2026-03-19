package com.SoLinX.service.impl;

import com.SoLinX.dto.SolicitudAcceptDto;
import com.SoLinX.model.Proyecto;
import com.SoLinX.model.Solicitud;
import com.SoLinX.repository.ProyectoRepository;
import com.SoLinX.repository.SolicitudRepository;
import com.SoLinX.service.SolicitudAcceptService;
import org.springframework.stereotype.Service;

@Service
public class SolicitudAcceptServiceImpl implements SolicitudAcceptService {
    private final SolicitudRepository solicitudRepository;
    private final ProyectoRepository proyectoRepository;

    public SolicitudAcceptServiceImpl(SolicitudRepository solicitudRepository,
                                      ProyectoRepository proyectoRepository) {
        this.solicitudRepository = solicitudRepository;
        this.proyectoRepository  = proyectoRepository;
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
        } else {
            solicitud.setEstadoSolicitud("rechazada");
        }

        return solicitudRepository.save(solicitud);
    }
}
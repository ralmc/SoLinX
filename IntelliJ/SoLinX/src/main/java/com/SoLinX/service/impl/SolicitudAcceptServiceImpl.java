package com.SoLinX.service.impl;

import com.SoLinX.dto.SolicitudAcceptDto;
import com.SoLinX.model.Solicitud;
import com.SoLinX.repository.SolicitudRepository;
import com.SoLinX.service.SolicitudAcceptService;
import org.springframework.stereotype.Service;

@Service
public class SolicitudAcceptServiceImpl implements SolicitudAcceptService {

    private final SolicitudRepository solicitudRepository;

    public SolicitudAcceptServiceImpl(SolicitudRepository solicitudRepository) {
        this.solicitudRepository = solicitudRepository;
    }

    @Override
    public Solicitud aceptar(SolicitudAcceptDto dto) {

        Solicitud solicitud = solicitudRepository.findById(dto.getIdSolicitud())
                .orElseThrow(() -> new RuntimeException("La solicitud no existe"));

        solicitud.setEstadoSolicitud(dto.isAceptado()
                ? "aceptada"
                : "rechazada");

        return solicitudRepository.save(solicitud);
    }
}

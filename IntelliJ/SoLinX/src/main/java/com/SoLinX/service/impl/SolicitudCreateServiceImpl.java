package com.SoLinX.service.impl;

import com.SoLinX.dto.SolicitudCreateDto;
import com.SoLinX.model.Solicitud;
import com.SoLinX.repository.SolicitudRepository;
import com.SoLinX.service.SolicitudCreateService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@AllArgsConstructor
public class SolicitudCreateServiceImpl implements SolicitudCreateService {

    private final SolicitudRepository solicitudRepository;

    @Override
    public SolicitudCreateDto crearSolicitud(Integer boleta, Integer idProyecto) {

        Timestamp ahora = new Timestamp(System.currentTimeMillis());

        Solicitud s = Solicitud.builder()
                .fechaSolicitud(ahora)
                .estadoSolicitud("enviada")
                .boleta(boleta)
                .idProyecto(idProyecto)
                .build();

        solicitudRepository.save(s);

        return SolicitudCreateDto.builder()
                .boleta(boleta)
                .idProyecto(idProyecto)
                .estadoSolicitud("enviada")
                .fechaSolicitud(ahora.toString())
                .build();
    }
}

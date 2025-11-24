package com.SoLinX.service.impl;

import com.SoLinX.dto.SolicitudCreateDto;
import com.SoLinX.model.Estudiante; // Importamos Estudiante
import com.SoLinX.model.Proyecto;   // Importamos Proyecto
import com.SoLinX.model.Solicitud;
import com.SoLinX.repository.SolicitudRepository;
import com.SoLinX.service.SolicitudCreateService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date; // Usamos java.util.Date para la entidad Solicitud

@Service
@AllArgsConstructor
public class SolicitudCreateServiceImpl implements SolicitudCreateService {

    private final SolicitudRepository solicitudRepository;

    @Override
    public SolicitudCreateDto crearSolicitud(Integer boleta, Integer idProyecto) {

        Estudiante estudianteLink = Estudiante.builder()
                .boleta(boleta)
                .build();

        Proyecto proyectoLink = Proyecto.builder()
                .idProyecto(idProyecto)
                .build();

        Date ahora = new Date();

        Solicitud s = Solicitud.builder()
                .fechaSolicitud(ahora)
                .estadoSolicitud("enviada")
                .estudiante(estudianteLink)
                .proyecto(proyectoLink)
                .build();

        solicitudRepository.save(s);

        return SolicitudCreateDto.builder()
                .boleta(boleta)
                .idProyecto(idProyecto)
                .estadoSolicitud("enviada")

                .fechaSolicitud(new Timestamp(ahora.getTime()).toString())
                .build();
    }
}
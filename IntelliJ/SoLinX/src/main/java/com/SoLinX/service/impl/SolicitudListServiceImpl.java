package com.SoLinX.service.impl;

import com.SoLinX.dto.SolicitudListDto;
import com.SoLinX.model.Solicitud;
import com.SoLinX.repository.SolicitudRepository;
import com.SoLinX.service.SolicitudListService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SolicitudListServiceImpl implements SolicitudListService {

    private final SolicitudRepository solicitudRepository;

    public SolicitudListServiceImpl(SolicitudRepository solicitudRepository) {
        this.solicitudRepository = solicitudRepository;
    }

    @Override
    public List<SolicitudListDto> listarSolicitudesDeEmpresa(Integer idEmpresa) {

        List<Solicitud> solicitudes = solicitudRepository.findSolicitudesByEmpresa(idEmpresa);

        return solicitudes.stream()
                .map(s -> SolicitudListDto.builder()
                        .idSolicitud(s.getIdSolicitud())
                        .fechaSolicitud(
                                s.getFechaSolicitud() != null ? s.getFechaSolicitud().toString() : null
                        )
                        .estadoSolicitud(s.getEstadoSolicitud())
                        .boleta(s.getBoleta())
                        .idProyecto(s.getIdProyecto())
                        .build()
                ).collect(Collectors.toList());
    }
}

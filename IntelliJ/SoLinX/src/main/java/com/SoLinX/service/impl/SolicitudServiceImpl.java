package com.SoLinX.service.impl;

import com.SoLinX.dto.SolicitudDto;
import com.SoLinX.model.Solicitud;
import com.SoLinX.repository.SolicitudRepository;
import com.SoLinX.service.SolicitudService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class SolicitudServiceImpl implements SolicitudService {

    private final SolicitudRepository solicitudRepository;

    @Override
    public List<Solicitud> getAll() {
        return solicitudRepository.findAll();
    }

    @Override
    public Solicitud getById(Integer id) {
        return solicitudRepository.findById(id).orElse(null);
    }

    @Override
    public Solicitud save(Solicitud bSolicitud) {
        return solicitudRepository.save(bSolicitud);
    }

    @Override
    public void delete(Integer id) {
        solicitudRepository.deleteById(id);
    }

    @Override
    public Solicitud update(Integer id, Solicitud bSolicitud) {
        Solicitud aux = solicitudRepository.findById(id).orElse(null);

        if (aux == null) {
            return null;
        }
        aux.setFechaSolicitud(bSolicitud.getFechaSolicitud());
        aux.setEstadoSolicitud(bSolicitud.getEstadoSolicitud());
        aux.setEstudiante(bSolicitud.getEstudiante());
        aux.setProyecto(bSolicitud.getProyecto());

        return solicitudRepository.save(aux);
    }

    @Override
    public List<Solicitud> obtenerPorEmpresa(Integer idEmpresa) {
        return solicitudRepository.findSolicitudesByEmpresa(idEmpresa);
    }

    // 🆕 MÉTODO NUEVO
    @Override
    public List<SolicitudDto> obtenerSolicitudesPorBoleta(Integer boleta) {
        List<Solicitud> solicitudes = solicitudRepository.findByBoleta(boleta);

        return solicitudes.stream()
                .map(this::convertirASolicitudDto)
                .collect(Collectors.toList());
    }

    private SolicitudDto convertirASolicitudDto(Solicitud solicitud) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        return SolicitudDto.builder()
                .idSolicitud(solicitud.getIdSolicitud())
                .boleta(solicitud.getEstudiante() != null ?
                        solicitud.getEstudiante().getBoleta() : null)
                .nombreProyecto(solicitud.getProyecto() != null ?
                        solicitud.getProyecto().getNombreProyecto() : "N/A")
                .nombreEmpresa(solicitud.getProyecto() != null &&
                        solicitud.getProyecto().getEmpresa() != null ?
                        solicitud.getProyecto().getEmpresa().getNombreEmpresa() : "N/A")
                .estadoSolicitud(solicitud.getEstadoSolicitud())
                .fechaSolicitud(solicitud.getFechaSolicitud() != null ?
                        dateFormat.format(solicitud.getFechaSolicitud()) : "N/A")
                .idProyecto(solicitud.getProyecto() != null ?
                        solicitud.getProyecto().getIdProyecto() : null)
                .build();
    }
}
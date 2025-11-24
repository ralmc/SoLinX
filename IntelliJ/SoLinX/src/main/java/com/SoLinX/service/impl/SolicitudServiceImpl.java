package com.SoLinX.service.impl;

import com.SoLinX.model.Solicitud;
import com.SoLinX.repository.SolicitudRepository;
import com.SoLinX.service.SolicitudService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
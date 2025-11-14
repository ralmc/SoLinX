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
        // 1. Usar findById (forma moderna) en lugar de getById (obsoleto)
        Solicitud aux = solicitudRepository.findById(id).orElse(null);

        // 2. Verificar si la solicitud existe
        if (aux == null) {
            // No podemos actualizar algo que no existe
            return null;
        }

        // 3. ¡ESTA ES LA CORRECCIÓN!
        // Copiamos los datos de 'bSolicitud' (los datos nuevos) a 'aux' (la entidad existente)
        // NUNCA se debe tocar el ID (aux.setIdSolicitud(...))
        aux.setFechaSolicitud(bSolicitud.getFechaSolicitud());
        aux.setEstadoSolicitud(bSolicitud.getEstadoSolicitud());
        aux.setBoleta(bSolicitud.getBoleta());
        aux.setIdProyecto(bSolicitud.getIdProyecto());

        // 4. Guardamos la entidad 'aux' (que sigue teniendo su ID original)
        return solicitudRepository.save(aux);
    }
}
package com.SoLinX.service;

import com.SoLinX.dto.SolicitudDto;
import com.SoLinX.model.Solicitud;
import java.util.List;

public interface SolicitudService {
    List<Solicitud> getAll();
    Solicitud getById(Integer idSolicitud);
    Solicitud save(Solicitud bSolicitud);
    void delete(Integer idSolicitud);
    Solicitud update(Integer idSolicitud, Solicitud bSolicitud);
    List<Solicitud> obtenerPorEmpresa(Integer idEmpresa);

    // 🆕 AGREGAR ESTA LÍNEA
    List<SolicitudDto> obtenerSolicitudesPorBoleta(Integer boleta);
}
package com.SoLinX.service;

import com.SoLinX.dto.SolicitudCreateDto;

public interface SolicitudCreateService {
    SolicitudCreateDto crearSolicitud(Integer boleta, Integer idProyecto);
}

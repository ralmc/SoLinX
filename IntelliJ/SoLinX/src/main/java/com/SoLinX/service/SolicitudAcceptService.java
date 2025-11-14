package com.SoLinX.service;

import com.SoLinX.dto.SolicitudAcceptDto;
import com.SoLinX.model.Solicitud;

public interface SolicitudAcceptService {
    Solicitud aceptar(SolicitudAcceptDto dto);
}

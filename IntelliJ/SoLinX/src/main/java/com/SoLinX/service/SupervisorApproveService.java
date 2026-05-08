package com.SoLinX.service;

import com.SoLinX.dto.AprobacionResponseDto;
import com.SoLinX.dto.SolicitudesResponseDto;
import com.SoLinX.dto.SupervisorResponseDto;
import com.SoLinX.model.Supervisor;

import java.util.List;

public interface SupervisorApproveService {

    SupervisorResponseDto getSupervisorData(Integer idUsuario);
    SolicitudesResponseDto getSolicitudesEnviadas(Integer idSupervisor);
    SolicitudesResponseDto getSolicitudesAceptadas(Integer idEmpresa);
    AprobacionResponseDto actualizarSolicitud(Integer idSolicitud, String nuevoEstado);
}
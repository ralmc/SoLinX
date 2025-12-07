package com.SoLinX.service;

import com.SoLinX.dto.RegistroSupervisorDTO;
import com.SoLinX.dto.RegistroSupervisorResponseDTO;

public interface RegistroSupervisorService {
    RegistroSupervisorResponseDTO registrarSupervisor(RegistroSupervisorDTO dto);
}
package com.SoLinX.service;

import com.SoLinX.dto.RegistroEmpresaDTO;
import com.SoLinX.dto.RegistroEmpresaResponseDTO;

public interface RegistroEmpresaService {
    RegistroEmpresaResponseDTO registrarEmpresa(RegistroEmpresaDTO dto);
}
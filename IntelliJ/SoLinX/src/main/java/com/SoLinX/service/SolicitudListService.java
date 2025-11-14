package com.SoLinX.service;

import com.SoLinX.dto.SolicitudListDto;
import java.util.List;

public interface SolicitudListService {
    List<SolicitudListDto> listarSolicitudesDeEmpresa(Integer idEmpresa);
}

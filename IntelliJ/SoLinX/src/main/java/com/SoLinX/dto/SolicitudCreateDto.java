package com.SoLinX.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SolicitudCreateDto {
    private Integer boleta;
    private Integer idProyecto;
    private String estadoSolicitud;
    private String fechaSolicitud;
}

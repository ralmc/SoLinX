package com.SoLinX.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SolicitudDto {
    private int idSolicitud;
    private String fechaSolicitud;
    private String estadoSolicitud;
    private int boleta;
    private int idProyecto;
}
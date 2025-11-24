package com.SoLinX.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudDto {

    private Integer idSolicitud;
    private String fechaSolicitud;
    private String estadoSolicitud;
    private String nombreProyecto;
    private String carreraAlumno;
    private Integer boletaAlumno;
    private Integer idProyecto;
}
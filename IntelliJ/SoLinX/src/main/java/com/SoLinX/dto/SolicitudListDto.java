package com.SoLinX.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SolicitudListDto {

    private Integer idSolicitud;
    private String fechaSolicitud;
    private String nombreProyecto;
    private String carreraAlumno;
    private Integer boletaAlumno;
    private Integer idProyecto;
    private String estadoSolicitud;
}
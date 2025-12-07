package com.SoLinX.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudDto {

    private Integer idSolicitud;
    private String fechaSolicitud;
    private String estadoSolicitud;

    private Integer boleta;
    private Integer boletaAlumno;
    private String nombreEstudiante;
    private String carrera;
    private String carreraAlumno;
    private String escuela;

    // Campos del proyecto / empresa
    private Integer idProyecto;
    private String nombreProyecto;
    private String nombreEmpresa;
}

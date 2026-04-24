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
    private String fechaAceptacion;
    private String estadoSolicitud;

    private Integer boleta;
    private Integer boletaAlumno;
    private String nombreEstudiante;
    private String correoEstudiante;
    private String carrera;
    private String carreraAlumno;
    private String escuela;

    private Integer idProyecto;
    private String nombreProyecto;
    private String nombreEmpresa;
    private String correoEmpresa;
}
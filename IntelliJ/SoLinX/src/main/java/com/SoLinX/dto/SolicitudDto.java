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

    // Campos de alumno (combinados de ambas versiones)
    private Integer boleta;            // versión nueva
    private Integer boletaAlumno;      // versión vieja
    private String nombreEstudiante;   // nueva
    private String carrera;            // nueva
    private String carreraAlumno;      // vieja
    private String escuela;            // nueva

    // Campos del proyecto / empresa
    private Integer idProyecto;
    private String nombreProyecto;
    private String nombreEmpresa;      // nueva
}

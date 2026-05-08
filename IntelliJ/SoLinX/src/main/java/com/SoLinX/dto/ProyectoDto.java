package com.SoLinX.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProyectoDto {
    private Integer idProyecto;
    private String carreraEnfocada;
    private String nombreProyecto;
    private String imagenRef;
    private String imagenProyecto;
    private String nombreEmpresa;
    private String objetivo;
    private String estadoProyecto;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "America/Mexico_City")
    private Date fechaInicio;

    private Integer vacantes;
    private String ubicacion;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "America/Mexico_City")
    private Date fechaTermino;

    private String telefonoEmpresa;
    private String fotoEmpresa;
    private Integer idEmpresa;
}
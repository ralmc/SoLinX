package com.SoLinX.dto;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class ProyectoDto {
    private Integer idProyecto;
    private String nombreProyecto;
    private String objetivo;
    private Timestamp fechaInicio;
    private int vacantes;
    private String ubicacion;
    private String justificacion;
    private Timestamp fechaTermino;
    private int idEmpresa;
}
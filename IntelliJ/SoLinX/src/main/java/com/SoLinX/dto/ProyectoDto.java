package com.SoLinX.dto;

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
    private String nombreEmpresa;
    private String objetivo;
    private Date fechaInicio;
    private Integer vacantes;
    private String ubicacion;
    private Date fechaTermino;

    private Integer idEmpresa;

}
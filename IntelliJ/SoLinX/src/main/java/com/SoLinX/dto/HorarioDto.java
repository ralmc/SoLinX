package com.SoLinX.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HorarioDto {
    private Integer idHorario;
    private String lunInicio;
    private String lunFinal;
    private String marInicio;
    private String marFinal;
    private String mierInicio;
    private String mierFinal;
    private String jueInicio;
    private String jueFinal;
    private String vieInicio;
    private String vieFinal;
    private String sabInicio;
    private String sabFinal;
    private String domInicio;
    private String domFinal;
}
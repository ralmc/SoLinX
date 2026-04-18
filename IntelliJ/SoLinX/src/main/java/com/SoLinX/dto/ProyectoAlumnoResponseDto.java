package com.SoLinX.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProyectoAlumnoResponseDto {
    private boolean enProyecto;
    private ProyectoDto proyectoAsignado;
    private String correoEmpresa;
    private List<ProyectoDto> proyectos;
}

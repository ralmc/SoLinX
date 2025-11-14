package com.SoLinX.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EstudianteDto {
    private int boleta;
    private String carrera;
    private String escuela;
}

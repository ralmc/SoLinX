package com.SoLinX.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmpresaDto {
    private int idEmpresa;
    private String nombreEmpresa;
}
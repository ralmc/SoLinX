package com.SoLinX.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SupervisorDto {
    private int idSupervisor;
    private String area;
    private int idEmpresa;
}
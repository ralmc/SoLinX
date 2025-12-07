package com.SoLinX.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroSupervisorDTO {
    private String nombreSupervisor;
    private String area;
    private Integer idEmpresa;
    private String correo;
    private String telefono;
    private String userPassword;
}
package com.SoLinX.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroSupervisorResponseDTO {
    private Integer idUsuario;
    private String nombre;
    private String correo;
    private String rol;
    private Integer idSupervisor;
    private String area;
    private Integer idEmpresa;
}
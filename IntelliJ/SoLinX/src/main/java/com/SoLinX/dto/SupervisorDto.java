package com.SoLinX.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupervisorDto {
    private Integer idSupervisor;
    private Integer idUsuario;
    private String nombre;
    private String correo;
    private String telefono;
    private String area;
    private Integer idEmpresa;
    private String nombreEmpresa;
}
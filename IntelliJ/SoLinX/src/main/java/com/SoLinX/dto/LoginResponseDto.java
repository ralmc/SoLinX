package com.SoLinX.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {
    private Integer idUsuario;
    private String nombre;
    private String correo;
    private String rol;
    private String tipoUsuario;
    private Integer idEmpresa;
    private Integer boleta;
    private String carrera;
    private String escuela;
    private String telefono;
}
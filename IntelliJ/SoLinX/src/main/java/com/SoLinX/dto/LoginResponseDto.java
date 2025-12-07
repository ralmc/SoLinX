package com.SoLinX.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private Integer idUsuario;
    private String nombre;
    private String correo;
    private String rol;
    private String tipoUsuario;
    private Integer idEmpresa;

    // Datos estudiante
    private Integer boleta;
    private String carrera;
    private String escuela;
    private String telefono;
}
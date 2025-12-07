package com.SoLinX.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class RegistroDto {
    private String nombreUsuario;
    private Integer boleta;
    private String carrera;
    private String escuela;
    private String correo;
    private String confirmarCorreo;
    private String contraseña;
    private String confirmarContraseña;
}
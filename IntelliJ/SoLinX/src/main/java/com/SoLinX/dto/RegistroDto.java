package com.SoLinX.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
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

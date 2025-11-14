package com.SoLinX.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioDto {
    private int idUsuario;
    private String nombre;
    private String correo;
    private String telefono;
    private String userPassword;
    private String rol;
}

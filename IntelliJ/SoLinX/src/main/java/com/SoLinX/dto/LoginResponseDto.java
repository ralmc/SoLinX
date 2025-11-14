package com.SoLinX.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDto {
    private int idUsuario;
    private String nombre;
    private String correo;
    private String rol;
}

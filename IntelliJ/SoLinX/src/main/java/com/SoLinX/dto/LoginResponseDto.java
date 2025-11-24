package com.SoLinX.dto;

import lombok.AllArgsConstructor; // Recomendado
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor; // Recomendado

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

    // --- ESTE ES EL CAMPO QUE FALTABA ---
    private Integer idEmpresa;
}
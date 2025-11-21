package com.SoLinX.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroEmpresaResponseDTO {
    private Integer idUsuario;
    private String nombre;
    private String correo;
    private String rol;
    private Integer idEmpresa;
    private String nombreEmpresa;
}
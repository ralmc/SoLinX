package com.SoLinX.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroEmpresaDTO {
    private String nombreEmpresa;
    private String correo;
    private String telefono;
    private String userPassword;
}

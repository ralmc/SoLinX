package com.SoLinX.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioEstudianteDto {
    private Integer idUsuario;
    private Integer boleta;
}

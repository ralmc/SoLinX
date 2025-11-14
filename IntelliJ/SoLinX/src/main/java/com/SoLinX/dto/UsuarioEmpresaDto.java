package com.SoLinX.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioEmpresaDto {
    private Integer idUsuario;
    private Integer idEmpresa;
}

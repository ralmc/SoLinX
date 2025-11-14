package com.SoLinX.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioSupervisorDto {
    private Integer idUsuario;
    private Integer idSupervisor;
}

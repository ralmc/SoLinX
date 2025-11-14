package com.SoLinX.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PerfilUpdateDto {
    private Integer idPerfil;
    private String foto;
    private String tema;
}

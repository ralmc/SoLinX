package com.SoLinX.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PerfilDto {
    private int idPerfil;
    private String foto; //Cambiar por Byte[]
    private String tema;
    private int idUsuario;
}

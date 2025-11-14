package com.SoLinX.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class SolicitudAcceptDto {

    private Integer idSolicitud;
    private boolean aceptado;
}

package com.SoLinX.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudAcceptDto {
    private Integer idSolicitud;
    private Boolean aceptado;
}

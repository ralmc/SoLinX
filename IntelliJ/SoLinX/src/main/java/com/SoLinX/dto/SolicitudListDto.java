package com.SoLinX.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SolicitudListDto {

    private Integer idSolicitud;
    private String fechaSolicitud;
    private String estadoSolicitud;
    private Integer boleta;
    private Integer idProyecto;
}

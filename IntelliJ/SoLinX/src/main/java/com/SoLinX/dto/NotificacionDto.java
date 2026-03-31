package com.SoLinX.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDto {
    private Integer idNotificacion;
    private String titulo;
    private String mensaje;
    private String fechaCreacion;
    private Boolean leida;
    private Integer idUsuario;
}

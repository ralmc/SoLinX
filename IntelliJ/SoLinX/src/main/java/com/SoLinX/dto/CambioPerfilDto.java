package com.SoLinX.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CambioPerfilDto {
    private Integer idCambio;
    private Integer idUsuario;
    private String  nombreUsuario;
    private String  rol;
    private String  campo;
    private String  valorAnterior;
    private String  valorNuevo;
    private String  estado;
    private String  fechaSolicitud;
    private String  fechaResolucion;
}
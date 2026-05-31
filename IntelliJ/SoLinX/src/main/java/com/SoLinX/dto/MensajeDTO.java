package com.SoLinX.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensajeDTO {
    private String rol;
    private String contenido;
}

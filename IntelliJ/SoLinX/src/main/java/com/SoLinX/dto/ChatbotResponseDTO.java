package com.SoLinX.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotResponseDTO {
    private String respuesta;
    private boolean exito;
    private String error;
}

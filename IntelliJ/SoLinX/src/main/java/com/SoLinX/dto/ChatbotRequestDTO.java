package com.SoLinX.dto;

import lombok.Data;
import java.util.List;


@Data
public class ChatbotRequestDTO {
    private List<MensajeDTO> historial;
    private String nuevoMensaje;
}

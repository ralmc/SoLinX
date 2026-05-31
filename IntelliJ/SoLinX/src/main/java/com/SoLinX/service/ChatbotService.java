package com.SoLinX.service;

import com.SoLinX.dto.ChatbotResponseDTO;
import com.SoLinX.dto.MensajeDTO;

import java.util.List;

public interface ChatbotService {
    ChatbotResponseDTO enviarMensaje(List<MensajeDTO> historial, String nuevoMensaje);
}

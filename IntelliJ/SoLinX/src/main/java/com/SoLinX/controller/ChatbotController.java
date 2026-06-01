package com.SoLinX.controller;

import com.SoLinX.dto.ChatbotRequestDTO;
import com.SoLinX.dto.ChatbotResponseDTO;
import com.SoLinX.service.ChatbotService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/SoLinX/api")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class ChatbotController {

    private final ChatbotService chatbotService;

    /**
     * Recibe el historial + nuevo mensaje y devuelve la respuesta del bot.
     * El frontend mantiene el historial en memoria/sesión.
     */
    @PostMapping("/chatbot/mensaje")
    public ResponseEntity<ChatbotResponseDTO> enviarMensaje(@RequestBody ChatbotRequestDTO request) {
        if (request.getNuevoMensaje() == null || request.getNuevoMensaje().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    new ChatbotResponseDTO(null, false, "El mensaje no puede estar vacío")
            );
        }

        ChatbotResponseDTO response = chatbotService.enviarMensaje(
                request.getHistorial(),
                request.getNuevoMensaje()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de saludo inicial — útil para mostrar al abrir el chatbot.
     */
    @GetMapping("/chatbot/saludo")
    public ResponseEntity<ChatbotResponseDTO> saludo() {
        String saludo = "¡Hola! 👋 Soy SoliBot, tu asistente para dudas sobre " +
                "el servicio social en SoLinX. ¿En qué te puedo ayudar?";
        return ResponseEntity.ok(new ChatbotResponseDTO(saludo, true, null));
    }
}

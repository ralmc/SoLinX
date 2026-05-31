package com.SoLinX.service.impl;

import com.SoLinX.dto.ChatbotResponseDTO;
import com.SoLinX.dto.MensajeDTO;
import com.SoLinX.service.ChatbotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ChatbotServiceImpl implements ChatbotService {

    @Value("${anthropic.api.key}")
    private String apiKey;

    @Value("${anthropic.api.url}")
    private String apiUrl;

    @Value("${anthropic.api.model}")
    private String model;

    @Value("${anthropic.api.max-tokens}")
    private int maxTokens;

    private static final String SYSTEM_PROMPT = """
            Eres "SoliBot", el asistente virtual de SoLinX, la plataforma del
            Instituto Politécnico Nacional (IPN) para gestionar el servicio social
            y las prácticas profesionales de los alumnos.

            Tu rol es resolver dudas sobre:
            - Requisitos del servicio social en el IPN (480 horas mínimo,
              haber cubierto el 70% de créditos, duración mínima de 6 meses, etc.)
            - Documentos necesarios (carta de aceptación, carta de presentación,
              reportes bimestrales, carta de término)
            - Proceso dentro de SoLinX: cómo postularse a una empresa,
              cómo subir documentos por periodos, cómo contactar a su supervisor
            - Diferencias entre servicio social y prácticas profesionales
            - Plazos y tiempos típicos del trámite

            Reglas:
            1. Responde SIEMPRE en español, de forma breve y clara (máximo 4-5 líneas).
            2. Si te preguntan algo que NO tiene que ver con servicio social,
               prácticas profesionales o SoLinX, redirige amablemente al tema.
            3. Si no estás seguro de una respuesta específica del IPN, sugiere
               consultar con su supervisor o con la Coordinación de Servicio Social
               de su escuela (ESCOM, ESIME, UPIICSA, etc.).
            4. Tono amigable pero profesional. Puedes usar emojis con moderación.
            5. NUNCA inventes datos específicos como números de oficinas, teléfonos
               o nombres de funcionarios.
            6. Si el alumno tiene un problema técnico con la plataforma, sugiérele
               contactar a su supervisor o reportarlo en SoLinX.
            """;

    private final WebClient webClient = WebClient.builder()
            .codecs(c -> c.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
            .build();

    @Override
    public ChatbotResponseDTO enviarMensaje(List<MensajeDTO> historial, String nuevoMensaje) {
        try {
            // Construir lista de mensajes para la API
            List<Map<String, String>> messages = new ArrayList<>();

            if (historial != null) {
                for (MensajeDTO m : historial) {
                    Map<String, String> msg = new HashMap<>();
                    msg.put("role", m.getRol());
                    msg.put("content", m.getContenido());
                    messages.add(msg);
                }
            }

            // Agregar el nuevo mensaje del usuario
            Map<String, String> nuevo = new HashMap<>();
            nuevo.put("role", "user");
            nuevo.put("content", nuevoMensaje);
            messages.add(nuevo);

            // Body del request a Anthropic
            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("max_tokens", maxTokens);
            body.put("system", SYSTEM_PROMPT);
            body.put("messages", messages);

            log.info("Enviando mensaje a Claude API. Historial: {} mensajes", messages.size());

            // Llamada a la API
            @SuppressWarnings("unchecked")
            Map<String, Object> response = webClient.post()
                    .uri(apiUrl)
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null) {
                return new ChatbotResponseDTO(null, false, "Respuesta vacía de la API");
            }

            // Extraer texto de la respuesta
            // Estructura: { content: [ { type: "text", text: "..." } ] }
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");

            if (content == null || content.isEmpty()) {
                return new ChatbotResponseDTO(null, false, "Sin contenido en la respuesta");
            }

            String textoRespuesta = (String) content.get(0).get("text");
            log.info("Respuesta recibida de Claude: {} caracteres", textoRespuesta.length());

            return new ChatbotResponseDTO(textoRespuesta, true, null);

        } catch (Exception e) {
            log.error("Error al llamar a la API de Anthropic", e);
            return new ChatbotResponseDTO(
                    null,
                    false,
                    "No pude conectarme al asistente. Intenta de nuevo en un momento."
            );
        }
    }
}

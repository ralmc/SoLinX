package com.example.solinx.DTO;

import java.util.List;

public class ChatbotRequest {
    private List<MensajeChatbot> historial;
    private String nuevoMensaje;

    public ChatbotRequest(List<MensajeChatbot> historial, String nuevoMensaje) {
        this.historial = historial;
        this.nuevoMensaje = nuevoMensaje;
    }

    public List<MensajeChatbot> getHistorial() { return historial; }
    public void setHistorial(List<MensajeChatbot> historial) { this.historial = historial; }

    public String getNuevoMensaje() { return nuevoMensaje; }
    public void setNuevoMensaje(String nuevoMensaje) { this.nuevoMensaje = nuevoMensaje; }
}

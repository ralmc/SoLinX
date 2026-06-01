package com.example.solinx.DTO;

public class ChatbotResponse {
    private String respuesta;
    private boolean exito;
    private String error;

    public String getRespuesta() { return respuesta; }
    public void setRespuesta(String respuesta) { this.respuesta = respuesta; }

    public boolean isExito() { return exito; }
    public void setExito(boolean exito) { this.exito = exito; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}

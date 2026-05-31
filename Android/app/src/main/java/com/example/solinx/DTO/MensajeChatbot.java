package com.example.solinx.DTO;

public class MensajeChatbot {
    private String rol;
    private String contenido;

    public MensajeChatbot() {}

    public MensajeChatbot(String rol, String contenido) {
        this.rol = rol;
        this.contenido = contenido;
    }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public boolean esUsuario() {
        return "user".equals(rol);
    }
}

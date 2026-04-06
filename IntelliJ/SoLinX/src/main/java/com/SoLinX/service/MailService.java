package com.SoLinX.service;

public interface MailService {
    void enviarCorreoVerificacion(String destinatario, String nombre, String token);
}
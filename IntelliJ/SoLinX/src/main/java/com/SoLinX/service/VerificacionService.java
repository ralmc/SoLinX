package com.SoLinX.service;

import com.SoLinX.model.Usuario;

public interface VerificacionService {
    String verificarToken(String token);
    void generarYEnviarToken(Usuario usuario);
}
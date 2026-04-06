package com.SoLinX.service.impl;

import com.SoLinX.dto.PasswordUpdateDto;
import com.SoLinX.model.Usuario;
import com.SoLinX.repository.UsuarioRepository;
import com.SoLinX.service.PasswordUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordUpdateServiceImpl implements PasswordUpdateService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public String actualizarPassword(PasswordUpdateDto dto) {
        Usuario usuario = usuarioRepository.findByCorreo(dto.getCorreo().trim())
                .orElse(null);

        if (usuario == null) {
            log.warn("Cambio de password — correo no existe: {}", dto.getCorreo());
            return "UsuarioNoExiste";
        }

        if (!usuario.getUserPassword().equals(dto.getOldPassword())) {
            log.warn("Cambio de password fallido para: {}", dto.getCorreo());
            return "PasswordIncorrecta";
        }

        usuario.setUserPassword(dto.getNewPassword());
        usuarioRepository.save(usuario);
        log.info("Password actualizado para: {}", dto.getCorreo());
        return "OK";
    }
}
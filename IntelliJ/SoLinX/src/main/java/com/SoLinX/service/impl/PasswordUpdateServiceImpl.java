package com.SoLinX.service.impl;

import com.SoLinX.dto.PasswordUpdateDto;
import com.SoLinX.model.Usuario;
import com.SoLinX.repository.UsuarioRepository;
import com.SoLinX.service.PasswordUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordUpdateServiceImpl implements PasswordUpdateService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public String actualizarPassword(PasswordUpdateDto dto) {
        Usuario usuario = usuarioRepository.findByCorreo(dto.getCorreo().trim())
                .orElse(null);

        if (usuario == null) {
            log.warn("Cambio de password — correo no existe: {}", dto.getCorreo());
            return "UsuarioNoExiste";
        }

        if (!passwordEncoder.matches(dto.getOldPassword(), usuario.getUserPassword())) {
            log.warn("Cambio de password fallido para: {}", dto.getCorreo());
            return "PasswordIncorrecta";
        }

        usuario.setUserPassword(passwordEncoder.encode(dto.getNewPassword()));
        usuarioRepository.save(usuario);
        log.info("Password actualizado para: {}", dto.getCorreo());
        return "OK";
    }
}
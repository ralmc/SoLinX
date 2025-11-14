package com.SoLinX.service.impl;

import com.SoLinX.dto.PasswordUpdateDto;
import com.SoLinX.model.Usuario;
import com.SoLinX.repository.UsuarioRepository;
import com.SoLinX.service.PasswordUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordUpdateServiceImpl implements PasswordUpdateService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public String actualizarPassword(PasswordUpdateDto dto) {

        Usuario usuario = usuarioRepository.findByCorreo(dto.getCorreo());

        if (usuario == null) {
            return "UsuarioNoExiste";
        }

        if (!usuario.getUserPassword().equals(dto.getOldPassword())) {
            return "PasswordIncorrecta";
        }

        usuario.setUserPassword(dto.getNewPassword());
        usuarioRepository.save(usuario);

        return "OK";
    }
}

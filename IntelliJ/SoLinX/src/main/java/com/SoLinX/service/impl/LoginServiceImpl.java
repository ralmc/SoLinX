package com.SoLinX.service.impl;

import com.SoLinX.dto.LoginDto;
import com.SoLinX.dto.LoginResponseDto;
import com.SoLinX.model.Usuario;
import com.SoLinX.repository.UsuarioRepository;
import com.SoLinX.service.LoginService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public LoginResponseDto login(LoginDto loginDto) {

        Usuario u = usuarioRepository
                .findAll()
                .stream()
                .filter(x -> x.getCorreo().equals(loginDto.getCorreo())
                        && x.getUserPassword().equals(loginDto.getUserPassword()))
                .findFirst()
                .orElse(null);

        if (u == null) return null;

        return LoginResponseDto.builder()
                .idUsuario(u.getIdUsuario())
                .nombre(u.getNombre())
                .correo(u.getCorreo())
                .rol(u.getRol())
                .build();
    }
}

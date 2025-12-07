package com.SoLinX.service.impl;

import com.SoLinX.dto.LoginDto;
import com.SoLinX.dto.LoginResponseDto;
import com.SoLinX.model.Usuario;
import com.SoLinX.model.UsuarioEstudiante;
import com.SoLinX.repository.UsuarioRepository;
import com.SoLinX.repository.UsuarioEstudianteRepository;
import com.SoLinX.service.LoginService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class LoginServiceImpl implements LoginService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioEstudianteRepository usuarioEstudianteRepository;

    @Override
    public LoginResponseDto login(LoginDto loginDto) {
        String identificador = loginDto.getCorreo().trim();
        String password = loginDto.getUserPassword();

        try {
            List<Usuario> todosUsuarios = usuarioRepository.findAll();

            Usuario usuario = null;
            for (Usuario u : todosUsuarios) {
                if (u.getCorreo().equals(identificador) && u.getUserPassword().equals(password)) {
                    usuario = u;
                    break;
                }
            }

            if (usuario != null) {
                UsuarioEstudiante usuarioEstudiante = usuarioEstudianteRepository.findByIdUsuario(usuario.getIdUsuario());

                if (usuarioEstudiante != null) {
                    return buildResponseFromUsuario(usuario, "ESTUDIANTE");
                }

                return buildResponseFromUsuario(usuario, "USUARIO");
            }

            return null;

        } catch (Exception e) {
            return null;
        }
    }

    private LoginResponseDto buildResponseFromUsuario(Usuario usuario, String tipoUsuario) {
        return LoginResponseDto.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .rol(usuario.getRol())
                .tipoUsuario(tipoUsuario)
                .build();
    }
}
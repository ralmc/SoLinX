package com.SoLinX.service.impl;

import com.SoLinX.dto.LoginDto;
import com.SoLinX.dto.LoginResponseDto;
import com.SoLinX.model.Estudiante;
import com.SoLinX.model.Usuario;
import com.SoLinX.model.UsuarioEstudiante;
import com.SoLinX.model.UsuarioEmpresa;
import com.SoLinX.repository.EstudianteRepository;
import com.SoLinX.repository.UsuarioRepository;
import com.SoLinX.repository.UsuarioEstudianteRepository;
import com.SoLinX.repository.UsuarioEmpresaRepository;
import com.SoLinX.service.LoginService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LoginServiceImpl implements LoginService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioEstudianteRepository usuarioEstudianteRepository;
    private final UsuarioEmpresaRepository usuarioEmpresaRepository; // ← AGREGADO
    private final EstudianteRepository estudianteRepository;

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
                if ("empresa".equals(usuario.getRol())) {
                    UsuarioEmpresa ue = usuarioEmpresaRepository.findByIdUsuario(usuario.getIdUsuario());
                    return buildResponseFromEmpresa(usuario, ue);
                }

                UsuarioEstudiante usuarioEstudiante = usuarioEstudianteRepository.findByIdUsuario(usuario.getIdUsuario());
                if (usuarioEstudiante != null) {
                    return buildResponseFromEstudiante(usuario, usuarioEstudiante);
                }

                // OTROS ROLES (supervisor, admin)
                return buildResponseFromUsuario(usuario, usuario.getRol().toUpperCase());
            }

            return null;

        } catch (Exception e) {
            return null;
        }
    }

    private LoginResponseDto buildResponseFromEmpresa(Usuario usuario, UsuarioEmpresa ue) {
        return LoginResponseDto.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .rol(usuario.getRol())
                .tipoUsuario("EMPRESA")
                .idEmpresa(ue != null ? ue.getIdEmpresa() : null)
                .telefono(usuario.getTelefono())
                .build();
    }

    private LoginResponseDto buildResponseFromEstudiante(Usuario usuario, UsuarioEstudiante usuarioEstudiante) {
        LoginResponseDto.LoginResponseDtoBuilder builder = LoginResponseDto.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .rol(usuario.getRol())
                .tipoUsuario("ESTUDIANTE")
                .telefono(usuario.getTelefono());

        Integer boleta = usuarioEstudiante.getBoleta();
        if (boleta != null) {
            builder.boleta(boleta);

            Optional<Estudiante> estudianteOpt = estudianteRepository.findById(boleta);
            if (estudianteOpt.isPresent()) {
                Estudiante estudiante = estudianteOpt.get();
                builder.carrera(estudiante.getCarrera())
                        .escuela(estudiante.getEscuela());
            }
        }

        return builder.build();
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
package com.SoLinX.service.impl;

import com.SoLinX.dto.LoginDto;
import com.SoLinX.dto.LoginResponseDto;
import com.SoLinX.model.*;
import com.SoLinX.repository.*;
import com.SoLinX.service.LoginService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioEstudianteRepository usuarioEstudianteRepository;
    private final UsuarioEmpresaRepository usuarioEmpresaRepository;
    private final EstudianteRepository estudianteRepository;
    private final UsuarioSupervisorRepository usuarioSupervisorRepository;
    private final SupervisorRepository supervisorRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public LoginResponseDto login(LoginDto loginDto) {

        String identificador = loginDto.getCorreo().trim();
        String password = loginDto.getUserPassword();

        try {
            Usuario usuario = usuarioRepository.findByCorreo(identificador);

            // Protección contra timing attacks
            String fakeHash = "$2a$10$abcdefghijklmnopqrstuv";
            boolean coincide = false;

            if (usuario != null) {
                coincide = passwordEncoder.matches(password, usuario.getUserPassword());
            } else {
                passwordEncoder.matches(password, fakeHash);
            }

            if (!coincide) {
                System.err.println("❌ LOGIN FALLIDO - Credenciales incorrectas");
                throw new RuntimeException("Credenciales incorrectas");
            }

            System.out.println("============================================");
            System.out.println("LOGIN EXITOSO");
            System.out.println("ID Usuario: " + usuario.getIdUsuario());
            System.out.println("============================================");

            // EMPRESA
            if ("empresa".equalsIgnoreCase(usuario.getRol())) {
                UsuarioEmpresa ue = usuarioEmpresaRepository.findByIdUsuario(usuario.getIdUsuario());
                return buildResponseFromEmpresa(usuario, ue);
            }

            // SUPERVISOR
            if ("supervisor".equalsIgnoreCase(usuario.getRol())) {
                UsuarioSupervisor us = usuarioSupervisorRepository.findByIdUsuario(usuario.getIdUsuario());
                return buildResponseFromSupervisor(usuario, us);
            }

            // ESTUDIANTE
            UsuarioEstudiante usuarioEstudiante = usuarioEstudianteRepository.findByIdUsuario(usuario.getIdUsuario());
            if (usuarioEstudiante != null) {
                return buildResponseFromEstudiante(usuario, usuarioEstudiante);
            }

            // OTROS ROLES
            return buildResponseFromUsuario(usuario, usuario.getRol().toUpperCase());

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("❌ ERROR EN LOGIN: " + e.getMessage());
            throw new RuntimeException("Error interno en el login");
        }
    }

    private LoginResponseDto buildResponseFromEmpresa(Usuario usuario, UsuarioEmpresa ue) {
        System.out.println("Construyendo respuesta para EMPRESA");

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

    private LoginResponseDto buildResponseFromSupervisor(Usuario usuario, UsuarioSupervisor us) {
        System.out.println("Construyendo respuesta para SUPERVISOR");

        LoginResponseDto.LoginResponseDtoBuilder builder = LoginResponseDto.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .rol(usuario.getRol())
                .tipoUsuario("SUPERVISOR")
                .telefono(usuario.getTelefono());

        if (us != null && us.getIdSupervisor() != null) {
            builder.idSupervisor(us.getIdSupervisor());

            Optional<Supervisor> supervisorOpt = supervisorRepository.findById(us.getIdSupervisor());
            if (supervisorOpt.isPresent()) {
                Supervisor supervisor = supervisorOpt.get();

                if (supervisor.getEmpresa() != null) {
                    builder.idEmpresa(supervisor.getEmpresa().getIdEmpresa());
                }

                builder.area(supervisor.getArea());
            }
        }

        return builder.build();
    }

    private LoginResponseDto buildResponseFromEstudiante(Usuario usuario, UsuarioEstudiante usuarioEstudiante) {
        System.out.println("Construyendo respuesta para ESTUDIANTE");

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
        System.out.println("Construyendo respuesta para ROL GENÉRICO: " + tipoUsuario);

        return LoginResponseDto.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .rol(usuario.getRol())
                .tipoUsuario(tipoUsuario)
                .telefono(usuario.getTelefono())
                .build();
    }
}
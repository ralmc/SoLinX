package com.SoLinX.service.impl;

import com.SoLinX.dto.LoginDto;
import com.SoLinX.dto.LoginResponseDto;
import com.SoLinX.model.*;
import com.SoLinX.repository.*;
import com.SoLinX.service.LoginService;
import com.SoLinX.service.VerificacionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioEstudianteRepository usuarioEstudianteRepository;
    private final UsuarioEmpresaRepository usuarioEmpresaRepository;
    private final UsuarioSupervisorRepository usuarioSupervisorRepository;
    private final EstudianteRepository estudianteRepository;
    private final SupervisorRepository supervisorRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final VerificacionService verificacionService;

    @Override
    public LoginResponseDto login(LoginDto loginDto) {
        String correo   = loginDto.getCorreo().trim();
        String password = loginDto.getUserPassword();

        Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);

        if (usuario == null) {
            log.warn("Login fallido — usuario no existe: {}", correo);
            return null;
        }

        if (!passwordEncoder.matches(password, usuario.getUserPassword())) {
            log.warn("Login fallido — password incorrecto: {}", correo);
            return null;
        }

        if (Boolean.FALSE.equals(usuario.getVerificado())) {
            log.warn("Usuario no verificado: {} — generando/reutilizando token", correo);
            verificacionService.generarYEnviarToken(usuario);
            return null;
        }

        log.info("Login exitoso — usuario: {} | rol: {}", usuario.getNombre(), usuario.getRol());
        return buildResponse(usuario);
    }

    private LoginResponseDto buildResponse(Usuario u) {
        LoginResponseDto.LoginResponseDtoBuilder builder = LoginResponseDto.builder()
                .idUsuario(u.getIdUsuario())
                .nombre(u.getNombre())
                .correo(u.getCorreo())
                .telefono(u.getTelefono())
                .rol(u.getRol())
                .tipoUsuario(u.getRol().toUpperCase());

        switch (u.getRol().toLowerCase()) {
            case "empresa"    -> enrichEmpresa(builder, u.getIdUsuario());
            case "supervisor" -> enrichSupervisor(builder, u.getIdUsuario());
            case "estudiante" -> enrichEstudiante(builder, u.getIdUsuario());
            default           -> log.warn("Rol genérico: {}", u.getRol());
        }
        return builder.build();
    }

    private void enrichEmpresa(LoginResponseDto.LoginResponseDtoBuilder b, Integer idUsuario) {
        UsuarioEmpresa ue = usuarioEmpresaRepository.findByIdUsuario(idUsuario);
        if (ue != null) b.idEmpresa(ue.getIdEmpresa());
        else log.warn("No se encontró UsuarioEmpresa para idUsuario: {}", idUsuario);
    }

    private void enrichSupervisor(LoginResponseDto.LoginResponseDtoBuilder b, Integer idUsuario) {
        UsuarioSupervisor us = usuarioSupervisorRepository.findByIdUsuario(idUsuario);
        if (us == null || us.getIdSupervisor() == null) {
            log.error("UsuarioSupervisor no encontrado para idUsuario: {}", idUsuario);
            return;
        }
        b.idSupervisor(us.getIdSupervisor());
        supervisorRepository.findById(us.getIdSupervisor()).ifPresentOrElse(
                supervisor -> {
                    b.area(supervisor.getArea());
                    if (supervisor.getEmpresa() != null)
                        b.idEmpresa(supervisor.getEmpresa().getIdEmpresa());
                },
                () -> log.error("Supervisor no encontrado con id: {}", us.getIdSupervisor())
        );
    }

    private void enrichEstudiante(LoginResponseDto.LoginResponseDtoBuilder b, Integer idUsuario) {
        UsuarioEstudiante ue = usuarioEstudianteRepository.findByIdUsuario(idUsuario).orElse(null);
        if (ue == null || ue.getBoleta() == null) {
            log.warn("UsuarioEstudiante no encontrado para idUsuario: {}", idUsuario);
            return;
        }
        b.boleta(ue.getBoleta());
        estudianteRepository.findById(ue.getBoleta()).ifPresent(e ->
                b.carrera(e.getCarrera()).escuela(e.getEscuela()));
    }
}
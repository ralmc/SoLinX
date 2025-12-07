package com.SoLinX.service.impl;

import com.SoLinX.dto.LoginDto;
import com.SoLinX.dto.LoginResponseDto;
import com.SoLinX.model.Estudiante;
import com.SoLinX.model.Supervisor;
import com.SoLinX.model.Usuario;
import com.SoLinX.model.UsuarioEstudiante;
import com.SoLinX.model.UsuarioEmpresa;
import com.SoLinX.model.UsuarioSupervisor;
import com.SoLinX.repository.EstudianteRepository;
import com.SoLinX.repository.SupervisorRepository;
import com.SoLinX.repository.UsuarioRepository;
import com.SoLinX.repository.UsuarioEstudianteRepository;
import com.SoLinX.repository.UsuarioEmpresaRepository;
import com.SoLinX.repository.UsuarioSupervisorRepository;
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
    private final UsuarioEmpresaRepository usuarioEmpresaRepository;
    private final EstudianteRepository estudianteRepository;
    private final UsuarioSupervisorRepository usuarioSupervisorRepository;
    private final SupervisorRepository supervisorRepository;

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
                System.out.println("============================================");
                System.out.println("LOGIN EXITOSO");
                System.out.println("Usuario: " + usuario.getNombre());
                System.out.println("Rol: " + usuario.getRol());
                System.out.println("============================================");

                // EMPRESA
                if ("empresa".equals(usuario.getRol())) {
                    UsuarioEmpresa ue = usuarioEmpresaRepository.findByIdUsuario(usuario.getIdUsuario());
                    return buildResponseFromEmpresa(usuario, ue);
                }

                // SUPERVISOR
                if ("supervisor".equals(usuario.getRol())) {
                    UsuarioSupervisor us = usuarioSupervisorRepository.findByIdUsuario(usuario.getIdUsuario());
                    return buildResponseFromSupervisor(usuario, us);
                }

                // ESTUDIANTE
                UsuarioEstudiante usuarioEstudiante = usuarioEstudianteRepository.findByIdUsuario(usuario.getIdUsuario());
                if (usuarioEstudiante != null) {
                    return buildResponseFromEstudiante(usuario, usuarioEstudiante);
                }

                // OTROS ROLES (admin)
                return buildResponseFromUsuario(usuario, usuario.getRol().toUpperCase());
            }

            System.err.println("❌ LOGIN FALLIDO - Credenciales incorrectas");
            return null;

        } catch (Exception e) {
            System.err.println("❌ ERROR EN LOGIN: " + e.getMessage());
            e.printStackTrace();
            return null;
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
        System.out.println("Usuario ID: " + usuario.getIdUsuario());
        System.out.println("Nombre: " + usuario.getNombre());

        LoginResponseDto.LoginResponseDtoBuilder builder = LoginResponseDto.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .rol(usuario.getRol())
                .tipoUsuario("SUPERVISOR")
                .telefono(usuario.getTelefono());

        if (us != null && us.getIdSupervisor() != null) {
            System.out.println("UsuarioSupervisor encontrado - ID: " + us.getIdSupervisor());
            builder.idSupervisor(us.getIdSupervisor());

            Optional<Supervisor> supervisorOpt = supervisorRepository.findById(us.getIdSupervisor());
            if (supervisorOpt.isPresent()) {
                Supervisor supervisor = supervisorOpt.get();
                System.out.println("Supervisor encontrado");
                System.out.println("   Area: " + supervisor.getArea());

                if (supervisor.getEmpresa() != null) {
                    Integer idEmpresa = supervisor.getEmpresa().getIdEmpresa();
                    System.out.println("   idEmpresa: " + idEmpresa);
                    builder.idEmpresa(idEmpresa);
                } else {
                    System.err.println("WARNING: Supervisor sin empresa asociada");
                }

                builder.area(supervisor.getArea());
            } else {
                System.err.println("❌ ERROR: No se encontró el Supervisor con ID: " + us.getIdSupervisor());
            }
        } else {
            System.err.println("❌ ERROR: UsuarioSupervisor es null o no tiene idSupervisor");
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
                .build();
    }
}
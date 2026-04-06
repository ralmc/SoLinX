package com.SoLinX.service.impl;

import com.SoLinX.dto.RegistroDto;
import com.SoLinX.model.Estudiante;
import com.SoLinX.model.Usuario;
import com.SoLinX.model.UsuarioEstudiante;
import com.SoLinX.repository.EstudianteRepository;
import com.SoLinX.repository.UsuarioEstudianteRepository;
import com.SoLinX.repository.UsuarioRepository;
import com.SoLinX.service.RegistroService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
@AllArgsConstructor
public class RegistroServiceImpl implements RegistroService {

    private final UsuarioRepository           usuarioRepository;
    private final EstudianteRepository        estudianteRepository;
    private final UsuarioEstudianteRepository usuarioEstudianteRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private boolean esPasswordSeguro(String password) {
        if (password == null) return false;

        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*\\d.*") &&
                password.matches(".*[@$!%*?&].*");
    }

    public String registrar(RegistroDto dto) {
        if (!dto.getCorreo().equals(dto.getConfirmarCorreo()))
            return "Los correos no coinciden.";
        if (!dto.getContraseña().equals(dto.getConfirmarContraseña()))
            return "Las contraseñas no coinciden.";
        if (usuarioRepository.findByCorreo(dto.getCorreo()).isPresent())
            return "El correo ya está registrado.";

        Estudiante estudianteExistente = estudianteRepository.findById(dto.getBoleta()).orElse(null);
        if (estudianteExistente != null) {
            if (usuarioEstudianteRepository.findByBoleta(dto.getBoleta()).isPresent())
                return "La boleta ya está registrada.";
        }

        Estudiante est = estudianteExistente != null ? estudianteExistente :
                estudianteRepository.save(Estudiante.builder()
                        .boleta(dto.getBoleta())
                        .carrera(dto.getCarrera())
                        .escuela(dto.getEscuela())
                        .build());

        Usuario u = usuarioRepository.save(Usuario.builder()
                .nombre(dto.getNombreUsuario())
                .correo(dto.getCorreo())
                .userPassword(dto.getContraseña())
                .rol("estudiante")
                .build());

        usuarioEstudianteRepository.save(UsuarioEstudiante.builder()
                .idUsuario(u.getIdUsuario())
                .boleta(est.getBoleta())
                .build());

        return "Registro exitoso";
    }

}
package com.SoLinX.service.impl;

import com.SoLinX.dto.RegistroDto;
import com.SoLinX.model.*;
import com.SoLinX.repository.*;
import com.SoLinX.service.RegistroService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegistroServiceImpl implements RegistroService {

    private final UsuarioRepository usuarioRepository;
    private final EstudianteRepository estudianteRepository;
    private final UsuarioEstudianteRepository usuarioEstudianteRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final PerfilRepository perfilRepository;

    private boolean esPasswordSeguro(String password) {
        if (password == null) return false;
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*\\d.*") &&
                password.matches(".*[@$!%*?&].*");
    }

    @Override
    public String registrar(RegistroDto dto) {

        if (!dto.getCorreo().equals(dto.getConfirmarCorreo()))
            return "Los correos no coinciden.";

        if (!dto.getContraseña().equals(dto.getConfirmarContraseña()))
            return "Las contraseñas no coinciden.";

        if (!esPasswordSeguro(dto.getContraseña()))
            return "Contraseña insegura.";

        if (usuarioRepository.findByCorreo(dto.getCorreo()).isPresent())
            return "El correo ya está registrado.";

        Estudiante estudianteExistente =
                estudianteRepository.findById(dto.getBoleta()).orElse(null);

        if (estudianteExistente != null) {
            if (usuarioEstudianteRepository.findByBoleta(dto.getBoleta()).isPresent())
                return "La boleta ya está registrada.";
        }

        Estudiante est = (estudianteExistente != null)
                ? estudianteExistente
                : estudianteRepository.save(Estudiante.builder()
                .boleta(dto.getBoleta())
                .carrera(dto.getCarrera())
                .escuela(dto.getEscuela())
                .build());

        String hash = passwordEncoder.encode(dto.getContraseña());

        Usuario u = usuarioRepository.save(Usuario.builder()
                .nombre(dto.getNombreUsuario())
                .correo(dto.getCorreo())
                .userPassword(hash)
                .rol("estudiante")
                .verificado(false)
                .build());

        usuarioEstudianteRepository.save(UsuarioEstudiante.builder()
                .idUsuario(u.getIdUsuario())
                .boleta(est.getBoleta())
                .build());

        perfilRepository.save(Perfil.builder()
                .tema("claro")
                .idUsuario(u.getIdUsuario())
                .build());

        return "Registro exitoso";
    }
}
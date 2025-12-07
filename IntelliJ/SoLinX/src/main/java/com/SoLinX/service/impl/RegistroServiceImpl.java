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

@Service
@AllArgsConstructor
public class RegistroServiceImpl implements RegistroService {

    private final UsuarioRepository usuarioRepository;
    private final EstudianteRepository estudianteRepository;
    private final UsuarioEstudianteRepository usuarioEstudianteRepository;

    public String registrar(RegistroDto dto) {

        if (!dto.getCorreo().equals(dto.getConfirmarCorreo())) {
            return "Los correos no coinciden.";
        }

        if (!dto.getContraseña().equals(dto.getConfirmarContraseña())) {
            return "Las contraseñas no coinciden.";
        }

        if (usuarioRepository.findByCorreo(dto.getCorreo()) != null) {
            return "El correo ya está registrado.";
        }

        Estudiante est = estudianteRepository.findById(dto.getBoleta()).orElse(null);

        if (est == null) {
            est = Estudiante.builder()
                    .boleta(dto.getBoleta())
                    .carrera(dto.getCarrera())
                    .escuela(dto.getEscuela())
                    .build();

            estudianteRepository.save(est);
        }

        Usuario u = Usuario.builder()
                .nombre(dto.getNombreUsuario())
                .correo(dto.getCorreo())
                .telefono(null)
                .userPassword(dto.getContraseña())
                .rol("estudiante")
                .build();

        usuarioRepository.save(u);

        // Código CORRECTO
        UsuarioEstudiante ue = UsuarioEstudiante.builder()
                .idUsuario(u.getIdUsuario()) // <-- Cambia 'usuario' por 'idUsuario'
                .boleta(est.getBoleta())
                .build();

        usuarioEstudianteRepository.save(ue);

        return "Registro exitoso";
    }
}

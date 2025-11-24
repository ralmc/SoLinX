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

        // Validar que los correos coincidan
        if (!dto.getCorreo().equals(dto.getConfirmarCorreo())) {
            return "Los correos no coinciden.";
        }

        // Validar que las contraseñas coincidan
        if (!dto.getContraseña().equals(dto.getConfirmarContraseña())) {
            return "Las contraseñas no coinciden.";
        }

        // Validar que el correo no esté registrado
        if (usuarioRepository.findByCorreo(dto.getCorreo()) != null) {
            return "El correo ya está registrado.";
        }

        // NUEVA VALIDACIÓN: Verificar si la boleta ya está registrada
        Estudiante estudianteExistente = estudianteRepository.findById(dto.getBoleta()).orElse(null);
        if (estudianteExistente != null) {
            // Verificar si ya tiene un usuario asociado
            UsuarioEstudiante usuarioEstudianteExistente = usuarioEstudianteRepository.findByBoleta(dto.getBoleta());
            if (usuarioEstudianteExistente != null) {
                return "La boleta ya está registrada.";
            }
        }

        // Crear o actualizar estudiante
        Estudiante est = estudianteExistente;
        if (est == null) {
            est = Estudiante.builder()
                    .boleta(dto.getBoleta())
                    .carrera(dto.getCarrera())
                    .escuela(dto.getEscuela())
                    .build();
            estudianteRepository.save(est);
        }

        // Crear usuario
        Usuario u = Usuario.builder()
                .nombre(dto.getNombreUsuario())
                .correo(dto.getCorreo())
                .telefono(null)
                .userPassword(dto.getContraseña())
                .rol("estudiante")
                .build();

        usuarioRepository.save(u);

        // Crear relación usuario-estudiante
        UsuarioEstudiante ue = UsuarioEstudiante.builder()
                .idUsuario(u.getIdUsuario())
                .boleta(est.getBoleta())
                .build();

        usuarioEstudianteRepository.save(ue);

        return "Registro exitoso";
    }
}
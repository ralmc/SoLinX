package com.SoLinX.service.impl;

import com.SoLinX.dto.RegistroSupervisorDTO;
import com.SoLinX.dto.RegistroSupervisorResponseDTO;
import com.SoLinX.model.Supervisor;
import com.SoLinX.model.Usuario;
import com.SoLinX.model.UsuarioSupervisor;
import com.SoLinX.repository.SupervisorRepository;
import com.SoLinX.repository.UsuarioRepository;
import com.SoLinX.repository.UsuarioSupervisorRepository;
import com.SoLinX.service.RegistroSupervisorService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class RegistroSupervisorServiceImpl implements RegistroSupervisorService {

    private final UsuarioRepository usuarioRepository;
    private final SupervisorRepository supervisorRepository;
    private final UsuarioSupervisorRepository usuarioSupervisorRepository;

    @Override
    @Transactional
    public RegistroSupervisorResponseDTO registrarSupervisor(RegistroSupervisorDTO dto) {
        try {

            Usuario usuarioExistente = usuarioRepository.findByCorreo(dto.getCorreo());
            if (usuarioExistente != null) {
                return null;
            }

            Usuario nuevoUsuario = Usuario.builder()
                    .nombre(dto.getNombreSupervisor())
                    .correo(dto.getCorreo())
                    .telefono(dto.getTelefono())
                    .userPassword(dto.getUserPassword())
                    .rol("supervisor")
                    .build();

            Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

            Supervisor nuevoSupervisor = Supervisor.builder()
                    .area(dto.getArea())
                    .idEmpresa(dto.getIdEmpresa())
                    .build();

            Supervisor supervisorGuardado = supervisorRepository.save(nuevoSupervisor);

            UsuarioSupervisor usuarioSupervisor = UsuarioSupervisor.builder()
                    .idUsuario(usuarioGuardado.getIdUsuario())
                    .idSupervisor(supervisorGuardado.getIdSupervisor())
                    .build();

            usuarioSupervisorRepository.save(usuarioSupervisor);

            return RegistroSupervisorResponseDTO.builder()
                    .idUsuario(usuarioGuardado.getIdUsuario())
                    .nombre(usuarioGuardado.getNombre())
                    .correo(usuarioGuardado.getCorreo())
                    .rol(usuarioGuardado.getRol())
                    .idSupervisor(supervisorGuardado.getIdSupervisor())
                    .area(supervisorGuardado.getArea())
                    .idEmpresa(supervisorGuardado.getIdEmpresa())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
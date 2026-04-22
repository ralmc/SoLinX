package com.SoLinX.service.impl;

import com.SoLinX.dto.RegistroEmpresaDTO;
import com.SoLinX.dto.RegistroEmpresaResponseDTO;
import com.SoLinX.model.*;
import com.SoLinX.repository.*;
import com.SoLinX.service.RegistroEmpresaService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class RegistroEmpresaServiceImpl implements RegistroEmpresaService {

    private final UsuarioRepository        usuarioRepository;
    private final EmpresaRepository        empresaRepository;
    private final UsuarioEmpresaRepository usuarioEmpresaRepository;
    private final PerfilRepository         perfilRepository;
    private final BCryptPasswordEncoder    passwordEncoder;

    @Override
    @Transactional
    public RegistroEmpresaResponseDTO registrarEmpresa(RegistroEmpresaDTO dto) {
        try {
            if (usuarioRepository.findByCorreo(dto.getCorreo()).isPresent()) return null;

            Usuario usuarioGuardado = usuarioRepository.save(Usuario.builder()
                    .nombre(dto.getNombreEmpresa())
                    .correo(dto.getCorreo())
                    .telefono(dto.getTelefono())
                    .userPassword(passwordEncoder.encode(dto.getUserPassword()))
                    .rol("empresa")
                    .verificado(false)
                    .build());

            Empresa empresaGuardada = empresaRepository.save(Empresa.builder()
                    .nombreEmpresa(dto.getNombreEmpresa())
                    .build());

            usuarioEmpresaRepository.save(UsuarioEmpresa.builder()
                    .idUsuario(usuarioGuardado.getIdUsuario())
                    .idEmpresa(empresaGuardada.getIdEmpresa())
                    .build());

            perfilRepository.save(Perfil.builder()
                    .tema("claro")
                    .idUsuario(usuarioGuardado.getIdUsuario())
                    .build());

            return RegistroEmpresaResponseDTO.builder()
                    .idUsuario(usuarioGuardado.getIdUsuario())
                    .nombre(usuarioGuardado.getNombre())
                    .correo(usuarioGuardado.getCorreo())
                    .rol(usuarioGuardado.getRol())
                    .idEmpresa(empresaGuardada.getIdEmpresa())
                    .nombreEmpresa(empresaGuardada.getNombreEmpresa())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
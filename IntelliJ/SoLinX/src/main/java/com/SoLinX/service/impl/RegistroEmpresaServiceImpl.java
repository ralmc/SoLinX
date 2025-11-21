package com.SoLinX.service.impl;

import com.SoLinX.dto.RegistroEmpresaDTO;
import com.SoLinX.dto.RegistroEmpresaResponseDTO;
import com.SoLinX.model.Empresa;
import com.SoLinX.model.Usuario;
import com.SoLinX.model.UsuarioEmpresa;
import com.SoLinX.repository.EmpresaRepository;
import com.SoLinX.repository.UsuarioRepository;
import com.SoLinX.repository.UsuarioEmpresaRepository;
import com.SoLinX.service.RegistroEmpresaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class RegistroEmpresaServiceImpl implements RegistroEmpresaService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioEmpresaRepository usuarioEmpresaRepository;

    @Override
    @Transactional
    public RegistroEmpresaResponseDTO registrarEmpresa(RegistroEmpresaDTO dto) {
        try {

            Usuario usuarioExistente = usuarioRepository.findByCorreo(dto.getCorreo());
            if (usuarioExistente != null) {
                return null;
            }

            Usuario nuevoUsuario = Usuario.builder()
                    .nombre(dto.getNombreEmpresa())
                    .correo(dto.getCorreo())
                    .telefono(dto.getTelefono())
                    .userPassword(dto.getUserPassword())
                    .rol("empresa")
                    .build();

            Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

            Empresa nuevaEmpresa = Empresa.builder()
                    .nombreEmpresa(dto.getNombreEmpresa())
                    .build();

            Empresa empresaGuardada = empresaRepository.save(nuevaEmpresa);

            UsuarioEmpresa usuarioEmpresa = UsuarioEmpresa.builder()
                    .idUsuario(usuarioGuardado.getIdUsuario())
                    .idEmpresa(empresaGuardada.getIdEmpresa())
                    .build();

            usuarioEmpresaRepository.save(usuarioEmpresa);

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

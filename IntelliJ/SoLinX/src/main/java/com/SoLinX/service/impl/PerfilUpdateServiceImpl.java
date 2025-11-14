package com.SoLinX.service.impl;

import com.SoLinX.dto.PerfilUpdateDto;
import com.SoLinX.model.Perfil;
import com.SoLinX.repository.PerfilRepository;
import com.SoLinX.service.PerfilUpdateService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PerfilUpdateServiceImpl implements PerfilUpdateService {

    private final PerfilRepository perfilRepository;

    @Override
    public String actualizarPerfil(PerfilUpdateDto dto) {

        Perfil perfil = perfilRepository.findById(dto.getIdPerfil())
                .orElse(null);

        if (perfil == null) {
            return "Perfil no encontrado.";
        }

        if (dto.getFoto() != null) {
            perfil.setFoto(dto.getFoto());
        }

        if (dto.getTema() != null) {
            perfil.setTema(dto.getTema());
        }

        perfilRepository.save(perfil);

        return "Perfil actualizado correctamente.";
    }
}

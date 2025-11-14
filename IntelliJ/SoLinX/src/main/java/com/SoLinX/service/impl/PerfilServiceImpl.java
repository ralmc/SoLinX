package com.SoLinX.service.impl;

import com.SoLinX.model.Perfil;
import com.SoLinX.repository.PerfilRepository;
import com.SoLinX.service.PerfilService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class PerfilServiceImpl implements PerfilService {
    private final PerfilRepository perfilRepository;

    @Override
    public List<Perfil> getAll() {
        return perfilRepository.findAll();
    }

    @Override
    public Perfil getById(Integer id)
    {
        return perfilRepository.findById(id).orElse(null);
    }

    @Override
    public Perfil save(Perfil bperfil)
    {
        return perfilRepository.save(bperfil);
    }

    @Override
    public void delete(Integer id)
    {
        perfilRepository.deleteById(id);
    }

    @Override
    public Perfil update(Integer id, Perfil bperfil) {
        Perfil aux = perfilRepository.getById(id);
        aux.setIdPerfil(bperfil.getIdPerfil());
        aux.setFoto(bperfil.getFoto());
        aux.setTema(bperfil.getTema());
        aux.setIdPerfil(bperfil.getIdPerfil());
        perfilRepository.save(aux);
        return aux;
    }
}

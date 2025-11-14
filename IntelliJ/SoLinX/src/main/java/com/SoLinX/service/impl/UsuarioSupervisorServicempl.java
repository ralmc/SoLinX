package com.SoLinX.service.impl;

import com.SoLinX.model.UsuarioSupervisor;
import com.SoLinX.repository.UsuarioSupervisorRepository;
import com.SoLinX.service.UsuarioSupervisorService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class UsuarioSupervisorServicempl implements UsuarioSupervisorService {

    private final UsuarioSupervisorRepository repository;

    @Override
    public List<UsuarioSupervisor> getAll() {
        return repository.findAll();
    }

    @Override
    public UsuarioSupervisor getById(Integer idUsuario) {
        return repository.findById(idUsuario).orElse(null);
    }

    @Override
    public UsuarioSupervisor save(UsuarioSupervisor usuarioSupervisor) {
        return repository.save(usuarioSupervisor);
    }

    @Override
    public void delete(Integer idUsuario) {
        repository.deleteById(idUsuario);
    }

    @Override
    public UsuarioSupervisor update(Integer idUsuario, UsuarioSupervisor usuarioSupervisor) {
        UsuarioSupervisor aux = repository.getById(idUsuario);
        aux.setSupervisor(usuarioSupervisor.getSupervisor());
        return repository.save(aux);
    }
}


package com.SoLinX.service.impl;

import com.SoLinX.model.UsuarioEmpresa;
import com.SoLinX.repository.UsuarioEmpresaRepository;
import com.SoLinX.service.UsuarioEmpresaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class UsuarioEmpresaServicempl implements UsuarioEmpresaService {

    private final UsuarioEmpresaRepository usuarioEmpresaRepository;

    @Override
    public List<UsuarioEmpresa> getAll() {
        return usuarioEmpresaRepository.findAll();
    }

    @Override
    public UsuarioEmpresa getById(Integer id) {
        return usuarioEmpresaRepository.findById(id).orElse(null);
    }

    @Override
    public UsuarioEmpresa save(UsuarioEmpresa ue) {
        return usuarioEmpresaRepository.save(ue);
    }

    @Override
    public void delete(Integer id) {
        usuarioEmpresaRepository.deleteById(id);
    }

    @Override
    public UsuarioEmpresa update(Integer id, UsuarioEmpresa ue) {
        UsuarioEmpresa aux = usuarioEmpresaRepository.getById(id);
        aux.setUsuario(ue.getUsuario());
        aux.setEmpresa(ue.getEmpresa());
        return usuarioEmpresaRepository.save(aux);
    }
}


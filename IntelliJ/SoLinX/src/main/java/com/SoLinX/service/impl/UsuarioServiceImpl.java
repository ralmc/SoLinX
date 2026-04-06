package com.SoLinX.service.impl;

import com.SoLinX.model.Usuario;
import com.SoLinX.repository.UsuarioRepository;
import com.SoLinX.service.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @AllArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Override public List<Usuario> getAll() { return usuarioRepository.findAll(); }

    @Override
    public Usuario getById(Integer id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @Override
    public Usuario save(Usuario u) { return usuarioRepository.save(u); }

    @Override
    public void delete(Integer id) { usuarioRepository.deleteById(id); }

    @Override
    public Usuario update(Integer id, Usuario nuevo) {
        return usuarioRepository.findById(id).map(aux -> {
            aux.setNombre(nuevo.getNombre());
            aux.setCorreo(nuevo.getCorreo());
            aux.setTelefono(nuevo.getTelefono());
            aux.setUserPassword(nuevo.getUserPassword());
            aux.setRol(nuevo.getRol());
            return usuarioRepository.save(aux);
        }).orElse(null);
    }
}
package com.SoLinX.service.impl;

import com.SoLinX.model.Usuario;
import com.SoLinX.repository.UsuarioRepository;
import com.SoLinX.service.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;

    @Override
    public List<Usuario> getAll()
    {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario getById(Integer id)
    {
        return usuarioRepository.findById(id).orElse(null);
    }

    @Override
    public Usuario save(Usuario busuario)
    {
        return usuarioRepository.save(busuario);
    }

    @Override
    public void delete(Integer id)
    {
        usuarioRepository.deleteById(id);
    }

    @Override
    public Usuario update(Integer id, Usuario busuario) {
        Usuario aux = usuarioRepository.getById(id);
        aux.setNombre(busuario.getNombre());
        aux.setCorreo(busuario.getCorreo());
        aux.setTelefono(busuario.getTelefono());
        aux.setUserPassword(busuario.getUserPassword());
        aux.setRol(busuario.getRol());
        usuarioRepository.save(aux);
        return aux;
    }
}

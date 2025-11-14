package com.SoLinX.service;

import com.SoLinX.model.Usuario;
import java.util.List;

public interface UsuarioService {
    List<Usuario> getAll( );
    Usuario getById(Integer idUsuario);
    Usuario save(Usuario busuario);
    void delete(Integer idUsuario);
    Usuario update(Integer idUsuario, Usuario busuario);
}

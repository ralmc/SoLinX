package com.SoLinX.service;

import com.SoLinX.model.Perfil;
import java.util.List;

public interface PerfilService {
    List<Perfil> getAll( );
    Perfil getById(Integer idPerfil);
    Perfil save(Perfil bPerfil);
    void delete(Integer idPerfil);
    Perfil update(Integer idPerfil, Perfil bPerfil);
}

package com.SoLinX.service;

import com.SoLinX.model.UsuarioSupervisor;
import java.util.List;

public interface UsuarioSupervisorService {
    List<UsuarioSupervisor> getAll();
    UsuarioSupervisor getById(Integer idUsuario);
    UsuarioSupervisor save(UsuarioSupervisor usuarioSupervisor);
    void delete(Integer idUsuario);
    UsuarioSupervisor update(Integer idUsuario, UsuarioSupervisor usuarioSupervisor);
}

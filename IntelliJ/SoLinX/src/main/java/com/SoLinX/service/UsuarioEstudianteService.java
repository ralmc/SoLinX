package com.SoLinX.service;

import com.SoLinX.model.UsuarioEstudiante;

import java.util.List;

public interface UsuarioEstudianteService {
    List<UsuarioEstudiante> getAll();
    UsuarioEstudiante getById(Integer id);
    UsuarioEstudiante save(UsuarioEstudiante ue);
    void delete(Integer id);
    UsuarioEstudiante update(Integer id, UsuarioEstudiante ue);
}

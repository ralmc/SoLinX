package com.SoLinX.service;

import com.SoLinX.model.Estudiante;

import java.util.List;

public interface EstudianteService {
    List<Estudiante> getAll( );
    Estudiante getById(Integer idEstudiante);
    Estudiante save(Estudiante bEstudiante);
    void delete(Integer idEstudiante);
    Estudiante update(Integer idEstudiante, Estudiante bEstudiante);
}

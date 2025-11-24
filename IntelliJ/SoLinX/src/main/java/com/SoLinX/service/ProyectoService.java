package com.SoLinX.service;

import com.SoLinX.model.Proyecto;
import java.util.List;

public interface ProyectoService {

    // Métodos que ya tenías
    List<Proyecto> getAll();
    Proyecto getById(Integer id);
    Proyecto save(Proyecto proyecto);
    Proyecto update(Integer id, Proyecto proyecto);
    void delete(Integer id);

    List<Proyecto> obtenerPorEmpresa(Integer idEmpresa);
}
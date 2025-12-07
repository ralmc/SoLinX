package com.SoLinX.service;

import com.SoLinX.model.Proyecto;

import java.util.List;

public interface ProyectoService {
    List<Proyecto> getAll();
    Proyecto getById(Integer id);
    Proyecto save(Proyecto Proyecto);
    Proyecto update(Integer id, Proyecto Proyecto);
    void delete(Integer id);
}
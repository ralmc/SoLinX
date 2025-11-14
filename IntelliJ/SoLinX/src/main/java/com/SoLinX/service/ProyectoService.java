package com.SoLinX.service;

import com.SoLinX.dto.ProyectoDto;

import java.util.List;

public interface ProyectoService {

    List<ProyectoDto> getAll();

    ProyectoDto getById(Integer id);

    ProyectoDto save(ProyectoDto proyectoDto);

    ProyectoDto update(Integer id, ProyectoDto proyectoDto);

    void delete(Integer id);
}
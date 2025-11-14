package com.SoLinX.service;

import com.SoLinX.dto.SupervisorDto;

import java.util.List;

public interface SupervisorService {

    List<SupervisorDto> getAll();

    SupervisorDto getById(Integer id);

    SupervisorDto save(SupervisorDto supervisorDto);

    SupervisorDto update(Integer id, SupervisorDto supervisorDto);

    void delete(Integer id);
}
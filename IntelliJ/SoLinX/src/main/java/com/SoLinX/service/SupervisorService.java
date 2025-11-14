package com.SoLinX.service;

import com.SoLinX.model.Supervisor;

import java.util.List;

public interface SupervisorService {
    List<Supervisor> getAll();
    Supervisor getById(Integer id);
    Supervisor save(Supervisor Supervisor);
    Supervisor update(Integer id, Supervisor Supervisor);
    void delete(Integer id);
}
package com.SoLinX.service.impl;

import com.SoLinX.model.Supervisor;
import com.SoLinX.repository.SupervisorRepository;
import com.SoLinX.service.SupervisorService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class SupervisorServiceImpl implements SupervisorService {

    private final SupervisorRepository supervisorRepository;

    @Override
    public List<Supervisor> getAll() {
        return supervisorRepository.findAll();
    }

    @Override
    public Supervisor getById(Integer id) {
        return supervisorRepository.findById(id).orElse(null);
    }

    @Override
    public Supervisor save(Supervisor supervisor) {
        return supervisorRepository.save(supervisor);
    }

    @Override
    public void delete(Integer id) {
        supervisorRepository.deleteById(id);
    }

    @Override
    public Supervisor update(Integer id, Supervisor supervisor) {
        Supervisor aux = supervisorRepository.getReferenceById(id);

        aux.setArea(supervisor.getArea());
        aux.setEmpresa(supervisor.getEmpresa());

        supervisorRepository.save(aux);
        return aux;
    }
}
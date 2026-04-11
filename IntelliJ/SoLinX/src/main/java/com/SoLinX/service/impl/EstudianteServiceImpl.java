package com.SoLinX.service.impl;

import com.SoLinX.model.Estudiante;
import com.SoLinX.repository.EstudianteRepository;
import com.SoLinX.service.EstudianteService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @AllArgsConstructor
public class EstudianteServiceImpl implements EstudianteService {

    private final EstudianteRepository estudianteRepository;

    @Override public List<Estudiante> getAll() { return estudianteRepository.findAll(); }

    @Override
    public Estudiante getById(Integer id) {
        return estudianteRepository.findById(id).orElse(null);
    }

    @Override
    public Estudiante save(Estudiante e) { return estudianteRepository.save(e); }

    @Override
    public void delete(Integer id) { estudianteRepository.deleteById(id); }

    @Override
    public Estudiante update(Integer id, Estudiante nuevo) {
        return estudianteRepository.findById(id).map(aux -> {
            aux.setCarrera(nuevo.getCarrera());
            aux.setEscuela(nuevo.getEscuela());
            return estudianteRepository.save(aux);
        }).orElse(null);
    }
}
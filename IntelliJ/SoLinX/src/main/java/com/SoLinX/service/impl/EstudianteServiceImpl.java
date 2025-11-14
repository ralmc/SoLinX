package com.SoLinX.service.impl;

import com.SoLinX.model.Estudiante;
import com.SoLinX.repository.EstudianteRepository;
import com.SoLinX.service.EstudianteService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class EstudianteServiceImpl implements EstudianteService {
    private final EstudianteRepository EstudianteRepository;

    @Override
    public List<Estudiante> getAll() {
        return EstudianteRepository.findAll();
    }

    @Override
    public Estudiante getById(Integer id) {
        return EstudianteRepository.findById(id).orElse(null);
    }

    @Override
    public Estudiante save(Estudiante bEstudiante) {
        return EstudianteRepository.save(bEstudiante);
    }

    @Override
    public void delete(Integer id) {
        EstudianteRepository.deleteById(id);
    }

    @Override
    public Estudiante update(Integer id, Estudiante bEstudiante) {
        Estudiante aux = EstudianteRepository.getById(id);
        aux.setBoleta(bEstudiante.getBoleta());
        aux.setCarrera(bEstudiante.getCarrera());
        aux.setEscuela(bEstudiante.getEscuela());
        EstudianteRepository.save(aux);
        return aux;
    }
}

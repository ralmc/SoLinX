package com.SoLinX.service.impl;

import com.SoLinX.model.Horario;
import com.SoLinX.model.HorarioEstudiante;
import com.SoLinX.repository.HorarioEstudianteRepository;
import com.SoLinX.repository.HorarioRepository;
import com.SoLinX.service.HorarioService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class HorarioServiceImpl implements HorarioService {

    private final HorarioRepository horarioRepository;
    private final HorarioEstudianteRepository horarioEstudianteRepository;

    @Override
    public HorarioEstudiante asignarHorario(Integer boleta, Horario horario) {
        Horario horarioGuardado = horarioRepository.save(horario);
        HorarioEstudiante he = HorarioEstudiante.builder()
                .boleta(boleta)
                .horario(horarioGuardado)
                .build();
        return horarioEstudianteRepository.save(he);
    }

    @Override
    public Horario getHorarioByBoleta(Integer boleta) {
        return horarioEstudianteRepository.findByBoleta(boleta)
                .map(HorarioEstudiante::getHorario)
                .orElse(null);
    }
}
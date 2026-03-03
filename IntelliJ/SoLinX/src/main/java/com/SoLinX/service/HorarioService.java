package com.SoLinX.service;

import com.SoLinX.model.Horario;
import com.SoLinX.model.HorarioEstudiante;

public interface HorarioService {
    HorarioEstudiante asignarHorario(Integer boleta, Horario horario);
    Horario getHorarioByBoleta(Integer boleta);
}
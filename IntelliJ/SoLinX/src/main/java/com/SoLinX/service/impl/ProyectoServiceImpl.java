package com.SoLinX.service.impl;

import com.SoLinX.model.Proyecto;
import com.SoLinX.repository.ProyectoRepository;
import com.SoLinX.service.ProyectoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ProyectoServiceImpl implements ProyectoService {
    private final ProyectoRepository proyectoRepository;

    @Override
    public List<Proyecto> getAll() {
        return proyectoRepository.findAll();
    }

    @Override
    public Proyecto getById(Integer id) {
        return proyectoRepository.findById(id).orElse(null);
    }

    @Override
    public Proyecto save(Proyecto bProyecto) {
        return proyectoRepository.save(bProyecto);
    }

    @Override
    public void delete(Integer id) {
        proyectoRepository.deleteById(id);
    }

    @Override
    public Proyecto update(Integer id, Proyecto bProyecto) {
        Proyecto aux = proyectoRepository.findById(id).orElse(null);
        aux.setIdProyecto(bProyecto.getIdProyecto());
        aux.setNombreProyecto(bProyecto.getNombreProyecto());
        aux.setObjetivo(bProyecto.getObjetivo());
        aux.setFechaInicio(bProyecto.getFechaInicio());
        aux.setVacantes(bProyecto.getVacantes());
        aux.setUbicacion(bProyecto.getUbicacion());
        aux.setJustificacion(bProyecto.getJustificacion());
        aux.setFechaTermino(bProyecto.getFechaTermino());
        aux.setIdEmpresa(bProyecto.getIdEmpresa());
        proyectoRepository.save(aux);
        return aux;
    }
}

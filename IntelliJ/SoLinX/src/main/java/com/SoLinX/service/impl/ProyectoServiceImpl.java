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

        if (aux != null) {
            aux.setCarreraEnfocada(bProyecto.getCarreraEnfocada());
            aux.setNombreProyecto(bProyecto.getNombreProyecto());
            aux.setObjetivo(bProyecto.getObjetivo());
            aux.setVacantes(bProyecto.getVacantes());
            aux.setUbicacion(bProyecto.getUbicacion());
            aux.setFechaInicio(bProyecto.getFechaInicio());
            aux.setFechaTermino(bProyecto.getFechaTermino());
            aux.setImagenRef(bProyecto.getImagenRef());
            aux.setEmpresa(bProyecto.getEmpresa());

            return proyectoRepository.save(aux);
        }
        return null;
    }

    @Override
    public List<Proyecto> obtenerPorEmpresa(Integer idEmpresa) {
        return proyectoRepository.findByEmpresa_IdEmpresa(idEmpresa);
    }
}
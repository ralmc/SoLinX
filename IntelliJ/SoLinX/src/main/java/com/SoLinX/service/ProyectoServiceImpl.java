package com.SoLinX.service;

import com.SoLinX.dto.ProyectoDto;
import com.SoLinX.model.Empresa;
import com.SoLinX.model.Proyecto;
import com.SoLinX.repository.EmpresaRepository;
import com.SoLinX.repository.ProyectoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProyectoServiceImpl implements ProyectoService {

    private final ProyectoRepository proyectoRepository;
    private final EmpresaRepository empresaRepository;
    private ProyectoDto convertirEntidadADto(Proyecto proyecto) {
        return ProyectoDto.builder()
                .idProyecto(proyecto.getIdProyecto())
                .nombreProyecto(proyecto.getNombreProyecto())
                .objetivo(proyecto.getObjetivo())
                .fechaInicio(proyecto.getFechaInicio())
                .vacantes(proyecto.getVacantes())
                .ubicacion(proyecto.getUbicacion())
                .justificacion(proyecto.getJustificacion())
                .fechaTermino(proyecto.getFechaTermino())
                .idEmpresa(proyecto.getEmpresa().getIdEmpresa()) // Sacamos el ID del objeto
                .build();
    }

    private Proyecto convertirDtoAEntidad(ProyectoDto dto) {

        Empresa empresa = empresaRepository.findById(dto.getIdEmpresa())
                .orElseThrow(() -> new RuntimeException("Error: Empresa no encontrada con id " + dto.getIdEmpresa()));

        return Proyecto.builder()
                .idProyecto(dto.getIdProyecto())
                .nombreProyecto(dto.getNombreProyecto())
                .objetivo(dto.getObjetivo())
                .fechaInicio(dto.getFechaInicio())
                .vacantes(dto.getVacantes())
                .ubicacion(dto.getUbicacion())
                .justificacion(dto.getJustificacion())
                .fechaTermino(dto.getFechaTermino())
                .empresa(empresa)
                .build();
    }

    @Override
    public List<ProyectoDto> getAll() {
        return proyectoRepository.findAll()
                .stream()
                .map(this::convertirEntidadADto)
                .collect(Collectors.toList());
    }

    @Override
    public ProyectoDto getById(Integer id) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        return convertirEntidadADto(proyecto);
    }

    @Override
    public ProyectoDto save(ProyectoDto proyectoDto) {

        Proyecto proyecto = convertirDtoAEntidad(proyectoDto);

        Proyecto proyectoGuardado = proyectoRepository.save(proyecto);

        return convertirEntidadADto(proyectoGuardado);
    }

    @Override
    public ProyectoDto update(Integer id, ProyectoDto proyectoDto) {

        Proyecto proyectoExistente = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        Empresa empresa = empresaRepository.findById(proyectoDto.getIdEmpresa())
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        proyectoExistente.setNombreProyecto(proyectoDto.getNombreProyecto());
        proyectoExistente.setObjetivo(proyectoDto.getObjetivo());
        proyectoExistente.setVacantes(proyectoDto.getVacantes());
        proyectoExistente.setUbicacion(proyectoDto.getUbicacion());
        proyectoExistente.setJustificacion(proyectoDto.getJustificacion());
        proyectoExistente.setFechaTermino(proyectoDto.getFechaTermino());
        proyectoExistente.setEmpresa(empresa); // Actualiza la empresa

        Proyecto proyectoActualizado = proyectoRepository.save(proyectoExistente);
        return convertirEntidadADto(proyectoActualizado);
    }

    @Override
    public void delete(Integer id) {
        if (!proyectoRepository.existsById(id)) {
            throw new RuntimeException("Proyecto no encontrado");
        }
        proyectoRepository.deleteById(id);
    }
}
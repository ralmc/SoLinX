package com.SoLinX.service;

import com.SoLinX.dto.SupervisorDto;
import com.SoLinX.model.Empresa;
import com.SoLinX.model.Supervisor;
import com.SoLinX.repository.EmpresaRepository;
import com.SoLinX.repository.SupervisorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SupervisorServiceImpl implements SupervisorService {

    private final SupervisorRepository supervisorRepository;
    private final EmpresaRepository empresaRepository;

    private SupervisorDto convertirEntidadADto(Supervisor s) {
        return SupervisorDto.builder()
                .idSupervisor(s.getIdSupervisor())
                .area(s.getArea())
                .idEmpresa(s.getEmpresa().getIdEmpresa())
                .build();
    }

    private Supervisor convertirDtoAEntidad(SupervisorDto dto) {

        Empresa empresa = empresaRepository.findById(dto.getIdEmpresa())
                .orElseThrow(() -> new RuntimeException("Error: Empresa no encontrada con id " + dto.getIdEmpresa()));

        return Supervisor.builder()

                .idSupervisor(dto.getIdSupervisor())
                .area(dto.getArea())
                .empresa(empresa)
                .build();
    }

    @Override
    public List<SupervisorDto> getAll() {
        return supervisorRepository.findAll()
                .stream()
                .map(this::convertirEntidadADto)
                .collect(Collectors.toList());
    }

    @Override
    public SupervisorDto getById(Integer id) {
        Supervisor s = supervisorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supervisor no encontrado"));
        return convertirEntidadADto(s);
    }

    @Override
    public SupervisorDto save(SupervisorDto supervisorDto) {

        Supervisor supervisor = convertirDtoAEntidad(supervisorDto);

        Supervisor supervisorGuardado = supervisorRepository.save(supervisor);

        return convertirEntidadADto(supervisorGuardado);
    }

    @Override
    public SupervisorDto update(Integer id, SupervisorDto supervisorDto) {
        Supervisor supervisorExistente = supervisorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supervisor no encontrado"));

        Empresa empresa = empresaRepository.findById(supervisorDto.getIdEmpresa())
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        supervisorExistente.setArea(supervisorDto.getArea());
        supervisorExistente.setEmpresa(empresa);

        Supervisor supervisorActualizado = supervisorRepository.save(supervisorExistente);
        return convertirEntidadADto(supervisorActualizado);
    }

    @Override
    public void delete(Integer id) {
        if (!supervisorRepository.existsById(id)) {
            throw new RuntimeException("Supervisor no encontrado");
        }
        supervisorRepository.deleteById(id);
    }
}
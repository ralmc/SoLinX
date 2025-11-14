package com.SoLinX.controler;

import com.SoLinX.dto.SupervisorDto;
import com.SoLinX.model.Empresa;
import com.SoLinX.model.Supervisor;
import com.SoLinX.service.SupervisorService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/SoLinX/api")
@RestController
@AllArgsConstructor
public class SupervisorController {

    private final SupervisorService supervisorService;

    // -------------------- CREATE ---------------------
    @PostMapping("/supervisor")
    public ResponseEntity<SupervisorDto> save(@RequestBody SupervisorDto dto) {

        Supervisor supervisor = convertToEntity(dto);
        Supervisor saved = supervisorService.save(supervisor);

        return ResponseEntity.ok(convertToDto(saved));
    }

    // -------------------- LIST ---------------------
    @GetMapping("/supervisor")
    public ResponseEntity<List<SupervisorDto>> lista() {
        List<Supervisor> supervisores = supervisorService.getAll();

        if (supervisores.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<SupervisorDto> dtos = supervisores.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // -------------------- GET BY ID ---------------------
    @GetMapping("/supervisor/{id}")
    public ResponseEntity<SupervisorDto> getById(@PathVariable Integer id) {
        Supervisor supervisor = supervisorService.getById(id);
        return ResponseEntity.ok(convertToDto(supervisor));
    }

    // -------------------- UPDATE ---------------------
    @PutMapping("/supervisor/{id}")
    public ResponseEntity<SupervisorDto> update(@PathVariable Integer id, @RequestBody SupervisorDto dto) {

        Supervisor supervisor = convertToEntity(dto);
        Supervisor actualizado = supervisorService.update(id, supervisor);

        return ResponseEntity.ok(convertToDto(actualizado));
    }

    // -------------------- DELETE ---------------------
    @DeleteMapping("/supervisor/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        supervisorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ============================================================
    //              MÉTODOS DE CONVERSIÓN DTO <-> ENTITY
    // ============================================================

    private SupervisorDto convertToDto(Supervisor supervisor) {
        return SupervisorDto.builder()
                .idSupervisor(supervisor.getIdSupervisor())
                .area(supervisor.getArea())
                .idEmpresa(supervisor.getEmpresa().getIdEmpresa())
                .build();
    }

    private Supervisor convertToEntity(SupervisorDto dto) {
        Empresa empresa = new Empresa();     // solo mandamos el ID, JPA se encarga
        empresa.setIdEmpresa(dto.getIdEmpresa());

        return Supervisor.builder()
                .idSupervisor(dto.getIdSupervisor())
                .area(dto.getArea())
                .empresa(empresa)
                .build();
    }
}

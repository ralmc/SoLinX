package com.SoLinX.controler;

import com.SoLinX.dto.EmpresaDto;
import com.SoLinX.model.Empresa;
import com.SoLinX.repository.EmpresaRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/SoLinX/api")
@RestController
@AllArgsConstructor
public class EmpresaController {

    private final EmpresaRepository empresaRepository;

    private EmpresaDto convertirEntidadADto(Empresa emp) {
        return EmpresaDto.builder()
                .idEmpresa(emp.getIdEmpresa())
                .nombreEmpresa(emp.getNombreEmpresa())
                .build();
    }

    @PostMapping("/empresa")
    public ResponseEntity<EmpresaDto> save(@RequestBody EmpresaDto dto) {

        Empresa empresa = Empresa.builder()
                .nombreEmpresa(dto.getNombreEmpresa())
                .build();

        Empresa empresaGuardada = empresaRepository.save(empresa);

        return ResponseEntity.ok(convertirEntidadADto(empresaGuardada));
    }

    @GetMapping("/empresa")
    public ResponseEntity<List<EmpresaDto>> lista() {
        List<Empresa> empresas = empresaRepository.findAll();

        List<EmpresaDto> dtos = empresas.stream()
                .map(this::convertirEntidadADto)
                .collect(Collectors.toList());

        if (dtos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/empresa/{id}")
    public ResponseEntity<EmpresaDto> getById(@PathVariable Integer id) {
        Empresa empresa = empresaRepository.findById(id).orElse(null);

        if (empresa == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirEntidadADto(empresa));
    }

    @PutMapping("/empresa/{id}")
    public ResponseEntity<EmpresaDto> update(@PathVariable Integer id, @RequestBody EmpresaDto dto) {

        Empresa empresaExistente = empresaRepository.findById(id).orElse(null);

        if (empresaExistente == null) {
            return ResponseEntity.notFound().build();
        }

        empresaExistente.setNombreEmpresa(dto.getNombreEmpresa());

        Empresa actualizada = empresaRepository.save(empresaExistente);

        return ResponseEntity.ok(convertirEntidadADto(actualizada));
    }

    @DeleteMapping("/empresa/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!empresaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        empresaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
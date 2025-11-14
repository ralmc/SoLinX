package com.SoLinX.controler;

import com.SoLinX.dto.SupervisorDto;
import com.SoLinX.service.SupervisorService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/SoLinX/api")
@RestController
@AllArgsConstructor
public class SupervisorController {

    private final SupervisorService supervisorService;

    @PostMapping("/supervisor")
    public ResponseEntity<SupervisorDto> save(@RequestBody SupervisorDto dto) {

        SupervisorDto supervisorGuardado = supervisorService.save(dto);
        return ResponseEntity.ok(supervisorGuardado);
    }

    @GetMapping("/supervisor")
    public ResponseEntity<List<SupervisorDto>> lista() {
        List<SupervisorDto> dtos = supervisorService.getAll();
        if (dtos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/supervisor/{id}")
    public ResponseEntity<SupervisorDto> getById(@PathVariable Integer id) {
        SupervisorDto dto = supervisorService.getById(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/supervisor/{id}")
    public ResponseEntity<SupervisorDto> update(@PathVariable Integer id, @RequestBody SupervisorDto dto) {
        SupervisorDto actualizado = supervisorService.update(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/supervisor/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        supervisorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
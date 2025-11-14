package com.SoLinX.controler;

import com.SoLinX.dto.ProyectoDto;
import com.SoLinX.service.ProyectoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/SoLinX/api")
@RestController
@AllArgsConstructor
public class ProyectoController {

    private final ProyectoService proyectoService;

    @PostMapping("/proyecto")
    public ResponseEntity<ProyectoDto> save(@RequestBody ProyectoDto dto) {

        ProyectoDto proyectoGuardado = proyectoService.save(dto);
        return ResponseEntity.ok(proyectoGuardado);
    }

    @GetMapping("/proyecto")
    public ResponseEntity<List<ProyectoDto>> lista() {
        List<ProyectoDto> dtos = proyectoService.getAll();
        if (dtos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/proyecto/{id}")
    public ResponseEntity<ProyectoDto> getById(@PathVariable Integer id) {
        ProyectoDto dto = proyectoService.getById(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/proyecto/{id}")
    public ResponseEntity<ProyectoDto> update(@PathVariable Integer id, @RequestBody ProyectoDto dto) {
        ProyectoDto actualizado = proyectoService.update(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/proyecto/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        proyectoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
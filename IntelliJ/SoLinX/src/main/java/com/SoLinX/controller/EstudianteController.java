package com.SoLinX.controller;

import com.SoLinX.dto.EstudianteDto;
import com.SoLinX.model.Estudiante;
import com.SoLinX.service.EstudianteService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/SoLinX/api")
@AllArgsConstructor
public class EstudianteController {

    private final EstudianteService estudianteService;

    @GetMapping("/estudiante")
    public ResponseEntity<List<EstudianteDto>> lista() {
        return ResponseEntity.ok(estudianteService.getAll().stream()
                .map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/estudiante/{id}")
    public ResponseEntity<EstudianteDto> getById(@PathVariable("id") Integer id) {
        Estudiante u = estudianteService.getById(id);
        return u != null ? ResponseEntity.ok(toDto(u)) : ResponseEntity.notFound().build();
    }

    @PostMapping("/estudiante")
    public ResponseEntity<EstudianteDto> save(@RequestBody EstudianteDto dto) {
        return ResponseEntity.ok(toDto(estudianteService.save(fromDto(dto))));
    }

    @PutMapping("/estudiante/{id}")
    public ResponseEntity<EstudianteDto> update(@PathVariable Integer id, @RequestBody EstudianteDto dto) {
        Estudiante aux = estudianteService.update(id, fromDto(dto));
        return aux != null ? ResponseEntity.ok(toDto(aux)) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/estudiante/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        estudianteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private EstudianteDto toDto(Estudiante e) {
        return EstudianteDto.builder().boleta(e.getBoleta()).carrera(e.getCarrera()).escuela(e.getEscuela()).build();
    }

    private Estudiante fromDto(EstudianteDto d) {
        return Estudiante.builder().boleta(d.getBoleta()).carrera(d.getCarrera()).escuela(d.getEscuela()).build();
    }
}
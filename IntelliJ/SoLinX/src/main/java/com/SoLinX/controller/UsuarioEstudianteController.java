package com.SoLinX.controller;

import com.SoLinX.dto.UsuarioEstudianteDto;
import com.SoLinX.model.UsuarioEstudiante;
import com.SoLinX.service.UsuarioEstudianteService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/SoLinX/api")
@AllArgsConstructor
public class UsuarioEstudianteController {

    private final UsuarioEstudianteService usuarioEstudianteService;

    @GetMapping("/usuarioEstudiante")
    public ResponseEntity<List<UsuarioEstudianteDto>> lista(
            @RequestParam(required = false) Integer idUsuario) {
        List<UsuarioEstudianteDto> result = usuarioEstudianteService.getAll().stream()
                .filter(u -> idUsuario == null || u.getIdUsuario().equals(idUsuario))
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/usuarioEstudiante/{id}")
    public ResponseEntity<UsuarioEstudianteDto> getById(@PathVariable Integer id) {
        UsuarioEstudiante u = usuarioEstudianteService.getById(id);
        return u != null ? ResponseEntity.ok(toDto(u)) : ResponseEntity.notFound().build();
    }

    @PostMapping("/usuarioEstudiante")
    public ResponseEntity<UsuarioEstudianteDto> save(@RequestBody UsuarioEstudianteDto dto) {
        return ResponseEntity.ok(toDto(usuarioEstudianteService.save(fromDto(dto))));
    }

    @PutMapping("/usuarioEstudiante/{id}")
    public ResponseEntity<UsuarioEstudianteDto> update(@PathVariable Integer id, @RequestBody UsuarioEstudianteDto dto) {
        UsuarioEstudiante u = usuarioEstudianteService.update(id, fromDto(dto));
        return u != null ? ResponseEntity.ok(toDto(u)) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/usuarioEstudiante/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        usuarioEstudianteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private UsuarioEstudianteDto toDto(UsuarioEstudiante u) {
        return UsuarioEstudianteDto.builder().idUsuario(u.getIdUsuario()).boleta(u.getBoleta()).build();
    }

    private UsuarioEstudiante fromDto(UsuarioEstudianteDto d) {
        return UsuarioEstudiante.builder().idUsuario(d.getIdUsuario()).boleta(d.getBoleta()).build();
    }
}
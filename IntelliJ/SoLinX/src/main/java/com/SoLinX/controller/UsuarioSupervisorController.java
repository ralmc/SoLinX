package com.SoLinX.controller;

import com.SoLinX.dto.UsuarioSupervisorDto;
import com.SoLinX.model.UsuarioSupervisor;
import com.SoLinX.service.UsuarioSupervisorService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/SoLinX/api")
@AllArgsConstructor
public class UsuarioSupervisorController {

    private final UsuarioSupervisorService usuarioSupervisorService;

    @GetMapping("/usuarioSupervisor")
    public ResponseEntity<List<UsuarioSupervisorDto>> lista(
            @RequestParam(required = false) Integer idUsuario) {
        List<UsuarioSupervisorDto> result = usuarioSupervisorService.getAll().stream()
                .filter(u -> idUsuario == null || u.getIdUsuario().equals(idUsuario))
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/usuarioSupervisor/{id}")
    public ResponseEntity<UsuarioSupervisorDto> getById(@PathVariable Integer id) {
        UsuarioSupervisor u = usuarioSupervisorService.getById(id);
        return u != null ? ResponseEntity.ok(toDto(u)) : ResponseEntity.notFound().build();
    }

    @PostMapping("/usuarioSupervisor")
    public ResponseEntity<UsuarioSupervisorDto> save(@RequestBody UsuarioSupervisorDto dto) {
        return ResponseEntity.ok(toDto(usuarioSupervisorService.save(fromDto(dto))));
    }

    @PutMapping("/usuarioSupervisor/{id}")
    public ResponseEntity<UsuarioSupervisorDto> update(@PathVariable Integer id, @RequestBody UsuarioSupervisorDto dto) {
        UsuarioSupervisor u = usuarioSupervisorService.update(id, fromDto(dto));
        return u != null ? ResponseEntity.ok(toDto(u)) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/usuarioSupervisor/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        usuarioSupervisorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private UsuarioSupervisorDto toDto(UsuarioSupervisor u) {
        return UsuarioSupervisorDto.builder().idUsuario(u.getIdUsuario()).idSupervisor(u.getIdSupervisor()).build();
    }

    private UsuarioSupervisor fromDto(UsuarioSupervisorDto d) {
        return UsuarioSupervisor.builder().idUsuario(d.getIdUsuario()).idSupervisor(d.getIdSupervisor()).build();
    }
}
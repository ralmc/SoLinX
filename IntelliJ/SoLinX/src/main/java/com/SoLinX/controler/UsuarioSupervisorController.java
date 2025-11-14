package com.SoLinX.controler;

import com.SoLinX.dto.UsuarioSupervisorDto;
import com.SoLinX.model.UsuarioSupervisor;
import com.SoLinX.service.UsuarioSupervisorService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/SoLinX/api")
@RestController
@AllArgsConstructor
public class UsuarioSupervisorController {

    private final UsuarioSupervisorService usuarioSupervisorService;

    @GetMapping("/usuarioSupervisor")
    public ResponseEntity<List<UsuarioSupervisorDto>> lista(@RequestParam(name = "idUsuario", required = false) Integer idUsuario) {
        List<UsuarioSupervisor> usuarios = usuarioSupervisorService.getAll();
        if (usuarios == null || usuarios.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (idUsuario != null) {
            return ResponseEntity.ok(
                    usuarios.stream()
                            .filter(u -> u.getIdUsuario().equals(idUsuario))
                            .map(u -> UsuarioSupervisorDto.builder()
                                    .idUsuario(u.getIdUsuario())
                                    .idSupervisor(u.getIdSupervisor())
                                    .build())
                            .collect(Collectors.toList())
            );
        }
        return ResponseEntity.ok(
                usuarios.stream()
                        .map(u -> UsuarioSupervisorDto.builder()
                                .idUsuario(u.getIdUsuario())
                                .idSupervisor(u.getIdSupervisor())
                                .build())
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/usuarioSupervisor/{id}")
    public ResponseEntity<UsuarioSupervisorDto> getById(@PathVariable Integer id) {
        UsuarioSupervisor u = usuarioSupervisorService.getById(id);
        if (u == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(UsuarioSupervisorDto.builder()
                .idUsuario(u.getIdUsuario())
                .idSupervisor(u.getIdSupervisor())
                .build());
    }

    @PostMapping("/usuarioSupervisor")
    public ResponseEntity<UsuarioSupervisorDto> save(@RequestBody UsuarioSupervisorDto dto) {
        UsuarioSupervisor u = UsuarioSupervisor.builder()
                .idUsuario(dto.getIdUsuario())
                .idSupervisor(dto.getIdSupervisor())
                .build();
        usuarioSupervisorService.save(u);
        return ResponseEntity.ok(UsuarioSupervisorDto.builder()
                .idUsuario(u.getIdUsuario())
                .idSupervisor(u.getIdSupervisor())
                .build());
    }

    @DeleteMapping("/usuarioSupervisor/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        usuarioSupervisorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/usuarioSupervisor/{id}")
    public ResponseEntity<UsuarioSupervisorDto> update(@PathVariable Integer id, @RequestBody UsuarioSupervisorDto dto) {
        UsuarioSupervisor u = usuarioSupervisorService.update(id, UsuarioSupervisor.builder()
                .idUsuario(dto.getIdUsuario())
                .idSupervisor(dto.getIdSupervisor())
                .build());
        return ResponseEntity.ok(UsuarioSupervisorDto.builder()
                .idUsuario(u.getIdUsuario())
                .idSupervisor(u.getIdSupervisor())
                .build());
    }
}

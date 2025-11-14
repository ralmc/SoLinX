package com.SoLinX.controler;

import com.SoLinX.dto.UsuarioEstudianteDto;
import com.SoLinX.model.UsuarioEstudiante;
import com.SoLinX.service.UsuarioEstudianteService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/SoLinX/api")
@RestController
@AllArgsConstructor
public class UsuarioEstudianteController {

    private final UsuarioEstudianteService usuarioEstudianteService;

    @GetMapping("/usuarioEstudiante")
    public ResponseEntity<List<UsuarioEstudianteDto>> lista(@RequestParam(name = "idUsuario", defaultValue = "", required = false) Integer idUsuario) {
        List<UsuarioEstudiante> usuarios = usuarioEstudianteService.getAll();
        if (usuarios == null || usuarios.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (idUsuario != null) {
            return ResponseEntity.ok(
                    usuarios.stream()
                            .filter(u -> u.getIdUsuario().equals(idUsuario))
                            .map(u -> UsuarioEstudianteDto.builder()
                                    .idUsuario(u.getIdUsuario())
                                    .boleta(u.getBoleta())
                                    .build())
                            .collect(Collectors.toList())
            );
        }
        return ResponseEntity.ok(
                usuarios.stream()
                        .map(u -> UsuarioEstudianteDto.builder()
                                .idUsuario(u.getIdUsuario())
                                .boleta(u.getBoleta())
                                .build())
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/usuarioEstudiante/{id}")
    public ResponseEntity<UsuarioEstudianteDto> getById(@PathVariable Integer id) {
        UsuarioEstudiante u = usuarioEstudianteService.getById(id);
        if (u == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(UsuarioEstudianteDto.builder()
                .idUsuario(u.getIdUsuario())
                .boleta(u.getBoleta())
                .build());
    }

    @PostMapping("/usuarioEstudiante")
    public ResponseEntity<UsuarioEstudianteDto> save(@RequestBody UsuarioEstudianteDto dto) {
        UsuarioEstudiante u = UsuarioEstudiante.builder()
                .idUsuario(dto.getIdUsuario())
                .boleta(dto.getBoleta())
                .build();
        usuarioEstudianteService.save(u);
        return ResponseEntity.ok(UsuarioEstudianteDto.builder()
                .idUsuario(u.getIdUsuario())
                .boleta(u.getBoleta())
                .build());
    }

    @DeleteMapping("/usuarioEstudiante/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        usuarioEstudianteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/usuarioEstudiante/{id}")
    public ResponseEntity<UsuarioEstudianteDto> update(@PathVariable Integer id, @RequestBody UsuarioEstudianteDto dto) {
        UsuarioEstudiante u = usuarioEstudianteService.update(id, UsuarioEstudiante.builder()
                .idUsuario(dto.getIdUsuario())
                .boleta(dto.getBoleta())
                .build());
        return ResponseEntity.ok(UsuarioEstudianteDto.builder()
                .idUsuario(u.getIdUsuario())
                .boleta(u.getBoleta())
                .build());
    }
}

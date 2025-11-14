package com.SoLinX.controler;

import com.SoLinX.dto.UsuarioEmpresaDto;
import com.SoLinX.model.UsuarioEmpresa;
import com.SoLinX.service.UsuarioEmpresaService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/SoLinX/api")
@RestController
@AllArgsConstructor
public class UsuarioEmpresaController {

    private final UsuarioEmpresaService usuarioEmpresaService;

    @GetMapping("/usuarioEmpresa")
    public ResponseEntity<List<UsuarioEmpresaDto>> lista(@RequestParam(name = "idUsuario", required = false) Integer idUsuario) {
        List<UsuarioEmpresa> usuarios = usuarioEmpresaService.getAll();
        if (usuarios == null || usuarios.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (idUsuario != null) {
            return ResponseEntity.ok(
                    usuarios.stream()
                            .filter(u -> u.getIdUsuario().equals(idUsuario))
                            .map(u -> UsuarioEmpresaDto.builder()
                                    .idUsuario(u.getIdUsuario())
                                    .idEmpresa(u.getIdEmpresa())
                                    .build())
                            .collect(Collectors.toList())
            );
        }
        return ResponseEntity.ok(
                usuarios.stream()
                        .map(u -> UsuarioEmpresaDto.builder()
                                .idUsuario(u.getIdUsuario())
                                .idEmpresa(u.getIdEmpresa())
                                .build())
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/usuarioEmpresa/{id}")
    public ResponseEntity<UsuarioEmpresaDto> getById(@PathVariable Integer id) {
        UsuarioEmpresa u = usuarioEmpresaService.getById(id);
        if (u == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(UsuarioEmpresaDto.builder()
                .idUsuario(u.getIdUsuario())
                .idEmpresa(u.getIdEmpresa())
                .build());
    }

    @PostMapping("/usuarioEmpresa")
    public ResponseEntity<UsuarioEmpresaDto> save(@RequestBody UsuarioEmpresaDto dto) {
        UsuarioEmpresa u = UsuarioEmpresa.builder()
                .idUsuario(dto.getIdUsuario())
                .idEmpresa(dto.getIdEmpresa())
                .build();
        usuarioEmpresaService.save(u);
        return ResponseEntity.ok(UsuarioEmpresaDto.builder()
                .idUsuario(u.getIdUsuario())
                .idEmpresa(u.getIdEmpresa())
                .build());
    }

    @DeleteMapping("/usuarioEmpresa/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        usuarioEmpresaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/usuarioEmpresa/{id}")
    public ResponseEntity<UsuarioEmpresaDto> update(@PathVariable Integer id, @RequestBody UsuarioEmpresaDto dto) {
        UsuarioEmpresa u = usuarioEmpresaService.update(id, UsuarioEmpresa.builder()
                .idUsuario(dto.getIdUsuario())
                .idEmpresa(dto.getIdEmpresa())
                .build());
        return ResponseEntity.ok(UsuarioEmpresaDto.builder()
                .idUsuario(u.getIdUsuario())
                .idEmpresa(u.getIdEmpresa())
                .build());
    }
}

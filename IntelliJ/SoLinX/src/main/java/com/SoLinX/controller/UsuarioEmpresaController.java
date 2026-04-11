package com.SoLinX.controller;

import com.SoLinX.dto.UsuarioEmpresaDto;
import com.SoLinX.model.UsuarioEmpresa;
import com.SoLinX.service.UsuarioEmpresaService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/SoLinX/api")
@AllArgsConstructor
public class UsuarioEmpresaController {

    private final UsuarioEmpresaService usuarioEmpresaService;

    @GetMapping("/usuarioEmpresa")
    public ResponseEntity<List<UsuarioEmpresaDto>> lista(
            @RequestParam(required = false) Integer idUsuario) {
        List<UsuarioEmpresaDto> result = usuarioEmpresaService.getAll().stream()
                .filter(u -> idUsuario == null || u.getIdUsuario().equals(idUsuario))
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/usuarioEmpresa/{id}")
    public ResponseEntity<UsuarioEmpresaDto> getById(@PathVariable Integer id) {
        UsuarioEmpresa u = usuarioEmpresaService.getById(id);
        return u != null ? ResponseEntity.ok(toDto(u)) : ResponseEntity.notFound().build();
    }

    @PostMapping("/usuarioEmpresa")
    public ResponseEntity<UsuarioEmpresaDto> save(@RequestBody UsuarioEmpresaDto dto) {
        return ResponseEntity.ok(toDto(usuarioEmpresaService.save(fromDto(dto))));
    }

    @PutMapping("/usuarioEmpresa/{id}")
    public ResponseEntity<UsuarioEmpresaDto> update(@PathVariable Integer id, @RequestBody UsuarioEmpresaDto dto) {
        UsuarioEmpresa u = usuarioEmpresaService.update(id, fromDto(dto));
        return u != null ? ResponseEntity.ok(toDto(u)) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/usuarioEmpresa/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        usuarioEmpresaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private UsuarioEmpresaDto toDto(UsuarioEmpresa u) {
        return UsuarioEmpresaDto.builder().idUsuario(u.getIdUsuario()).idEmpresa(u.getIdEmpresa()).build();
    }

    private UsuarioEmpresa fromDto(UsuarioEmpresaDto d) {
        return UsuarioEmpresa.builder().idUsuario(d.getIdUsuario()).idEmpresa(d.getIdEmpresa()).build();
    }
}
package com.SoLinX.controller;

import com.SoLinX.dto.UsuarioDto;
import com.SoLinX.model.Usuario;
import com.SoLinX.service.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/SoLinX/api")
@AllArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/usuario")
    public ResponseEntity<List<UsuarioDto>> lista(
            @RequestParam(required = false) String nombre) {
        List<Usuario> usuarios = usuarioService.getAll();
        List<UsuarioDto> result = usuarios.stream()
                .filter(u -> nombre == null || nombre.isEmpty() || u.getNombre().equals(nombre))
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/usuario/{id}")
    public ResponseEntity<UsuarioDto> getById(@PathVariable Integer id) {
        Usuario u = usuarioService.getById(id);
        return u != null ? ResponseEntity.ok(toDto(u)) : ResponseEntity.notFound().build();
    }

    @PostMapping("/usuario")
    public ResponseEntity<UsuarioDto> save(@RequestBody UsuarioDto dto) {
        return ResponseEntity.ok(toDto(usuarioService.save(fromDto(dto))));
    }

    @PutMapping("/usuario/{id}")
    public ResponseEntity<UsuarioDto> update(@PathVariable Integer id, @RequestBody UsuarioDto dto) {
        Usuario updated = usuarioService.update(id, fromDto(dto));
        return updated != null ? ResponseEntity.ok(toDto(updated)) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/usuario/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private UsuarioDto toDto(Usuario u) {
        return UsuarioDto.builder()
                .idUsuario(u.getIdUsuario())
                .nombre(u.getNombre())
                .correo(u.getCorreo())
                .telefono(u.getTelefono())
                .userPassword(u.getUserPassword())
                .rol(u.getRol())
                .build();
    }

    private Usuario fromDto(UsuarioDto d) {
        return Usuario.builder()
                .idUsuario(d.getIdUsuario())
                .nombre(d.getNombre())
                .correo(d.getCorreo())
                .telefono(d.getTelefono())
                .userPassword(d.getUserPassword())
                .rol(d.getRol())
                .build();
    }
}
package com.SoLinX.controller;

import com.SoLinX.dto.PerfilDto;
import com.SoLinX.model.Perfil;
import com.SoLinX.service.PerfilService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/SoLinX/api")
@RestController
@AllArgsConstructor
public class PerfilController {
    private final PerfilService perfilService;
    private List<PerfilDto> PerfilDtos;

    public void loadList() {
        PerfilDtos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            PerfilDtos.add(
                    PerfilDto.builder()
                            .idPerfil(i++)
                            .foto("Foto " + i)
                            .tema("Tema " + i)
                            .idUsuario(i)
                            .build()
            );
        }
    }

    @RequestMapping("/perfil")
    public ResponseEntity<List<PerfilDto>> lista() {
        List<Perfil> perfils = perfilService.getAll();
        if (perfils == null || perfils.size() == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(perfils.stream()
                .map(u -> PerfilDto.builder()
                        .idPerfil(u.getIdPerfil())
                        .foto(u.getFoto())
                        .tema(u.getTema())
                        .idUsuario(u.getIdUsuario())
                        .build())
                .collect(Collectors.toList()));
    }

    @RequestMapping("/perfil/{id}")
    public ResponseEntity<PerfilDto> getById(@PathVariable("id") Integer id) {
        Perfil u = perfilService.getById(id);
        if (u == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(PerfilDto.builder()
                .idPerfil(u.getIdPerfil())
                .foto(u.getFoto())
                .tema(u.getTema())
                .idUsuario(u.getIdUsuario())
                .build());
    }

    @PostMapping("/perfil")
    public ResponseEntity<PerfilDto> save(@RequestBody PerfilDto PerfilDto) {
        Perfil u = Perfil.builder()
                .foto(PerfilDto.getFoto())
                .tema(PerfilDto.getTema())
                .idUsuario(PerfilDto.getIdUsuario())
                .build();
        perfilService.save(u);
        return ResponseEntity.ok(PerfilDto.builder()
                .idPerfil(u.getIdPerfil())
                .foto(u.getFoto())
                .tema(u.getTema())
                .idUsuario(u.getIdUsuario())
                .build());
    }

    @DeleteMapping("/perfil/{id}")
    public ResponseEntity<PerfilDto> delete(@PathVariable("id") Integer id) {
        perfilService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/perfil/{id}")
    public ResponseEntity<PerfilDto> update(@PathVariable("id") Integer id, @RequestBody PerfilDto PerfilDto) {
        Perfil aux = perfilService.update(id, Perfil.builder()
                .foto(PerfilDto.getFoto())
                .tema(PerfilDto.getTema())
                .idUsuario(PerfilDto.getIdUsuario())
                .build());
        return ResponseEntity.ok(PerfilDto.builder()
                .foto(aux.getFoto())
                .tema(aux.getTema())
                .idUsuario(aux.getIdUsuario())
                .build());
    }

    @GetMapping("/perfil/usuario/{idUsuario}")
    public ResponseEntity<PerfilDto> getByIdUsuario(@PathVariable("idUsuario") Integer idUsuario) {
        Perfil p = perfilService.getByIdUsuario(idUsuario);
        if (p == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(PerfilDto.builder()
                .idPerfil(p.getIdPerfil())
                .foto(p.getFoto())
                .tema(p.getTema())
                .idUsuario(p.getIdUsuario())
                .build());
    }

    @PutMapping("/perfil/usuario/{idUsuario}/foto")
    public ResponseEntity<String> actualizarFoto(@PathVariable("idUsuario") Integer idUsuario,
                                                 @RequestBody java.util.Map<String, String> body) {
        Perfil p = perfilService.getByIdUsuario(idUsuario);
        if (p == null) return ResponseEntity.notFound().build();
        p.setFoto(body.get("foto"));
        perfilService.save(p);
        return ResponseEntity.ok("Foto actualizada correctamente.");
    }

    // NUEVO: Actualizar tema en la BD
    @PutMapping("/perfil/usuario/{idUsuario}/tema")
    public ResponseEntity<String> actualizarTema(@PathVariable("idUsuario") Integer idUsuario,
                                                 @RequestBody java.util.Map<String, String> body) {
        Perfil p = perfilService.getByIdUsuario(idUsuario);
        if (p == null) return ResponseEntity.notFound().build();
        String tema = body.get("tema");
        // Validar que el tema sea uno de los permitidos
        if (tema == null || (!tema.equalsIgnoreCase("claro") && !tema.equalsIgnoreCase("oscuro"))) {
            return ResponseEntity.badRequest().body("Tema inválido. Use 'claro' u 'oscuro'.");
        }
        p.setTema(tema.toLowerCase());
        perfilService.save(p);
        return ResponseEntity.ok("Tema actualizado correctamente.");
    }
}

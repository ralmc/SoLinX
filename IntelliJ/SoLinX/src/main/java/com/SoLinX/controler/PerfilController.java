package com.SoLinX.controler;

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
        if(perfils == null || perfils.size()== 0) {
            return ResponseEntity.notFound( ).build( );
        } return ResponseEntity
                .ok(
                        perfils
                                .stream()
                                .map(u -> PerfilDto.builder()
                                        .idPerfil(u.getIdPerfil())
                                        .foto(u.getFoto())
                                        .tema( u.getTema())
                                        .idUsuario(u.getIdUsuario())
                                        .build())
                                .collect(Collectors.toList()));
    }

    @RequestMapping("/perfil/{id}")
    public ResponseEntity<PerfilDto>getById(@PathVariable Integer id) {
        Perfil u = perfilService.getById(id);

        if(u == null ) {
            return  ResponseEntity.notFound().build();
        } return ResponseEntity.ok(PerfilDto.builder()
                .idPerfil(u.getIdPerfil())
                .foto(u.getFoto())
                .tema( u.getTema())
                .idUsuario(u.getIdUsuario())
                .build());
    }

    @PostMapping( "/perfil")
    public ResponseEntity<PerfilDto> save(@RequestBody PerfilDto PerfilDto) {
        Perfil u = Perfil.
                builder()
                        .foto( PerfilDto.getFoto())
                        .tema( PerfilDto.getTema())
                        .idUsuario(PerfilDto.getIdUsuario())
                .build();
        perfilService.save(u);
        return ResponseEntity.ok(PerfilDto.builder()
                .idPerfil(u.getIdPerfil())
                .foto(u.getFoto())
                .tema( u.getTema())
                .idUsuario(u.getIdUsuario())
                .build());
    }

    @DeleteMapping( "/perfil/{id}")
    public ResponseEntity<PerfilDto> delete(@PathVariable Integer id) {
        perfilService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping( "/perfil/{id}")
    public ResponseEntity<PerfilDto>update( @PathVariable Integer id, @RequestBody PerfilDto PerfilDto) {
        Perfil aux = perfilService.update( id, Perfil
                .builder()
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
}

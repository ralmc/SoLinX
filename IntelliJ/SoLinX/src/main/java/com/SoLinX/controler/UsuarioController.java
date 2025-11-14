package com.SoLinX.controler;

import com.SoLinX.dto.UsuarioDto;
import com.SoLinX.model.Usuario;
import com.SoLinX.service.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/SoLinX/api")
@RestController
@AllArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;
    private List<UsuarioDto> usuarioDtos;

    public void loadList() {
        usuarioDtos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            usuarioDtos.add(
                    UsuarioDto.builder()
                            .idUsuario(i++)
                            .nombre("Nombre " + i)
                            .correo("Correo " + i)
                            .telefono("Telefono " + i)
                            .userPassword("Contraseña " + i)
                            .rol("Rol " + i)
                            .build()
            );
        }
    }

    @RequestMapping("/usuario")
    public ResponseEntity<List<UsuarioDto>> lista(@RequestParam (name = "nombre", defaultValue = "", required = false) String nombre) {
        List<Usuario> usuarios = usuarioService.getAll();
        if(usuarios == null || usuarios.size()== 0) {
            return ResponseEntity.notFound().build( );
        } if( nombre != null && !nombre.isEmpty()) {
            return ResponseEntity.ok( usuarios
                    .stream()
                    .filter(u -> u.getNombre().equals(nombre) )
                    .map( u ->
                            UsuarioDto.builder()
                                    .idUsuario(u.getIdUsuario())
                                    .nombre(u.getNombre())
                                    .correo(u.getCorreo())
                                    .telefono(u.getTelefono())
                                    .userPassword(u.getUserPassword())
                                    .rol(u.getRol())
                                    .build())
                    .collect(Collectors.toList()));
        } return ResponseEntity
                .ok(
                        usuarios
                                .stream()
                                .map(u -> UsuarioDto.builder()
                        .idUsuario(u.getIdUsuario())
                        .nombre(u.getNombre())
                        .correo(u.getCorreo())
                        .telefono(u.getTelefono())
                        .userPassword(u.getUserPassword())
                        .rol(u.getRol())
                        .build())
                .collect(Collectors.toList()));
    }


    @RequestMapping("/usuario/{id}")
    public ResponseEntity<UsuarioDto>getById(@PathVariable Integer id) {
        Usuario u = usuarioService.getById(id);

        if(u == null ) {
            return  ResponseEntity.notFound().build();
        } return ResponseEntity.ok(UsuarioDto.builder()
                .idUsuario(u.getIdUsuario())
                .nombre(u.getNombre())
                .correo(u.getCorreo())
                .telefono(u.getTelefono())
                .userPassword(u.getUserPassword())
                .rol(u.getRol())
                .build());
    }

    @PostMapping( "/usuario")
    public ResponseEntity<UsuarioDto> save(@RequestBody UsuarioDto usuarioDto) {
        Usuario u = Usuario.
                builder()
                        .nombre( usuarioDto.getNombre())
                        .correo( usuarioDto.getCorreo())
                        .telefono(usuarioDto.getTelefono())
                        .userPassword(usuarioDto.getUserPassword())
                        .rol(usuarioDto.getRol())
                .build();
        usuarioService.save(u);
        return ResponseEntity.ok(UsuarioDto.builder()
                .nombre(u.getNombre())
                .correo(u.getCorreo())
                .telefono(u.getTelefono())
                .userPassword(u.getUserPassword())
                .rol(u.getRol())
                .build());
    }

    @DeleteMapping( "/usuario/{id}")
    public ResponseEntity<UsuarioDto> delete(@PathVariable Integer id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping( "/usuario/{id}")
    public ResponseEntity<UsuarioDto>update( @PathVariable Integer id, @RequestBody UsuarioDto usuarioDto) {
        Usuario aux = usuarioService.update( id, Usuario
                .builder()
                    .idUsuario( usuarioDto.getIdUsuario())
                    .nombre( usuarioDto.getNombre())
                    .correo( usuarioDto.getCorreo())
                    .telefono(usuarioDto.getTelefono())
                    .userPassword(usuarioDto.getUserPassword())
                    .rol(usuarioDto.getRol())
                .build());
        return ResponseEntity.ok(UsuarioDto.builder()
                .idUsuario(aux.getIdUsuario())
                .nombre(aux.getNombre())
                .correo(aux.getCorreo())
                .telefono(aux.getTelefono())
                .userPassword(aux.getUserPassword())
                .rol(aux.getRol())
                .build());
    }
}

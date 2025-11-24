package com.SoLinX.controler;

import com.SoLinX.dto.LoginDto;           // Nombre corregido
import com.SoLinX.dto.LoginResponseDto;   // Nombre corregido
import com.SoLinX.dto.UsuarioDto;
import com.SoLinX.model.Usuario;
import com.SoLinX.model.UsuarioEmpresa;
import com.SoLinX.service.UsuarioEmpresaService; // Asegúrate de tener este service o repository
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
    private final UsuarioEmpresaService usuarioEmpresaService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto) {

        List<Usuario> usuarios = usuarioService.getAll();
        Usuario usuarioEncontrado = usuarios.stream()
                .filter(u -> u.getCorreo().equals(loginDto.getCorreo()) &&
                        u.getUserPassword().equals(loginDto.getUserPassword()))
                .findFirst()
                .orElse(null);

        if (usuarioEncontrado != null) {
            LoginResponseDto.LoginResponseDtoBuilder responseBuilder = LoginResponseDto.builder()
                    .idUsuario(usuarioEncontrado.getIdUsuario())
                    .nombre(usuarioEncontrado.getNombre())
                    .correo(usuarioEncontrado.getCorreo())
                    .rol(usuarioEncontrado.getRol());

            if ("empresa".equalsIgnoreCase(usuarioEncontrado.getRol())) {
                List<UsuarioEmpresa> vinculos = usuarioEmpresaService.getAll();

                UsuarioEmpresa vinculo = vinculos.stream()
                        .filter(ue -> ue.getIdUsuario().equals(usuarioEncontrado.getIdUsuario()))
                        .findFirst()
                        .orElse(null);

                if (vinculo != null) {
                    responseBuilder.idEmpresa(vinculo.getIdEmpresa());
                } else {
                    responseBuilder.idEmpresa(0); // No tiene empresa asignada
                }
            }

            return ResponseEntity.ok(responseBuilder.build());
        }

        return ResponseEntity.status(401).build();
    }

    @RequestMapping("/usuario")
    public ResponseEntity<List<UsuarioDto>> lista(@RequestParam (name = "nombre", defaultValue = "", required = false) String nombre) {
        List<Usuario> usuarios = usuarioService.getAll();
        if(usuarios == null || usuarios.size()== 0) {
            return ResponseEntity.notFound().build( );
        } if( nombre != null && !nombre.isEmpty()) {
            return ResponseEntity.ok( usuarios.stream().filter(u -> u.getNombre().equals(nombre) ).map( u -> UsuarioDto.builder().idUsuario(u.getIdUsuario()).nombre(u.getNombre()).correo(u.getCorreo()).telefono(u.getTelefono()).userPassword(u.getUserPassword()).rol(u.getRol()).build()).collect(Collectors.toList()));
        } return ResponseEntity.ok(usuarios.stream().map(u -> UsuarioDto.builder().idUsuario(u.getIdUsuario()).nombre(u.getNombre()).correo(u.getCorreo()).telefono(u.getTelefono()).userPassword(u.getUserPassword()).rol(u.getRol()).build()).collect(Collectors.toList()));
    }

    @RequestMapping("/usuario/{id}")
    public ResponseEntity<UsuarioDto>getById(@PathVariable Integer id) {
        Usuario u = usuarioService.getById(id);
        if(u == null ) return  ResponseEntity.notFound().build();
        return ResponseEntity.ok(UsuarioDto.builder().idUsuario(u.getIdUsuario()).nombre(u.getNombre()).correo(u.getCorreo()).telefono(u.getTelefono()).userPassword(u.getUserPassword()).rol(u.getRol()).build());
    }

    @PostMapping( "/usuario")
    public ResponseEntity<UsuarioDto> save(@RequestBody UsuarioDto usuarioDto) {
        Usuario u = Usuario.builder().nombre( usuarioDto.getNombre()).correo( usuarioDto.getCorreo()).telefono(usuarioDto.getTelefono()).userPassword(usuarioDto.getUserPassword()).rol(usuarioDto.getRol()).build();
        usuarioService.save(u);
        return ResponseEntity.ok(UsuarioDto.builder().nombre(u.getNombre()).correo(u.getCorreo()).telefono(u.getTelefono()).userPassword(u.getUserPassword()).rol(u.getRol()).build());
    }

    @DeleteMapping( "/usuario/{id}")
    public ResponseEntity<UsuarioDto> delete(@PathVariable Integer id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping( "/usuario/{id}")
    public ResponseEntity<UsuarioDto>update( @PathVariable Integer id, @RequestBody UsuarioDto usuarioDto) {
        Usuario aux = usuarioService.update( id, Usuario.builder().idUsuario( usuarioDto.getIdUsuario()).nombre( usuarioDto.getNombre()).correo( usuarioDto.getCorreo()).telefono(usuarioDto.getTelefono()).userPassword(usuarioDto.getUserPassword()).rol(usuarioDto.getRol()).build());
        return ResponseEntity.ok(UsuarioDto.builder().idUsuario(aux.getIdUsuario()).nombre(aux.getNombre()).correo(aux.getCorreo()).telefono(aux.getTelefono()).userPassword(aux.getUserPassword()).rol(aux.getRol()).build());
    }
}
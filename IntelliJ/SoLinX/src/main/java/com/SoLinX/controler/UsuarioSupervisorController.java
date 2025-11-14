package com.SoLinX.controler;

import com.SoLinX.model.UsuarioSupervisor;
import com.SoLinX.service.UsuarioSupervisorService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/usuariosupervisor")
public class UsuarioSupervisorController {

    private final UsuarioSupervisorService service;

    @GetMapping
    public List<UsuarioSupervisor> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public UsuarioSupervisor getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PostMapping
    public UsuarioSupervisor save(@RequestBody UsuarioSupervisor usuarioSupervisor) {
        return service.save(usuarioSupervisor);
    }

    @PutMapping("/{id}")
    public UsuarioSupervisor update(@PathVariable Integer id, @RequestBody UsuarioSupervisor usuarioSupervisor) {
        return service.update(id, usuarioSupervisor);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}

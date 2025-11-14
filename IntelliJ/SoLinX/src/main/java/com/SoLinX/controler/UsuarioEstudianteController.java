package com.SoLinX.controler;

import com.SoLinX.model.UsuarioEstudiante;
import com.SoLinX.service.UsuarioEstudianteService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/usuario-estudiante")
@CrossOrigin("*")
public class UsuarioEstudianteController {

    private final UsuarioEstudianteService usuarioEstudianteService;

    @GetMapping
    public List<UsuarioEstudiante> getAll() {
        return usuarioEstudianteService.getAll();
    }

    @GetMapping("/{id}")
    public UsuarioEstudiante getById(@PathVariable Integer id) {
        return usuarioEstudianteService.getById(id);
    }

    @PostMapping
    public UsuarioEstudiante save(@RequestBody UsuarioEstudiante ue) {
        return usuarioEstudianteService.save(ue);
    }

    @PutMapping("/{id}")
    public UsuarioEstudiante update(@PathVariable Integer id, @RequestBody UsuarioEstudiante ue) {
        return usuarioEstudianteService.update(id, ue);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        usuarioEstudianteService.delete(id);
    }
}

package com.SoLinX.controler;

import com.SoLinX.model.UsuarioEmpresa;
import com.SoLinX.service.UsuarioEmpresaService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/usuario-empresa")
@CrossOrigin("*")
public class UsuarioEmpresaController {

    private final UsuarioEmpresaService usuarioEmpresaService;

    @GetMapping
    public List<UsuarioEmpresa> getAll() {
        return usuarioEmpresaService.getAll();
    }

    @GetMapping("/{id}")
    public UsuarioEmpresa getById(@PathVariable Integer id) {
        return usuarioEmpresaService.getById(id);
    }

    @PostMapping
    public UsuarioEmpresa save(@RequestBody UsuarioEmpresa ue) {
        return usuarioEmpresaService.save(ue);
    }

    @PutMapping("/{id}")
    public UsuarioEmpresa update(@PathVariable Integer id, @RequestBody UsuarioEmpresa ue) {
        return usuarioEmpresaService.update(id, ue);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        usuarioEmpresaService.delete(id);
    }
}

package com.SoLinX.service.impl;

import com.SoLinX.model.UsuarioEmpresa;
import com.SoLinX.repository.UsuarioEmpresaRepository;
import com.SoLinX.service.UsuarioEmpresaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class UsuarioEmpresaServicempl implements UsuarioEmpresaService { // El nombre de la clase 'Servicempl' es inusual, pero lo respeto si así se llama tu archivo.

    private final UsuarioEmpresaRepository usuarioEmpresaRepository;

    @Override
    public List<UsuarioEmpresa> getAll() {
        return usuarioEmpresaRepository.findAll();
    }

    @Override
    public UsuarioEmpresa getById(Integer id) {
        return usuarioEmpresaRepository.findById(id).orElse(null);
    }

    @Override
    public UsuarioEmpresa save(UsuarioEmpresa ue) {
        return usuarioEmpresaRepository.save(ue);
    }

    @Override
    public void delete(Integer id) {
        usuarioEmpresaRepository.deleteById(id);
    }

    @Override
    public UsuarioEmpresa update(Integer id, UsuarioEmpresa ue) {
        // Cambiamos getById(id) por findById(id) que es la forma moderna
        UsuarioEmpresa aux = usuarioEmpresaRepository.findById(id).orElse(null);

        // Si no se encuentra el registro, no podemos actualizar
        if (aux == null) {
            // Aquí podrías lanzar una excepción o simplemente retornar null
            return null;
        }

        // --- CORRECCIÓN ---
        // Asumimos que los campos en tu clase UsuarioEmpresa son 'idUsuario' y 'idEmpresa'
        // (o como se llame el ID de tu modelo Empresa)
        aux.setIdUsuario(ue.getIdUsuario());
        aux.setIdEmpresa(ue.getIdEmpresa()); // <-- Asegúrate de que el método get se llame 'getIdEmpresa'

        return usuarioEmpresaRepository.save(aux);
    }
}
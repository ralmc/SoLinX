package com.SoLinX.service.impl;

import com.SoLinX.model.UsuarioSupervisor;
import com.SoLinX.repository.UsuarioSupervisorRepository;
import com.SoLinX.service.UsuarioSupervisorService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class UsuarioSupervisorServicempl implements UsuarioSupervisorService {

    private final UsuarioSupervisorRepository repository;

    @Override
    public List<UsuarioSupervisor> getAll() {
        return repository.findAll();
    }

    @Override
    public UsuarioSupervisor getById(Integer idUsuario) {
        // findById es el método estándar de JpaRepository
        return repository.findById(idUsuario).orElse(null);
    }

    @Override
    public UsuarioSupervisor save(UsuarioSupervisor usuarioSupervisor) {
        return repository.save(usuarioSupervisor);
    }

    @Override
    public void delete(Integer idUsuario) {
        repository.deleteById(idUsuario);
    }

    @Override
    public UsuarioSupervisor update(Integer idUsuario, UsuarioSupervisor usuarioSupervisor) {
        // 1. Usar findById (forma moderna) en lugar de getById (obsoleto)
        UsuarioSupervisor aux = repository.findById(idUsuario).orElse(null);

        // 2. Es buena práctica verificar si existe antes de actualizar
        if (aux == null) {
            return null; // O lanzar una excepción de "No Encontrado"
        }

        // 3. --- ESTA ES LA CORRECCIÓN ---
        // Los métodos getter y setter usan los nombres de campo (asumiendo 'idSupervisor')
        // El idUsuario (la llave) no se cambia, solo el supervisor asignado.
        aux.setIdSupervisor(usuarioSupervisor.getIdSupervisor());

        return repository.save(aux);
    }
}
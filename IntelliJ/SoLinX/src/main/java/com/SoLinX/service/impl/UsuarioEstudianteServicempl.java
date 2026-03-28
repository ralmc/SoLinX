package com.SoLinX.service.impl;

import com.SoLinX.model.UsuarioEstudiante;
import com.SoLinX.repository.UsuarioEstudianteRepository;
import com.SoLinX.service.UsuarioEstudianteService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class UsuarioEstudianteServicempl implements UsuarioEstudianteService {

    private final UsuarioEstudianteRepository usuarioEstudianteRepository;

    @Override
    public List<UsuarioEstudiante> getAll() {
        return usuarioEstudianteRepository.findAll();
    }

    @Override
    public UsuarioEstudiante getById(Integer id) {
        return usuarioEstudianteRepository.findById(id).orElse(null);
    }

    @Override
    public UsuarioEstudiante save(UsuarioEstudiante ue) {
        return usuarioEstudianteRepository.save(ue);
    }

    @Override
    public void delete(Integer id) {
        usuarioEstudianteRepository.deleteById(id);
    }

    @Override
    public UsuarioEstudiante update(Integer id, UsuarioEstudiante ue) {
        UsuarioEstudiante aux = usuarioEstudianteRepository.findById(id).orElse(null);

        if (aux == null) {
            return null;
        }

        aux.setIdUsuario(ue.getIdUsuario());
        aux.setBoleta(ue.getBoleta());

        return usuarioEstudianteRepository.save(aux);
    }
}
package com.SoLinX.service.impl;

import com.SoLinX.model.Empresa;
import com.SoLinX.repository.EmpresaRepository;
import com.SoLinX.service.EmpresaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class EmpresaServiceImpl implements EmpresaService {
    private final EmpresaRepository EmpresaRepository;

    @Override
    public List<Empresa> getAll() {
        return EmpresaRepository.findAll();
    }

    @Override
    public Empresa getById(Integer id) {
        return EmpresaRepository.findById(id).orElse(null);
    }

    @Override
    public Empresa save(Empresa bEmpresa) {
        return EmpresaRepository.save(bEmpresa);
    }

    @Override
    public void delete(Integer id) {
        EmpresaRepository.deleteById(id);
    }

    @Override
    public Empresa update(Integer id, Empresa bEmpresa) {
        Empresa aux = EmpresaRepository.getById(id);
        aux.setIdEmpresa(bEmpresa.getIdEmpresa());
        aux.setNombreEmpresa(bEmpresa.getNombreEmpresa());
        EmpresaRepository.save(aux);
        return aux;
    }
}

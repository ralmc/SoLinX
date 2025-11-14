package com.SoLinX.service;

import com.SoLinX.model.Empresa;

import java.util.List;

public interface EmpresaService {
    List<Empresa> getAll( );
    Empresa getById(Integer idEmpresa);
    Empresa save(Empresa bEmpresa);
    void delete(Integer idEmpresa);
    Empresa update(Integer idEmpresa, Empresa bEmpresa);
}

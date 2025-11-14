package com.SoLinX.service;

import com.SoLinX.model.UsuarioEmpresa;

import java.util.List;

public interface UsuarioEmpresaService {
    List<UsuarioEmpresa> getAll();
    UsuarioEmpresa getById(Integer id);
    UsuarioEmpresa save(UsuarioEmpresa ue);
    void delete(Integer id);
    UsuarioEmpresa update(Integer id, UsuarioEmpresa ue);
}


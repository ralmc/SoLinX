package com.SoLinX.repository;

import com.SoLinX.model.UsuarioEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioEmpresaRepository extends JpaRepository<UsuarioEmpresa, Integer> {
    UsuarioEmpresa findByIdUsuario(Integer idUsuario);
}
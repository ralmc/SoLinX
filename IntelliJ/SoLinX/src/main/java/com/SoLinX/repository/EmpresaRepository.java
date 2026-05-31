package com.SoLinX.repository;

import com.SoLinX.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {

    @Query(value = """
        SELECT e.* FROM Empresa e
        JOIN UsuarioEmpresa ue ON ue.idEmpresa = e.idEmpresa
        WHERE ue.idUsuario = :idUsuario
        """, nativeQuery = true)
    Optional<Empresa> findByUsuarioEmpresa_IdUsuario(Integer idUsuario);
}
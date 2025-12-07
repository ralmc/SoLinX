package com.SoLinX.repository;

import com.SoLinX.model.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Integer> {
    List<Proyecto> findByEmpresa_IdEmpresa(Integer idEmpresa);
}
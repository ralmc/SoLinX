package com.SoLinX.repository;

import com.SoLinX.model.CambioPerfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CambioPerfilRepository extends JpaRepository<CambioPerfil, Integer> {

    List<CambioPerfil> findByEstadoOrderByFechaSolicitudDesc(String estado);
    List<CambioPerfil> findByUsuario_IdUsuarioOrderByFechaSolicitudDesc(Integer idUsuario);
}
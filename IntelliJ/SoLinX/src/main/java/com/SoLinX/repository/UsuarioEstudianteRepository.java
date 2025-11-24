package com.SoLinX.repository;

import com.SoLinX.model.UsuarioEstudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioEstudianteRepository extends JpaRepository<UsuarioEstudiante, Integer> {

    @Query("SELECT ue FROM UsuarioEstudiante ue WHERE ue.idUsuario = :idUsuario")
    UsuarioEstudiante findByIdUsuario(@Param("idUsuario") Integer idUsuario);

    // NUEVO: Método para buscar si una boleta ya tiene un usuario asociado (para validar registro)
    UsuarioEstudiante findByBoleta(Integer boleta);
}
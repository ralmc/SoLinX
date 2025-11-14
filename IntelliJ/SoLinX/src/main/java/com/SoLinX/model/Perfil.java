package com.SoLinX.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "perfil")
public class Perfil {
    @Id
    @Column(name = "idPerfil")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPerfil;
    @Column(name = "foto")
    private String foto; //Cambiar por Byte[]
    @Column(name = "tema")
    private String tema;
    @Column(name = "idUsuario")
    private Integer idUsuario;
}


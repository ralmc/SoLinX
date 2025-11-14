package com.SoLinX.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "perfil")
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPerfil")
    private Integer idPerfil;

    @Column(name = "foto")
    private String foto;

    @Column(name = "tema")
    private String tema;

    @Column(name = "idUsuario", nullable = false)
    private Integer idUsuario;
}

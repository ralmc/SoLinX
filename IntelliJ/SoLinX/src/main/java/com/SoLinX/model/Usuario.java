package com.SoLinX.model;

import jakarta.persistence.*;
import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
@Entity
@Table(name = "usuario", indexes = {
        @Index(name = "idx_correo", columnList = "correo", unique = true)
})
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUsuario")
    private Integer idUsuario;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "correo", unique = true, nullable = false)
    private String correo;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "userPassword", nullable = false)
    private String userPassword;

    @Column(name = "rol", nullable = false)
    private String rol;

    @Column(name = "verificado", nullable = false)
    private Boolean verificado = false;
}
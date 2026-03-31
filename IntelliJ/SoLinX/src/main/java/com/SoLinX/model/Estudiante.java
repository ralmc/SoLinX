package com.SoLinX.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Estudiante")
public class Estudiante {

    @Id
    @Column(name = "boleta")
    private Integer boleta;

    @Column(name = "carrera")
    private String carrera;

    @Column(name = "escuela")
    private String escuela;

    @OneToOne
    @JoinColumn(name = "boleta", referencedColumnName = "boleta", insertable = false, updatable = false)
    private UsuarioEstudiante usuarioEstudiante;
}
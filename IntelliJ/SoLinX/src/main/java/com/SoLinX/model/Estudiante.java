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

    @Column(name = "carrera", nullable = false)
    private String carrera;

    @Column(name = "escuela", nullable = false)
    private String escuela;
}

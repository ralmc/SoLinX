package com.SoLinX.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
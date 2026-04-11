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
@Table(name = "Empresa")
public class Empresa {

    @Id
    @Column(name = "idEmpresa")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEmpresa;

    @Column(name = "nombreEmpresa")
    private String nombreEmpresa;

    // Aquí agregamos la variable para que coincida con la BD
    @Column(name = "telefono")
    private String telefono;

}
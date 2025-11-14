package com.SoLinX.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "proyecto")
public class Proyecto {

    @Id
    @Column(name = "idProyecto")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProyecto;

    @Column(name = "nombreProyecto")
    private String nombreProyecto;

    @Column(name = "objetivo", columnDefinition = "TEXT") // Para campos TEXT
    private String objetivo;

    @Column(name = "fechaInicio")
    private Timestamp fechaInicio;

    @Column(name = "vacantes")
    private Integer vacantes;

    @Column(name = "ubicacion")
    private String ubicacion;

    @Column(name = "justificacion")
    private String justificacion;

    @Column(name = "fechaTermino")
    private Timestamp fechaTermino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idEmpresa")
    private Empresa idEmpresa;
}
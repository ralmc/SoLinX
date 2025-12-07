package com.SoLinX.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Proyecto")
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idProyecto")
    private Integer idProyecto;

    @Column(name = "carreraEnfocada")
    private String carreraEnfocada;

    @Column(name = "nombreProyecto")
    private String nombreProyecto;

    @Column(name = "objetivo")
    private String objetivo;

    @Column(name = "vacantes")
    private Integer vacantes;

    @Column(name = "ubicacion")
    private String ubicacion;

    @Column(name = "imagenRef")
    private String imagenRef;

    @Column(name = "fechaInicio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInicio;

    @Column(name = "fechaTermino")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaTermino;

    @ManyToOne
    @JoinColumn(name = "idEmpresa", nullable = false)
    private Empresa empresa;
}
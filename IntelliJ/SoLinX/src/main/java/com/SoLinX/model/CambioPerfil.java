package com.SoLinX.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "CambioPerfil")
public class CambioPerfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCambio")
    private Integer idCambio;

    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;

    @Column(name = "rol", nullable = false)
    private String rol;

    @Column(name = "campo", nullable = false)
    private String campo;

    @Column(name = "valorAnterior", nullable = false)
    private String valorAnterior;

    @Column(name = "valorNuevo", nullable = false)
    private String valorNuevo;

    @Column(name = "estado", nullable = false)
    private String estado;

    @Column(name = "fechaSolicitud")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaSolicitud;

    @Column(name = "fechaResolucion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaResolucion;
}
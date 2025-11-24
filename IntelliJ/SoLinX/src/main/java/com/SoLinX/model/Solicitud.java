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
@Table(name = "Solicitud")
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idSolicitud")
    private Integer idSolicitud;

    @Column(name = "fechaSolicitud")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaSolicitud;

    @Column(name = "estadoSolicitud")
    private String estadoSolicitud;

    @ManyToOne
    @JoinColumn(name = "boleta")
    private Estudiante estudiante;

    @ManyToOne
    @JoinColumn(name = "idProyecto")
    private Proyecto proyecto;
}
package com.SoLinX.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "solicitud")
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idSolicitud")
    private Integer idSolicitud;

    @Column(name = "fechaSolicitud")
    private java.sql.Timestamp fechaSolicitud;

    @Column(name = "estadoSolicitud")
    private String estadoSolicitud;

    @Column(name = "boleta")
    private Integer boleta;

    @Column(name = "idProyecto")
    private Integer idProyecto;
}

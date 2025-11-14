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
@Table(name = "solicitud")
public class Solicitud {
    @Id
    @Column(name = "idSolicitud")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSolicitud;
    @Column(name = "fechaSolicitud")
    private String fechaSolicitud;
    @Column(name = "estadoSolicitud")
    private String estadoSolicitud;
    @Column(name = "boleta")
    private Integer boleta;
    @Column(name = "idProyecto")
    private Integer idProyecto;
}


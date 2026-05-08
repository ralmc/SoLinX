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
@Table(name = "Documento")
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idDocumento")
    private Integer idDocumento;

    @Column(name = "periodo", nullable = false)
    private Integer periodo;

    @Lob
    @Column(name = "archivo", nullable = false)
    private byte[] archivo;

    @Column(name = "nombreArchivo", nullable = false)
    private String nombreArchivo;

    @Column(name = "fechaSubida")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaSubida;

    @ManyToOne
    @JoinColumn(name = "boleta", nullable = false)
    private Estudiante estudiante;

    @Column(name = "estadoDocumento", nullable = false)
    private String estadoDocumento = "pendiente";
}
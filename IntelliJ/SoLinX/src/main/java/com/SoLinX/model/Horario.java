package com.SoLinX.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Horario")
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idHorario")
    private Integer idHorario;

    private String lunInicio;
    private String lunFinal;
    private String marInicio;
    private String marFinal;
    private String mierInicio;
    private String mierFinal;
    private String jueInicio;
    private String jueFinal;
    private String vieInicio;
    private String vieFinal;
    private String sabInicio;
    private String sabFinal;
    private String domInicio;
    private String domFinal;
}
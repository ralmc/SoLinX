package com.SoLinX.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "HorarioEstudiante")
public class HorarioEstudiante {

    @Id
    @Column(name = "boleta")
    private Integer boleta;

    @ManyToOne
    @JoinColumn(name = "idHorario", nullable = false)
    private Horario horario;
}
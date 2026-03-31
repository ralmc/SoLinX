package com.SoLinX.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "UsuarioEstudiante")
public class UsuarioEstudiante {

    @Id
    @Column(name = "idUsuario")
    private Integer idUsuario;

    @Column(name = "boleta")
    private Integer boleta;
}

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
@Table(name = "UsuarioSupervisor")
public class UsuarioSupervisor {

    @Id
    @Column(name = "idUsuario")
    private Integer idUsuario;

    @Column(name = "idSupervisor")
    private Integer idSupervisor;
}

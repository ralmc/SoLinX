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
@Table(name = "UsuarioEmpresa")
public class UsuarioEmpresa {

    @Id
    @Column(name = "idUsuario")
    private Integer idUsuario;

    @Column(name = "idEmpresa")
    private Integer idEmpresa;
}

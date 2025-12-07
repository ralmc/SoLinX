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
@Table(name = "supervisor")
public class Supervisor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idSupervisor")
    private Integer idSupervisor;

    @Column(name = "area")
    private String area;

    @Column(name = "idEmpresa", insertable = false, updatable = false)
    private Integer idEmpresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idEmpresa")
    private Empresa empresa;

    public Integer getIdEmpresa() {
        if (this.idEmpresa != null) {
            return this.idEmpresa;
        }
        return empresa != null ? empresa.getIdEmpresa() : null;
    }
}
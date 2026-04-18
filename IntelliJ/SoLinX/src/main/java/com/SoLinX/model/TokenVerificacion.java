package com.SoLinX.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
@Entity
@Table(name = "TokenVerificacion")
public class TokenVerificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idToken")
    private Integer idToken;

    @Column(name = "token", nullable = false, unique = true, length = 36)
    private String token;

    @Column(name = "fechaExpira", nullable = false)
    private LocalDateTime fechaExpira;

    @Column(name = "usado", nullable = false)
    private Boolean usado = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;
}
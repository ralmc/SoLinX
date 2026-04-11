package com.SoLinX.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Respuesta del endpoint GET /proyecto/alumno/{boleta}.
 *
 * Si el alumno ya tiene una solicitud en estado 'aprobada' (visto bueno final),
 * enProyecto = true y proyectoAsignado contiene la info completa de ese proyecto.
 *
 * Si no, enProyecto = false y proyectos contiene la lista de proyectos disponibles,
 * excluyendo los que el alumno ya tiene en proceso.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProyectoAlumnoResponseDto {
    private boolean enProyecto;
    private ProyectoDto proyectoAsignado;
    private String correoEmpresa;
    private List<ProyectoDto> proyectos;
}

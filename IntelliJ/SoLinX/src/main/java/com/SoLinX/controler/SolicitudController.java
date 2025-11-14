package com.SoLinX.controler;

import com.SoLinX.dto.SolicitudDto;
import com.SoLinX.model.Solicitud;
import com.SoLinX.service.SolicitudService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp; // <-- Necesario para la conversión
import java.time.LocalDateTime; // <-- Necesario para la conversión
import java.time.format.DateTimeFormatter; // <-- Necesario para formatear
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/SoLinX/api")
@RestController
@AllArgsConstructor
public class SolicitudController {

    private final SolicitudService solicitudService;

    // --- INICIO DE CAMBIOS ---

    // 1. Define un formateador de fecha. Puedes cambiar el patrón "dd/MM/yyyy HH:mm"
    //    al formato de String que prefieras usar en tu DTO.
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Helper para convertir un Timestamp (de la Entidad) a un String (para el DTO).
     */
    private String formatTimestamp(Timestamp ts) {
        if (ts == null) {
            return null;
        }
        return ts.toLocalDateTime().format(formatter);
    }

    /**
     * Helper para convertir un String (del DTO) a un Timestamp (para la Entidad).
     */
    private Timestamp parseString(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        try {
            LocalDateTime ldt = LocalDateTime.parse(s, formatter);
            return Timestamp.valueOf(ldt);
        } catch (java.time.format.DateTimeParseException e) {
            // Maneja el error si el String del DTO no tiene el formato correcto
            // Por ahora, solo imprimimos el error y retornamos null.
            System.err.println("Error al parsear la fecha: " + s);
            return null;
            // O podrías lanzar una excepción:
            // throw new IllegalArgumentException("Formato de fecha inválido. Usar: " + formatter.toString());
        }
    }

    // 2. He eliminado la lista 'solicitudDtos' y el método 'loadList()'
    //    porque no se estaban usando y parecían ser solo de prueba.

    // --- FIN DE CAMBIOS ---


    @RequestMapping("/solicitud")
    public ResponseEntity<List<SolicitudDto>> lista() {
        List<Solicitud> Solicituds = solicitudService.getAll();
        if(Solicituds == null || Solicituds.size()== 0) {
            return ResponseEntity.notFound( ).build( );
        } return ResponseEntity
                .ok(
                        Solicituds
                                .stream()
                                .map(u -> SolicitudDto.builder()
                                        .idSolicitud(u.getIdSolicitud())
                                        // APLICAMOS LA CONVERSIÓN (Timestamp -> String)
                                        .fechaSolicitud(formatTimestamp(u.getFechaSolicitud())) // <-- CAMBIO
                                        .estadoSolicitud( u.getEstadoSolicitud())
                                        .boleta(u.getBoleta())
                                        .idProyecto(u.getIdProyecto())
                                        .build())
                                .collect(Collectors.toList()));
    }

    @RequestMapping("/solicitud/{id}")
    public ResponseEntity<SolicitudDto>getById(@PathVariable Integer id) {
        Solicitud u = solicitudService.getById(id);

        if(u == null ) {
            return  ResponseEntity.notFound().build();
        } return ResponseEntity.ok(SolicitudDto.builder()
                .idSolicitud(u.getIdSolicitud())
                // APLICAMOS LA CONVERSIÓN (Timestamp -> String)
                .fechaSolicitud(formatTimestamp(u.getFechaSolicitud())) // <-- CAMBIO
                .estadoSolicitud( u.getEstadoSolicitud())
                .boleta(u.getBoleta())
                .idProyecto(u.getIdProyecto())
                .build());
    }

    @PostMapping( "/solicitud")
    public ResponseEntity<SolicitudDto> save(@RequestBody SolicitudDto solicitudDto) {
        Solicitud u = Solicitud.
                builder()
                // APLICAMOS LA CONVERSIÓN INVERSA (String -> Timestamp)
                .fechaSolicitud(parseString(solicitudDto.getFechaSolicitud())) // <-- CAMBIO
                .estadoSolicitud( solicitudDto.getEstadoSolicitud())
                .boleta(solicitudDto.getBoleta())
                .idProyecto(solicitudDto.getIdProyecto())
                .build();
        solicitudService.save(u); // 'u' ahora tiene el ID generado por la DB

        return ResponseEntity.ok(SolicitudDto.builder()
                .idSolicitud(u.getIdSolicitud()) // Devuelve el ID generado
                // APLICAMOS LA CONVERSIÓN (Timestamp -> String)
                .fechaSolicitud(formatTimestamp(u.getFechaSolicitud())) // <-- CAMBIO
                .estadoSolicitud( u.getEstadoSolicitud())
                .boleta(u.getBoleta())
                .idProyecto(u.getIdProyecto())
                .build());
    }

    @DeleteMapping( "/solicitud/{id}")
    public ResponseEntity<SolicitudDto> delete(@PathVariable Integer id) {
        solicitudService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping( "/solicitud/{id}")
    public ResponseEntity<SolicitudDto>update( @PathVariable Integer id, @RequestBody SolicitudDto solicitudDto) {
        Solicitud aux = solicitudService.update( id, Solicitud
                .builder()
                // APLICAMOS LA CONVERSIÓN INVERSA (String -> Timestamp)
                .fechaSolicitud(parseString(solicitudDto.getFechaSolicitud())) // <-- CAMBIO
                .estadoSolicitud( solicitudDto.getEstadoSolicitud())
                .boleta(solicitudDto.getBoleta())
                .idProyecto(solicitudDto.getIdProyecto())
                .build());

        return ResponseEntity.ok(SolicitudDto.builder()
                // APLICAMOS LA CONVERSIÓN (Timestamp -> String)
                .fechaSolicitud(formatTimestamp(aux.getFechaSolicitud())) // <-- CAMBIO
                .estadoSolicitud(aux.getEstadoSolicitud())
                .boleta(aux.getBoleta())
                .idProyecto(aux.getIdProyecto())
                .build());
    }
}
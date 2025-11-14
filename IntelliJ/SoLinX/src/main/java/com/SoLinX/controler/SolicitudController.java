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

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private String formatTimestamp(Timestamp ts) {
        if (ts == null) {
            return null;
        }
        return ts.toLocalDateTime().format(formatter);
    }

    private Timestamp parseString(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        try {
            LocalDateTime ldt = LocalDateTime.parse(s, formatter);
            return Timestamp.valueOf(ldt);
        } catch (java.time.format.DateTimeParseException e) {
            System.err.println("Error al parsear la fecha: " + s);
            return null;
        }
    }

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
                                        .fechaSolicitud(formatTimestamp(u.getFechaSolicitud()))
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
                .fechaSolicitud(formatTimestamp(u.getFechaSolicitud()))
                .estadoSolicitud( u.getEstadoSolicitud())
                .boleta(u.getBoleta())
                .idProyecto(u.getIdProyecto())
                .build());
    }

    @PostMapping( "/solicitud")
    public ResponseEntity<SolicitudDto> save(@RequestBody SolicitudDto solicitudDto) {
        Solicitud u = Solicitud.
                builder()
                .fechaSolicitud(parseString(solicitudDto.getFechaSolicitud()))
                .estadoSolicitud( solicitudDto.getEstadoSolicitud())
                .boleta(solicitudDto.getBoleta())
                .idProyecto(solicitudDto.getIdProyecto())
                .build();
        solicitudService.save(u);

        return ResponseEntity.ok(SolicitudDto.builder()
                .idSolicitud(u.getIdSolicitud())
                .fechaSolicitud(formatTimestamp(u.getFechaSolicitud()))
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
                .fechaSolicitud(parseString(solicitudDto.getFechaSolicitud()))
                .estadoSolicitud( solicitudDto.getEstadoSolicitud())
                .boleta(solicitudDto.getBoleta())
                .idProyecto(solicitudDto.getIdProyecto())
                .build());
        
        if (aux == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(SolicitudDto.builder()
                .idSolicitud(aux.getIdSolicitud())
                .fechaSolicitud(formatTimestamp(aux.getFechaSolicitud()))
                .estadoSolicitud(aux.getEstadoSolicitud())
                .boleta(aux.getBoleta())
                .idProyecto(aux.getIdProyecto())
                .build());
    }
}
package com.SoLinX.controler;

import com.SoLinX.dto.SolicitudDto;
import com.SoLinX.model.Solicitud;
import com.SoLinX.service.SolicitudService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/SoLinX/api")
@RestController
@AllArgsConstructor
public class SolicitudController {
    private final SolicitudService solicitudService;
    private List<SolicitudDto> solicitudDtos;

    public void loadList() {
        solicitudDtos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            solicitudDtos.add(
                    SolicitudDto.builder()
                            .idSolicitud(i++)
                            .fechaSolicitud("Fecha " + i)
                            .estadoSolicitud("Estado " + i)
                            .boleta(i)
                            .idProyecto(i)
                            .build()
            );
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
                                        .fechaSolicitud(u.getFechaSolicitud())
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
                .fechaSolicitud(u.getFechaSolicitud())
                .estadoSolicitud( u.getEstadoSolicitud())
                .boleta(u.getBoleta())
                .idProyecto(u.getIdProyecto())
                .build());
    }

    @PostMapping( "/solicitud")
    public ResponseEntity<SolicitudDto> save(@RequestBody SolicitudDto SolicitudDto) {
        Solicitud u = Solicitud.
                builder()
                        .fechaSolicitud( SolicitudDto.getFechaSolicitud())
                        .estadoSolicitud( SolicitudDto.getEstadoSolicitud())
                        .boleta(SolicitudDto.getBoleta())
                        .idProyecto(SolicitudDto.getIdProyecto())
                .build();
        solicitudService.save(u);
        return ResponseEntity.ok(SolicitudDto.builder()
                        .idSolicitud(u.getIdSolicitud())
                        .fechaSolicitud(u.getFechaSolicitud())
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
    public ResponseEntity<SolicitudDto>update( @PathVariable Integer id, @RequestBody SolicitudDto SolicitudDto) {
        Solicitud aux = solicitudService.update( id, Solicitud
                .builder()
                    .fechaSolicitud( SolicitudDto.getFechaSolicitud())
                    .estadoSolicitud( SolicitudDto.getEstadoSolicitud())
                    .boleta(SolicitudDto.getBoleta())
                    .idProyecto(SolicitudDto.getIdProyecto())
                .build());
        return ResponseEntity.ok(SolicitudDto.builder()
                    .fechaSolicitud(aux.getFechaSolicitud())
                    .estadoSolicitud(aux.getEstadoSolicitud())
                    .boleta(aux.getBoleta())
                    .idProyecto(aux.getIdProyecto())
                .build());
    }
}

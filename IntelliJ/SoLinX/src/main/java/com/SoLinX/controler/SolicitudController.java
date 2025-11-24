package com.SoLinX.controler;

import com.SoLinX.dto.SolicitudDto;
import com.SoLinX.model.Estudiante;
import com.SoLinX.model.Proyecto;
import com.SoLinX.model.Solicitud;
import com.SoLinX.service.SolicitudService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/SoLinX/api")
@RestController
@AllArgsConstructor
public class SolicitudController {

    private final SolicitudService solicitudService;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private String formatDate(Date date) {
        if (date == null) return null;
        Timestamp ts = new Timestamp(date.getTime());
        return ts.toLocalDateTime().format(formatter);
    }

    private Date parseString(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            LocalDateTime ldt = LocalDateTime.parse(s, formatter);
            return Timestamp.valueOf(ldt);
        } catch (Exception e) {
            return new Date();
        }
    }
    @GetMapping("/solicitud/empresa/{idEmpresa}")
    public ResponseEntity<List<SolicitudDto>> listarPorEmpresa(@PathVariable Integer idEmpresa) {

        List<Solicitud> solicitudes = solicitudService.obtenerPorEmpresa(idEmpresa);

        if (solicitudes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<SolicitudDto> dtos = solicitudes.stream()
                .map(s -> SolicitudDto.builder()
                        .idSolicitud(s.getIdSolicitud())
                        .fechaSolicitud(formatDate(s.getFechaSolicitud()))
                        .estadoSolicitud(s.getEstadoSolicitud()) // <--- AQUI SE GUARDA EL ESTADO

                        // Llenamos los datos visuales
                        .nombreProyecto(s.getProyecto() != null ? s.getProyecto().getNombreProyecto() : "Sin Nombre")
                        .carreraAlumno(s.getEstudiante() != null ? s.getEstudiante().getCarrera() : "Sin Carrera")

                        // Llenamos los IDs
                        .boletaAlumno(s.getEstudiante() != null ? s.getEstudiante().getBoleta() : 0)
                        .idProyecto(s.getProyecto() != null ? s.getProyecto().getIdProyecto() : 0)

                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/solicitud")
    public ResponseEntity<List<SolicitudDto>> lista() {
        List<Solicitud> solicitudes = solicitudService.getAll();
        if(solicitudes == null || solicitudes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(solicitudes.stream().map(s -> SolicitudDto.builder()
                .idSolicitud(s.getIdSolicitud())
                .fechaSolicitud(formatDate(s.getFechaSolicitud()))
                .estadoSolicitud(s.getEstadoSolicitud())
                .boletaAlumno(s.getEstudiante().getBoleta())
                .idProyecto(s.getProyecto().getIdProyecto())
                .build()).collect(Collectors.toList()));
    }

    @GetMapping("/solicitud/{id}")
    public ResponseEntity<SolicitudDto> getById(@PathVariable Integer id) {
        Solicitud s = solicitudService.getById(id);
        if(s == null ) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(SolicitudDto.builder()
                .idSolicitud(s.getIdSolicitud())
                .fechaSolicitud(formatDate(s.getFechaSolicitud()))
                .estadoSolicitud(s.getEstadoSolicitud())
                .boletaAlumno(s.getEstudiante().getBoleta())
                .idProyecto(s.getProyecto().getIdProyecto())
                .build());
    }
    @PostMapping("/solicitud")
    public ResponseEntity<SolicitudDto> save(@RequestBody SolicitudDto dto) {

        Estudiante est = new Estudiante();
        est.setBoleta(dto.getBoletaAlumno());

        Proyecto pro = new Proyecto();
        pro.setIdProyecto(dto.getIdProyecto());

        Solicitud s = Solicitud.builder()
                .fechaSolicitud(parseString(dto.getFechaSolicitud()))
                .estadoSolicitud(dto.getEstadoSolicitud())
                .estudiante(est)
                .proyecto(pro)
                .build();

        solicitudService.save(s);

        dto.setIdSolicitud(s.getIdSolicitud());
        return ResponseEntity.ok(dto);
    }
    @PutMapping("/solicitud/{id}")
    public ResponseEntity<SolicitudDto> update(@PathVariable Integer id, @RequestBody SolicitudDto dto) {
        Estudiante est = new Estudiante();
        est.setBoleta(dto.getBoletaAlumno());

        Proyecto pro = new Proyecto();
        pro.setIdProyecto(dto.getIdProyecto());

        Solicitud aux = solicitudService.update(id, Solicitud.builder()
                .fechaSolicitud(parseString(dto.getFechaSolicitud()))
                .estadoSolicitud(dto.getEstadoSolicitud())
                .estudiante(est)
                .proyecto(pro)
                .build());

        if (aux == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(SolicitudDto.builder()
                .idSolicitud(aux.getIdSolicitud())
                .fechaSolicitud(formatDate(aux.getFechaSolicitud()))
                .estadoSolicitud(aux.getEstadoSolicitud())
                .boletaAlumno(aux.getEstudiante().getBoleta())
                .idProyecto(aux.getProyecto().getIdProyecto())
                .build());
    }

    @DeleteMapping("/solicitud/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        solicitudService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // [IMPORTANTE] Endpoint para cambiar estado desde Android (Aceptar/Rechazar)
    @PutMapping("/solicitud/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Integer id, @RequestParam String nuevoEstado) {
        Solicitud s = solicitudService.getById(id);
        if (s != null) {
            s.setEstadoSolicitud(nuevoEstado);
            solicitudService.save(s);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
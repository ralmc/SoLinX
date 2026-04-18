package com.SoLinX.controller;

import com.SoLinX.dto.NotificacionDto;
import com.SoLinX.model.Estudiante;
import com.SoLinX.repository.EstudianteRepository;
import com.SoLinX.service.NotificacionService;
import com.SoLinX.service.RecordatorioService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/SoLinX/api")
@RestController
@AllArgsConstructor
public class RecordatorioController {

    private final RecordatorioService recordatorioService;
    private final NotificacionService notificacionService;
    private final EstudianteRepository estudianteRepository;

    @PostMapping("/recordatorio/documentos")
    public ResponseEntity<String> dispararRecordatorios() {
        recordatorioService.enviarRecordatoriosDocumentosPendientes();
        return ResponseEntity.ok("Recordatorios enviados correctamente.");
    }

    @PostMapping("/recordatorio/alumno/{boleta}")
    public ResponseEntity<NotificacionDto> notificarAlumno(
            @PathVariable("boleta") Integer boleta,
            @RequestBody Map<String, String> body) {

        Estudiante estudiante = estudianteRepository.findById(boleta).orElse(null);
        if (estudiante == null) return ResponseEntity.notFound().build();

        Integer idUsuario = estudiante.getUsuarioEstudiante().getIdUsuario();
        String titulo = body.get("titulo");
        String mensaje = body.get("mensaje");

        NotificacionDto dto = notificacionService.crear(idUsuario, titulo, mensaje);
        return ResponseEntity.ok(dto);
    }
}
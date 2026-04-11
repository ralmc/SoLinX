package com.SoLinX.controller;

import com.SoLinX.dto.AprobacionResponseDto;
import com.SoLinX.dto.SolicitudesResponseDto;
import com.SoLinX.dto.SupervisorResponseDto;
import com.SoLinX.service.SupervisorApproveService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/SoLinX/api/supervisor")
@AllArgsConstructor
public class SupervisorController {

    private final SupervisorApproveService supervisorApproveService;

    @GetMapping("/datos")
    public ResponseEntity<SupervisorResponseDto> getSupervisorData(
            @RequestParam(name = "idUsuario") Integer idUsuario) {
        SupervisorResponseDto response = supervisorApproveService.getSupervisorData(idUsuario);
        return response.getSuccess() ? ResponseEntity.ok(response) : ResponseEntity.status(404).body(response);
    }

    @GetMapping("/solicitudes-enviadas")
    public ResponseEntity<SolicitudesResponseDto> getSolicitudesEnviadas(
            @RequestParam(name = "idSupervisor") Integer idSupervisor) {
        SolicitudesResponseDto response = supervisorApproveService.getSolicitudesEnviadas(idSupervisor);
        return response.getSuccess() ? ResponseEntity.ok(response) : ResponseEntity.status(500).body(response);
    }

    @GetMapping("/solicitudes-aceptadas")
    public ResponseEntity<SolicitudesResponseDto> getSolicitudesAceptadas(
            @RequestParam(name = "idEmpresa") Integer idEmpresa) {
        SolicitudesResponseDto response = supervisorApproveService.getSolicitudesAceptadas(idEmpresa);
        return response.getSuccess() ? ResponseEntity.ok(response) : ResponseEntity.status(500).body(response);
    }

    @PostMapping("/actualizar-solicitud")
    public ResponseEntity<AprobacionResponseDto> actualizarSolicitud(
            @RequestParam(name = "idSolicitud") Integer idSolicitud,
            @RequestParam(name = "nuevoEstado") String nuevoEstado) {
        AprobacionResponseDto response = supervisorApproveService.actualizarSolicitud(idSolicitud, nuevoEstado);
        return response.getSuccess() ? ResponseEntity.ok(response) : ResponseEntity.status(400).body(response);
    }
}
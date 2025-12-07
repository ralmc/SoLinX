package com.SoLinX.controler;

import com.SoLinX.dto.AprobacionResponseDto;
import com.SoLinX.dto.SolicitudesResponseDto;
import com.SoLinX.dto.SupervisorResponseDto;
import com.SoLinX.service.SupervisorApproveService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/SoLinX/api/supervisor")
@RestController
@AllArgsConstructor
public class SupervisorController {

    private final SupervisorApproveService SupervisorApproveService;

    @GetMapping("/datos")
    public ResponseEntity<SupervisorResponseDto> getSupervisorData(@RequestParam Integer idUsuario) {
        SupervisorResponseDto response = SupervisorApproveService.getSupervisorData(idUsuario);

        if (!response.getSuccess()) {
            return ResponseEntity.status(404).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/solicitudes-enviadas")
    public ResponseEntity<SolicitudesResponseDto> getSolicitudesEnviadas(@RequestParam Integer idSupervisor) {
        SolicitudesResponseDto response = SupervisorApproveService.getSolicitudesEnviadas(idSupervisor);

        if (!response.getSuccess()) {
            return ResponseEntity.status(500).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/solicitudes-aceptadas")
    public ResponseEntity<SolicitudesResponseDto> getSolicitudesAceptadas(@RequestParam Integer idEmpresa) {
        SolicitudesResponseDto response = SupervisorApproveService.getSolicitudesAceptadas(idEmpresa);

        if (!response.getSuccess()) {
            return ResponseEntity.status(500).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/actualizar-solicitud")
    public ResponseEntity<AprobacionResponseDto> actualizarSolicitud(
            @RequestParam Integer idSolicitud,
            @RequestParam String nuevoEstado) {

        AprobacionResponseDto response = SupervisorApproveService.actualizarSolicitud(idSolicitud, nuevoEstado);

        if (!response.getSuccess()) {
            return ResponseEntity.status(400).body(response);
        }
        return ResponseEntity.ok(response);
    }
}
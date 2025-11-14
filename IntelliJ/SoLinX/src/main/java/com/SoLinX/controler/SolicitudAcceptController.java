package com.SoLinX.controler;

import com.SoLinX.dto.SolicitudAcceptDto;
import com.SoLinX.model.Solicitud;
import com.SoLinX.service.SolicitudAcceptService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/SoLinX/api")
@RestController
@AllArgsConstructor
public class SolicitudAcceptController {

    private final SolicitudAcceptService solicitudAcceptService;

    @PostMapping("/solicitud/accept")
    public ResponseEntity<Solicitud> aceptar(@RequestBody SolicitudAcceptDto dto) {
        Solicitud solicitud = solicitudAcceptService.aceptar(dto);
        return ResponseEntity.ok(solicitud);
    }
}

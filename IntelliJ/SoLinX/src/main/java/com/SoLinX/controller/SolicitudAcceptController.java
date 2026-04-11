package com.SoLinX.controller;

import com.SoLinX.dto.SolicitudAcceptDto;
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
    public ResponseEntity<Void> aceptar(@RequestBody SolicitudAcceptDto dto) {
        try {
            solicitudAcceptService.aceptar(dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}

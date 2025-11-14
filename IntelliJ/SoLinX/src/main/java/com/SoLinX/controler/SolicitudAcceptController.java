package com.SoLinX.controler;

import com.SoLinX.dto.SolicitudAcceptDto;
import com.SoLinX.model.Solicitud;
import com.SoLinX.service.SolicitudAcceptService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/solicitud/accept")
public class SolicitudAcceptController {

    private final SolicitudAcceptService solicitudAcceptService;

    public SolicitudAcceptController(SolicitudAcceptService solicitudAcceptService) {
        this.solicitudAcceptService = solicitudAcceptService;
    }

    @PostMapping
    public Solicitud aceptar(@RequestBody SolicitudAcceptDto dto) {
        return solicitudAcceptService.aceptar(dto);
    }
}

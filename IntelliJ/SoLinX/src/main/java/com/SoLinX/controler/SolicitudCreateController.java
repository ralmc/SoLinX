package com.SoLinX.controler;

import com.SoLinX.dto.SolicitudCreateDto;
import com.SoLinX.service.SolicitudCreateService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/SoLinX/api")
@RestController
@AllArgsConstructor
public class SolicitudCreateController {

    private final SolicitudCreateService solicitudCreateService;

    @PostMapping("/solicitud/crear")
    public ResponseEntity<SolicitudCreateDto> crear(@RequestBody SolicitudCreateDto dto) {

        SolicitudCreateDto response = solicitudCreateService.crearSolicitud(
                dto.getBoleta(),
                dto.getIdProyecto()
        );

        return ResponseEntity.ok(response);
    }
}

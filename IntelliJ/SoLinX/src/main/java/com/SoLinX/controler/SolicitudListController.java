package com.SoLinX.controler;

import com.SoLinX.dto.SolicitudListDto;
import com.SoLinX.service.SolicitudListService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/SoLinX/api")
@RestController
@AllArgsConstructor
public class SolicitudListController {

    private final SolicitudListService solicitudListService;

    @GetMapping("/solicitud/list/{idEmpresa}")
    public ResponseEntity<List<SolicitudListDto>> listar(@PathVariable Integer idEmpresa) {
        List<SolicitudListDto> lista = solicitudListService.listarSolicitudesDeEmpresa(idEmpresa);
        return ResponseEntity.ok(lista);
    }
}

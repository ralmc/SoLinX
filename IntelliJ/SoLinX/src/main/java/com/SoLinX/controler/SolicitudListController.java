package com.SoLinX.controler;

import com.SoLinX.dto.SolicitudListDto;
import com.SoLinX.service.SolicitudListService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/solicitud/list")
public class SolicitudListController {

    private final SolicitudListService solicitudListService;

    public SolicitudListController(SolicitudListService solicitudListService) {
        this.solicitudListService = solicitudListService;
    }

    @GetMapping("/{idEmpresa}")
    public List<SolicitudListDto> listar(@PathVariable Integer idEmpresa) {
        return solicitudListService.listarSolicitudesDeEmpresa(idEmpresa);
    }
}

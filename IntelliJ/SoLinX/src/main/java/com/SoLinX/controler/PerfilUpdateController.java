package com.SoLinX.controler;

import com.SoLinX.dto.PerfilUpdateDto;
import com.SoLinX.service.PerfilUpdateService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/SoLinX/api")
@RestController
@AllArgsConstructor
public class PerfilUpdateController {

    private final PerfilUpdateService perfilUpdateService;

    @PostMapping("/perfil/update")
    public ResponseEntity<String> actualizarPerfil(@RequestBody PerfilUpdateDto dto) {
        String respuesta = perfilUpdateService.actualizarPerfil(dto);
        return ResponseEntity.ok(respuesta);
    }
}

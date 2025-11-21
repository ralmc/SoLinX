package com.SoLinX.controler;

import com.SoLinX.dto.RegistroEmpresaDTO;
import com.SoLinX.dto.RegistroEmpresaResponseDTO;
import com.SoLinX.service.RegistroEmpresaService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/SoLinX/api")
@RestController
@AllArgsConstructor
public class RegistroEmpresaController {

    private final RegistroEmpresaService registroEmpresaService;

    @PostMapping("/registro/empresa")
    public ResponseEntity<RegistroEmpresaResponseDTO> registrar(@RequestBody RegistroEmpresaDTO dto) {

        RegistroEmpresaResponseDTO response = registroEmpresaService.registrarEmpresa(dto);

        if (response == null) {
            return ResponseEntity.status(409).build();
        }

        return ResponseEntity.ok(response);
    }
}
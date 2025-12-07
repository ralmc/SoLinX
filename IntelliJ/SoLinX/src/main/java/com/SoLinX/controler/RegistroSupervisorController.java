package com.SoLinX.controler;

import com.SoLinX.dto.RegistroSupervisorDTO;
import com.SoLinX.dto.RegistroSupervisorResponseDTO;
import com.SoLinX.service.RegistroSupervisorService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/SoLinX/api")
@RestController
@AllArgsConstructor
public class RegistroSupervisorController {

    private final RegistroSupervisorService registroSupervisorService;

    @PostMapping("/registro/supervisor")
    public ResponseEntity<RegistroSupervisorResponseDTO> registrar(@RequestBody RegistroSupervisorDTO dto) {

        RegistroSupervisorResponseDTO response = registroSupervisorService.registrarSupervisor(dto);

        if (response == null) {
            return ResponseEntity.status(409).build();
        }

        return ResponseEntity.ok(response);
    }
}
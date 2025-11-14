package com.SoLinX.controler;

import com.SoLinX.dto.PasswordUpdateDto;
import com.SoLinX.service.PasswordUpdateService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/SoLinX/api")
@RestController
@AllArgsConstructor
public class PasswordUpdateController {

    private final PasswordUpdateService passwordUpdateService;

    @PostMapping("/password/update")
    public ResponseEntity<String> actualizarPassword(@RequestBody PasswordUpdateDto dto) {
        String respuesta = passwordUpdateService.actualizarPassword(dto);
        return ResponseEntity.ok(respuesta);
    }
}

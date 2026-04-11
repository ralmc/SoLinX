package com.SoLinX.controller;

import com.SoLinX.dto.PasswordUpdateDto;
import com.SoLinX.service.PasswordUpdateService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/SoLinX/api")
@AllArgsConstructor
public class PasswordUpdateController {

    private final PasswordUpdateService passwordUpdateService;

    @PostMapping("/password/update")
    public ResponseEntity<String> actualizarPassword(@RequestBody PasswordUpdateDto dto) {
        return switch (passwordUpdateService.actualizarPassword(dto)) {
            case "OK"                 -> ResponseEntity.ok("Contraseña actualizada");
            case "UsuarioNoExiste"    -> ResponseEntity.status(404).body("Usuario no encontrado");
            case "PasswordIncorrecta" -> ResponseEntity.status(401).body("Contraseña actual incorrecta");
            default                   -> ResponseEntity.status(500).body("Error inesperado");
        };
    }
}
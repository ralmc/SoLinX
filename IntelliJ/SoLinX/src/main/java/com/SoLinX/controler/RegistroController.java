package com.SoLinX.controler;

import com.SoLinX.dto.RegistroDto;
import com.SoLinX.service.RegistroService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/SoLinX/api")
@RestController
@AllArgsConstructor
public class RegistroController {

    private final RegistroService registroService;

    @PostMapping("/registro")
    public ResponseEntity<String> registrar(@RequestBody RegistroDto dto) {
        String resultado = registroService.registrar(dto);

        if (resultado.equals("Registro exitoso")) {
            return ResponseEntity.ok(resultado);
        } else {
            return ResponseEntity.badRequest().body(resultado);
        }
    }
}

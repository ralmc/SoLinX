package com.SoLinX.controller;

import com.SoLinX.dto.LoginDto;
import com.SoLinX.dto.LoginResponseDto;
import com.SoLinX.model.Usuario;
import com.SoLinX.repository.UsuarioRepository;
import com.SoLinX.service.LoginService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/SoLinX/api")
@AllArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto) {
        try {
            LoginResponseDto response = loginService.login(loginDto);

            if (response == null) {
                Usuario usuario = usuarioRepository
                        .findByCorreo(loginDto.getCorreo().trim())
                        .orElse(null);

                if (usuario != null && Boolean.FALSE.equals(usuario.getVerificado())) {
                    return ResponseEntity.status(401)
                            .header("X-Login-Error", "NO_VERIFICADO")
                            .build();
                }

                return ResponseEntity.status(401)
                        .header("X-Login-Error", "CREDENCIALES_INVALIDAS")
                        .build();
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("ERROR EN LOGIN: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}
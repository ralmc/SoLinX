package com.SoLinX.controler;

import com.SoLinX.dto.LoginDto;
import com.SoLinX.dto.LoginResponseDto;
import com.SoLinX.service.LoginService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/SoLinX/api")
@AllArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto) {
        try {
            LoginResponseDto response = loginService.login(loginDto);

            if (response == null) {
                return ResponseEntity.status(401).build();
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("ERROR EN LOGIN: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}
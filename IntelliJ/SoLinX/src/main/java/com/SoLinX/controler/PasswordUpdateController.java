package com.SoLinX.controler;

import com.SoLinX.dto.PasswordUpdateDto;
import com.SoLinX.service.PasswordUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/password")
@RequiredArgsConstructor
public class PasswordUpdateController {

    private final PasswordUpdateService passwordUpdateService;

    @PostMapping("/update")
    public String actualizarPassword(@RequestBody PasswordUpdateDto dto) {
        return passwordUpdateService.actualizarPassword(dto);
    }
}

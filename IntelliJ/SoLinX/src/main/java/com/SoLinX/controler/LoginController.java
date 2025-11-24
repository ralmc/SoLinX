package com.SoLinX.controler;

// ... imports ...

import lombok.AllArgsConstructor;
//ADRIAN- ESTE DABA ERROR Y LO COMENTO PARA QUE ME DEJE FUNCIONAR LO DEMAS, LA OTRA OPCION ERA BORRARLO
// COMENTA ESTAS DOS LÍNEAS (O BORRA EL ARCHIVO)
// @RequestMapping("/SoLinX/api")
// @RestController
@AllArgsConstructor
public class LoginController {

    /* <--- ABRE COMENTARIO AQUÍ PARA BLOQUEAR TODO EL BLOQUE
    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto) {
        LoginResponseDto response = loginService.login(loginDto);

        if (response == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(response);
    }
    CIERRA COMENTARIO AQUÍ ---> */
}
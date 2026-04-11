package com.SoLinX.controller;

import com.SoLinX.model.Usuario;
import com.SoLinX.repository.UsuarioRepository;
import com.SoLinX.service.VerificacionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/SoLinX/api")
@AllArgsConstructor
public class VerificacionController {

    private final VerificacionService verificacionService;
    private final UsuarioRepository usuarioRepository;

    // Abre el navegador cuando el usuario hace clic en el correo
    @GetMapping("/auth/verificar")
    public ResponseEntity<String> verificar(@RequestParam String token) {
        String resultado = verificacionService.verificarToken(token);

        String html = """
            <html>
            <body style="font-family:Arial,sans-serif;text-align:center;
                         padding:60px;background:#f5f5f5;">
                <div style="max-width:400px;margin:auto;background:#fff;padding:40px;
                            border-radius:12px;box-shadow:0 2px 8px rgba(0,0,0,0.1);">
                    <h2 style="color:#1565C0;">SoLinX</h2>
                    <p style="font-size:17px;">%s</p>
                </div>
            </body>
            </html>
            """.formatted(resultado);

        return ResponseEntity.ok()
                .header("Content-Type", "text/html; charset=UTF-8")
                .body(html);
    }

    // Android llama este endpoint desde el diálogo "Reenviar correo"
    @PostMapping("/auth/verificar/reenviar")
    public ResponseEntity<String> reenviar(@RequestParam String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);

        if (usuario == null)
            return ResponseEntity.status(404).body("Usuario no encontrado.");

        if (Boolean.TRUE.equals(usuario.getVerificado()))
            return ResponseEntity.ok("Tu cuenta ya está verificada.");

        verificacionService.generarYEnviarToken(usuario);
        return ResponseEntity.ok("Correo de verificación enviado.");
    }
}
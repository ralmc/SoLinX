package com.SoLinX.controller;

import com.SoLinX.dto.CambioPerfilDto;
import com.SoLinX.service.CambioPerfilService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/SoLinX/api")
@AllArgsConstructor
public class CambioPerfilController {

    private final CambioPerfilService cambioPerfilService;

    // ─── Alumno/Empresa solicita cambio ──────────────────────
    @PostMapping("/cambio-perfil")
    public ResponseEntity<CambioPerfilDto> solicitar(@RequestBody Map<String, String> body) {
        Integer idUsuario    = Integer.parseInt(body.get("idUsuario"));
        String  rol          = body.get("rol");
        String  campo        = body.get("campo");
        String  valorAnterior = body.get("valorAnterior");
        String  valorNuevo   = body.get("valorNuevo");
        return ResponseEntity.ok(
                cambioPerfilService.solicitar(idUsuario, rol, campo, valorAnterior, valorNuevo));
    }

    // ─── Supervisor: ver todos los pendientes ─────────────────
    @GetMapping("/cambio-perfil/pendientes")
    public ResponseEntity<List<CambioPerfilDto>> obtenerPendientes() {
        List<CambioPerfilDto> lista = cambioPerfilService.obtenerPendientes();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    // ─── Alumno/Empresa: ver sus propios cambios ──────────────
    @GetMapping("/cambio-perfil/usuario/{idUsuario}")
    public ResponseEntity<List<CambioPerfilDto>> obtenerPorUsuario(
            @PathVariable Integer idUsuario) {
        List<CambioPerfilDto> lista = cambioPerfilService.obtenerPorUsuario(idUsuario);
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    // ─── Supervisor: aprobar cambio ───────────────────────────
    @PutMapping("/cambio-perfil/{idCambio}/aprobar")
    public ResponseEntity<CambioPerfilDto> aprobar(@PathVariable Integer idCambio) {
        return ResponseEntity.ok(cambioPerfilService.aprobar(idCambio));
    }

    // ─── Supervisor: rechazar cambio ──────────────────────────
    @PutMapping("/cambio-perfil/{idCambio}/rechazar")
    public ResponseEntity<CambioPerfilDto> rechazar(@PathVariable Integer idCambio) {
        return ResponseEntity.ok(cambioPerfilService.rechazar(idCambio));
    }
}
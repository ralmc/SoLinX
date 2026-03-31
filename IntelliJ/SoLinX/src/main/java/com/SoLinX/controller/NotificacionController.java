package com.SoLinX.controller;

import com.SoLinX.dto.NotificacionDto;
import com.SoLinX.service.NotificacionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/SoLinX/api")
@RestController
@AllArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    @GetMapping("/notificacion/usuario/{idUsuario}")
    public ResponseEntity<List<NotificacionDto>> obtenerPorUsuario(
            @PathVariable("idUsuario") Integer idUsuario) {
        List<NotificacionDto> lista = notificacionService.obtenerPorUsuario(idUsuario);
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/notificacion/usuario/{idUsuario}/no-leidas")
    public ResponseEntity<Integer> contarNoLeidas(
            @PathVariable("idUsuario") Integer idUsuario) {
        return ResponseEntity.ok(notificacionService.contarNoLeidas(idUsuario));
    }

    @PostMapping("/notificacion")
    public ResponseEntity<NotificacionDto> crear(@RequestBody Map<String, String> body) {
        Integer idUsuario = Integer.parseInt(body.get("idUsuario"));
        String titulo = body.get("titulo");
        String mensaje = body.get("mensaje");
        return ResponseEntity.ok(notificacionService.crear(idUsuario, titulo, mensaje));
    }

    @PutMapping("/notificacion/{idNotificacion}/leida")
    public ResponseEntity<Void> marcarComoLeida(
            @PathVariable("idNotificacion") Integer idNotificacion) {
        notificacionService.marcarComoLeida(idNotificacion);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/notificacion/usuario/{idUsuario}/leidas")
    public ResponseEntity<Void> marcarTodasComoLeidas(
            @PathVariable("idUsuario") Integer idUsuario) {
        notificacionService.marcarTodasComoLeidas(idUsuario);
        return ResponseEntity.ok().build();
    }
}

package com.SoLinX.scheduler;

import com.SoLinX.model.Usuario;
import com.SoLinX.repository.TokenVerificacionRepository;
import com.SoLinX.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class VerificacionScheduler {

    private final TokenVerificacionRepository tokenRepo;
    private final UsuarioRepository usuarioRepo;

    @Value("${app.verificacion.dias-limpieza-empresa:3}")
    private int diasLimpieza;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void limpiarEmpresasNoVerificadas() {
        LocalDateTime limite = LocalDateTime.now().minusDays(diasLimpieza);
        List<Usuario> vencidos = tokenRepo.findEmpresasNoVerificadasExpiradas(limite);

        if (vencidos.isEmpty()) {
            log.info("[Scheduler] Sin empresas no verificadas que limpiar.");
            return;
        }

        for (Usuario u : vencidos) {
            log.info("[Scheduler] Eliminando empresa no verificada: {}", u.getCorreo());
            tokenRepo.deleteAllByUsuario(u);
            usuarioRepo.delete(u);
        }

        log.info("[Scheduler] {} empresa(s) eliminada(s).", vencidos.size());
    }
}
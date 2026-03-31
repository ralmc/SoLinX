package com.SoLinX.scheduler;

import com.SoLinX.service.RecordatorioService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class RecordatorioScheduler {

    private final RecordatorioService recordatorioService;

    @Scheduled(cron = "0 0 8 * * *")
    public void ejecutarRecordatorios() {
        log.info("Iniciando scheduler de recordatorios...");
        recordatorioService.enviarRecordatoriosDocumentosPendientes();
        log.info("Scheduler finalizado.");
    }
}

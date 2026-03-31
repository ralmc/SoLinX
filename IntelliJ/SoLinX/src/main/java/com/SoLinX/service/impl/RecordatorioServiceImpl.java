package com.SoLinX.service.impl;

import com.SoLinX.model.Estudiante;
import com.SoLinX.repository.DocumentoRepository;
import com.SoLinX.repository.EstudianteRepository;
import com.SoLinX.service.NotificacionService;
import com.SoLinX.service.RecordatorioService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class RecordatorioServiceImpl implements RecordatorioService {

    private final DocumentoRepository documentoRepository;
    private final EstudianteRepository estudianteRepository;
    private final NotificacionService notificacionService;

    @Override
    public void enviarRecordatoriosDocumentosPendientes() {
        Date hace5Dias = Date.from(Instant.now().minus(5, ChronoUnit.DAYS));

        // Caso 1: Alumnos que subieron pero ya pasaron 5 días
        List<Object[]> resultados = documentoRepository.findUltimaFechaSubidaPorEstudiante();
        for (Object[] fila : resultados) {
            Integer boleta = (Integer) fila[0];
            Date ultimaFecha = (Date) fila[1];
            if (ultimaFecha.before(hace5Dias)) {
                enviarRecordatorio(boleta);
            }
        }

        // Caso 2: Alumnos que nunca han subido documento
        List<Estudiante> sinDocumentos = estudianteRepository.findEstudiantesSinDocumentos();
        for (Estudiante estudiante : sinDocumentos) {
            enviarRecordatorio(estudiante.getBoleta());
        }
    }

    private void enviarRecordatorio(Integer boleta) {
        estudianteRepository.findById(boleta).ifPresent(estudiante -> {
            Integer idUsuario = estudiante.getUsuarioEstudiante().getIdUsuario();
            notificacionService.crear(
                    idUsuario,
                    "📄 Recordatorio: Documentos pendientes",
                    "Han pasado más de 5 días sin que subas tu documentación del periodo. " +
                            "Por favor ingresa a la plataforma y sube tus documentos."
            );
            log.info("Recordatorio enviado al alumno con boleta: {}", boleta);
        });
    }
}

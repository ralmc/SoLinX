package com.SoLinX.service.impl;

import com.SoLinX.model.Documento;
import com.SoLinX.model.Estudiante;
import com.SoLinX.repository.DocumentoRepository;
import com.SoLinX.service.DocumentoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Service
public class DocumentoServiceImpl implements DocumentoService {

    private final DocumentoRepository documentoRepository;

    @Override
    public Documento subirDocumento(Integer boleta, Integer periodo, byte[] archivo, String nombreArchivo) {
        if (periodo < 1 || periodo > 8)
            throw new RuntimeException("El periodo debe estar entre 1 y 8");

        Documento existente = documentoRepository
                .findByEstudiante_BoletaAndPeriodo(boleta, periodo).orElse(null);

        if (existente != null) {
            if (!"rechazado".equalsIgnoreCase(existente.getEstadoDocumento())) {
                throw new RuntimeException("Ya existe un documento para el periodo " + periodo);
            }
            // Reemplazar archivo y resetear estado
            existente.setArchivo(archivo);
            existente.setNombreArchivo(nombreArchivo);
            existente.setFechaSubida(new Date());
            existente.setEstadoDocumento("pendiente");
            return documentoRepository.save(existente);
        }

        Estudiante estudiante = new Estudiante();
        estudiante.setBoleta(boleta);
        Documento documento = Documento.builder()
                .periodo(periodo)
                .archivo(archivo)
                .nombreArchivo(nombreArchivo)
                .fechaSubida(new Date())
                .estadoDocumento("pendiente")
                .estudiante(estudiante)
                .build();
        return documentoRepository.save(documento);
    }

    @Override
    public Documento save(Documento documento) {
        return documentoRepository.save(documento);
    }

    @Override
    public Documento actualizarEstado(Integer boleta, Integer periodo, String estado) {
        Documento doc = documentoRepository
                .findByEstudiante_BoletaAndPeriodo(boleta, periodo).orElse(null);
        if (doc == null) return null;
        doc.setEstadoDocumento(estado);
        return documentoRepository.save(doc);
    }

    @Override
    public List<Documento> getByBoleta(Integer boleta) {
        return documentoRepository.findByEstudiante_Boleta(boleta);
    }

    @Override
    public Documento getByBoletaAndPeriodo(Integer boleta, Integer periodo) {
        return documentoRepository.findByEstudiante_BoletaAndPeriodo(boleta, periodo)
                .orElse(null);
    }
}
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

        if (periodo < 1 || periodo > 8) {
            throw new RuntimeException("El periodo debe estar entre 1 y 8");
        }
        if (documentoRepository.existsByEstudiante_BoletaAndPeriodo(boleta, periodo)) {
            throw new RuntimeException("Ya existe un documento para el periodo " + periodo);
        }

        Estudiante estudiante = new Estudiante();
        estudiante.setBoleta(boleta);

        Documento documento = Documento.builder()
                .periodo(periodo)
                .archivo(archivo)
                .nombreArchivo(nombreArchivo)
                .fechaSubida(new Date())
                .estudiante(estudiante)
                .build();

        return documentoRepository.save(documento);
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
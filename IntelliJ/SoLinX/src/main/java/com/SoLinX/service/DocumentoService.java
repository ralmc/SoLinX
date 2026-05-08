package com.SoLinX.service;

import com.SoLinX.model.Documento;

import java.util.List;

public interface DocumentoService {
    Documento subirDocumento(Integer boleta, Integer periodo, byte[] archivo, String nombreArchivo);
    List<Documento> getByBoleta(Integer boleta);
    Documento getByBoletaAndPeriodo(Integer boleta, Integer periodo);
    Documento save(Documento documento);
    Documento actualizarEstado(Integer boleta, Integer periodo, String estado);
}
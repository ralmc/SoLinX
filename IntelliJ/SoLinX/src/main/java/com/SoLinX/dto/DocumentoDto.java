package com.SoLinX.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentoDto {
    private Integer idDocumento;
    private Integer periodo;
    private String nombreArchivo;
    private String fechaSubida;
    private Integer boleta;
    private String estadoDocumento;
}
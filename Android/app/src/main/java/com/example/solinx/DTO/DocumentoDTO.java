package com.example.solinx.DTO;

public class DocumentoDTO {
    private Integer idDocumento;
    private Integer periodo;
    private String nombreArchivo;
    private String fechaSubida;
    private Integer boleta;
    private String estadoDocumento;

    public DocumentoDTO() {}

    public Integer getIdDocumento() { return idDocumento; }
    public void setIdDocumento(Integer idDocumento) { this.idDocumento = idDocumento; }
    public Integer getPeriodo() { return periodo; }
    public void setPeriodo(Integer periodo) { this.periodo = periodo; }
    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }
    public String getFechaSubida() { return fechaSubida; }
    public void setFechaSubida(String fechaSubida) { this.fechaSubida = fechaSubida; }
    public Integer getBoleta() { return boleta; }
    public void setBoleta(Integer boleta) { this.boleta = boleta; }
    public String getEstadoDocumento() { return estadoDocumento; }
    public void setEstadoDocumento(String estadoDocumento) { this.estadoDocumento = estadoDocumento; }
}
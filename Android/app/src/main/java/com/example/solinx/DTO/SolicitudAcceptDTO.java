package com.example.solinx.DTO;

public class SolicitudAcceptDTO {
    private Integer idSolicitud;
    private Boolean aceptado;

    public SolicitudAcceptDTO(Integer idSolicitud, Boolean aceptado) {
        this.idSolicitud = idSolicitud;
        this.aceptado = aceptado;
    }

    public Integer getIdSolicitud() { return idSolicitud; }
    public Boolean getAceptado() { return aceptado; }
    public void setIdSolicitud(Integer idSolicitud) { this.idSolicitud = idSolicitud; }
    public void setAceptado(Boolean aceptado) { this.aceptado = aceptado; }
}

package com.example.solinx.DTO;

public class CambioPerfilDTO {
    private Integer idCambio;
    private Integer idUsuario;
    private String nombreUsuario;
    private String rol;
    private String campo;
    private String valorAnterior;
    private String valorNuevo;
    private String estado;
    private String fechaSolicitud;
    private String fechaResolucion;

    public Integer getIdCambio()       { return idCambio; }
    public Integer getIdUsuario()      { return idUsuario; }
    public String getNombreUsuario()   { return nombreUsuario; }
    public String getRol()             { return rol; }
    public String getCampo()           { return campo; }
    public String getValorAnterior()   { return valorAnterior; }
    public String getValorNuevo()      { return valorNuevo; }
    public String getEstado()          { return estado; }
    public String getFechaSolicitud()  { return fechaSolicitud; }
    public String getFechaResolucion() { return fechaResolucion; }
}
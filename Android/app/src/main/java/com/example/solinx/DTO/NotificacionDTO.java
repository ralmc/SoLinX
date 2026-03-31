package com.example.solinx.DTO;

public class NotificacionDTO {
    private Integer idNotificacion;
    private String titulo;
    private String mensaje;
    private String fechaCreacion;
    private Boolean leida;
    private Integer idUsuario;

    public Integer getIdNotificacion() { return idNotificacion; }
    public String getTitulo() { return titulo; }
    public String getMensaje() { return mensaje; }
    public String getFechaCreacion() { return fechaCreacion; }
    public Boolean getLeida() { return leida; }
    public Integer getIdUsuario() { return idUsuario; }
}

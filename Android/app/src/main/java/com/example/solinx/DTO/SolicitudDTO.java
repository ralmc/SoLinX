package com.example.solinx.DTO;

public class SolicitudDTO {
    private Integer idSolicitud;
    private Integer boleta;
    private Integer boletaAlumno;
    private String nombreProyecto;
    private String nombreEmpresa;
    private String estadoSolicitud;
    private String fechaSolicitud;
    private Integer idProyecto;

    // Constructor vacío
    public SolicitudDTO() {}

    // Getters
    public Integer getIdSolicitud() { return idSolicitud; }
    public Integer getBoleta() { return boleta; }
    public Integer getBoletaAlumno() { return boletaAlumno; }  // 🆕
    public String getNombreProyecto() { return nombreProyecto; }
    public String getNombreEmpresa() { return nombreEmpresa; }
    public String getEstadoSolicitud() { return estadoSolicitud; }
    public String getFechaSolicitud() { return fechaSolicitud; }
    public Integer getIdProyecto() { return idProyecto; }

    // Setters
    public void setIdSolicitud(Integer idSolicitud) { this.idSolicitud = idSolicitud; }
    public void setBoleta(Integer boleta) { this.boleta = boleta; }
    public void setBoletaAlumno(Integer boletaAlumno) { this.boletaAlumno = boletaAlumno; }  // 🆕
    public void setNombreProyecto(String nombreProyecto) { this.nombreProyecto = nombreProyecto; }
    public void setNombreEmpresa(String nombreEmpresa) { this.nombreEmpresa = nombreEmpresa; }
    public void setEstadoSolicitud(String estadoSolicitud) { this.estadoSolicitud = estadoSolicitud; }
    public void setFechaSolicitud(String fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
    public void setIdProyecto(Integer idProyecto) { this.idProyecto = idProyecto; }
}

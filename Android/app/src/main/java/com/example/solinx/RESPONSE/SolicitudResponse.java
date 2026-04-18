package com.example.solinx.RESPONSE;

import java.io.Serializable;

public class SolicitudResponse implements Serializable {
    private Integer idSolicitud;
    private String fechaSolicitud;
    private String nombreProyecto;
    private String carreraAlumno;
    private Integer boletaAlumno;
    private String nombreEstudiante;
    private String correoEstudiante;
    private Integer idProyecto;
    private String estadoSolicitud;

    public Integer getIdSolicitud() { return idSolicitud; }
    public String getFechaSolicitud() { return fechaSolicitud; }
    public String getNombreProyecto() { return nombreProyecto; }
    public String getCarreraAlumno() { return carreraAlumno; }
    public Integer getBoletaAlumno() { return boletaAlumno; }
    public String getNombreEstudiante() { return nombreEstudiante; }
    public String getCorreoEstudiante() { return correoEstudiante; }
    public Integer getIdProyecto() { return idProyecto; }
    public String getEstadoSolicitud() { return estadoSolicitud; }

    public void setIdSolicitud(Integer idSolicitud) { this.idSolicitud = idSolicitud; }
    public void setFechaSolicitud(String fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
    public void setNombreProyecto(String nombreProyecto) { this.nombreProyecto = nombreProyecto; }
    public void setCarreraAlumno(String carreraAlumno) { this.carreraAlumno = carreraAlumno; }
    public void setBoletaAlumno(Integer boletaAlumno) { this.boletaAlumno = boletaAlumno; }
    public void setNombreEstudiante(String nombreEstudiante) { this.nombreEstudiante = nombreEstudiante; }
    public void setCorreoEstudiante(String correoEstudiante) { this.correoEstudiante = correoEstudiante; }
    public void setIdProyecto(Integer idProyecto) { this.idProyecto = idProyecto; }
    public void setEstadoSolicitud(String estadoSolicitud) { this.estadoSolicitud = estadoSolicitud; }
}

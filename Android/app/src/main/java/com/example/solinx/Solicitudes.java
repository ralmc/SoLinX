package com.example.solinx;

import java.io.Serializable;

public class Solicitudes implements Serializable {
    private int idSolicitud;
    private String fechaSolicitud;
    private String fechaAceptacion;
    private String estadoSolicitud;
    private int boleta;
    private String nombreEstudiante;
    private String correoEstudiante;
    private String carrera;
    private String escuela;
    private int idProyecto;
    private String nombreProyecto;
    private String nombreEmpresa;
    private String correoEmpresa;
    private String carreraEnfocada;

    public int getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(int idSolicitud) { this.idSolicitud = idSolicitud; }

    public String getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(String fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public String getFechaAceptacion() { return fechaAceptacion; }
    public void setFechaAceptacion(String fechaAceptacion) { this.fechaAceptacion = fechaAceptacion; }

    public String getEstadoSolicitud() { return estadoSolicitud; }
    public void setEstadoSolicitud(String estadoSolicitud) { this.estadoSolicitud = estadoSolicitud; }

    public int getBoleta() { return boleta; }
    public void setBoleta(int boleta) { this.boleta = boleta; }

    public String getNombreEstudiante() { return nombreEstudiante; }
    public void setNombreEstudiante(String nombreEstudiante) { this.nombreEstudiante = nombreEstudiante; }

    public String getCorreoEstudiante() { return correoEstudiante; }
    public void setCorreoEstudiante(String correoEstudiante) { this.correoEstudiante = correoEstudiante; }

    public String getCarrera() { return carrera; }
    public void setCarrera(String carrera) { this.carrera = carrera; }

    public String getEscuela() { return escuela; }
    public void setEscuela(String escuela) { this.escuela = escuela; }

    public int getIdProyecto() { return idProyecto; }
    public void setIdProyecto(int idProyecto) { this.idProyecto = idProyecto; }

    public String getNombreProyecto() { return nombreProyecto; }
    public void setNombreProyecto(String nombreProyecto) { this.nombreProyecto = nombreProyecto; }

    public String getNombreEmpresa() { return nombreEmpresa; }
    public void setNombreEmpresa(String nombreEmpresa) { this.nombreEmpresa = nombreEmpresa; }

    public String getCorreoEmpresa() { return correoEmpresa; }
    public void setCorreoEmpresa(String correoEmpresa) { this.correoEmpresa = correoEmpresa; }

    public String getCarreraEnfocada() { return carreraEnfocada; }
    public void setCarreraEnfocada(String carreraEnfocada) { this.carreraEnfocada = carreraEnfocada; }
}

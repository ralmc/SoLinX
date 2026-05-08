package com.example.solinx.RESPONSE;

import java.io.Serializable;

public class ProyectoResponse implements Serializable {

    private Integer idProyecto;
    private String carreraEnfocada;
    private String nombreProyecto;
    private String objetivo;
    private Integer vacantes;
    private String ubicacion;
    private String fechaInicio;
    private String fechaTermino;
    private String imagenRef;
    private String imagenProyecto;
    private Integer idEmpresa;
    private String nombreEmpresa;
    private String telefonoEmpresa;
    private String fotoEmpresa;
    private String estadoProyecto;  // pendiente | aprobado | rechazado

    public ProyectoResponse() {}

    public Integer getIdProyecto() { return idProyecto; }
    public String getCarreraEnfocada() { return carreraEnfocada; }
    public String getNombreProyecto() { return nombreProyecto; }
    public String getObjetivo() { return objetivo; }
    public Integer getVacantes() { return vacantes; }
    public String getUbicacion() { return ubicacion; }
    public String getFechaInicio() { return fechaInicio; }
    public String getFechaTermino() { return fechaTermino; }
    public String getImagenRef() { return imagenRef; }
    public String getImagenProyecto() { return imagenProyecto; }
    public Integer getIdEmpresa() { return idEmpresa; }
    public String getNombreEmpresa() { return nombreEmpresa; }
    public String getTelefonoEmpresa() { return telefonoEmpresa; }
    public String getFotoEmpresa() { return fotoEmpresa; }
    public String getEstadoProyecto() { return estadoProyecto; }

    public void setIdProyecto(Integer idProyecto) { this.idProyecto = idProyecto; }
    public void setCarreraEnfocada(String c) { this.carreraEnfocada = c; }
    public void setNombreProyecto(String n) { this.nombreProyecto = n; }
    public void setObjetivo(String o) { this.objetivo = o; }
    public void setVacantes(Integer v) { this.vacantes = v; }
    public void setUbicacion(String u) { this.ubicacion = u; }
    public void setFechaInicio(String f) { this.fechaInicio = f; }
    public void setFechaTermino(String f) { this.fechaTermino = f; }
    public void setImagenRef(String i) { this.imagenRef = i; }
    public void setImagenProyecto(String i) { this.imagenProyecto = i; }
    public void setIdEmpresa(Integer i) { this.idEmpresa = i; }
    public void setNombreEmpresa(String n) { this.nombreEmpresa = n; }
    public void setTelefonoEmpresa(String t) { this.telefonoEmpresa = t; }
    public void setFotoEmpresa(String f) { this.fotoEmpresa = f; }
    public void setEstadoProyecto(String e) { this.estadoProyecto = e; }

    public String getFechasFormateadas() {
        if (fechaInicio != null && fechaTermino != null)
            return "Fecha: " + fechaInicio + " - " + fechaTermino;
        else if (fechaInicio != null)
            return "Fecha Inicio: " + fechaInicio;
        return "Fechas no disponibles";
    }
}
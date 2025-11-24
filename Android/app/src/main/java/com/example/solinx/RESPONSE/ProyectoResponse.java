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

    private Integer idEmpresa;
    private String nombreEmpresa;

    // ================= GETTERS =================
    public Integer getIdProyecto() { return idProyecto; }
    public String getCarreraEnfocada() { return carreraEnfocada; }
    public String getNombreProyecto() { return nombreProyecto; }
    public String getObjetivo() { return objetivo; }
    public Integer getVacantes() { return vacantes; }
    public String getUbicacion() { return ubicacion; }
    public String getFechaInicio() { return fechaInicio; }
    public String getFechaTermino() { return fechaTermino; }
    public String getImagenRef() { return imagenRef; }
    public Integer getIdEmpresa() { return idEmpresa; }
    public String getNombreEmpresa() { return nombreEmpresa; }

    // ================= SETTERS =================
    public void setIdProyecto(Integer idProyecto) { this.idProyecto = idProyecto; }
    public void setCarreraEnfocada(String carreraEnfocada) { this.carreraEnfocada = carreraEnfocada; }
    public void setNombreProyecto(String nombreProyecto) { this.nombreProyecto = nombreProyecto; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }
    public void setVacantes(Integer vacantes) { this.vacantes = vacantes; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }
    public void setFechaTermino(String fechaTermino) { this.fechaTermino = fechaTermino; }
    public void setImagenRef(String imagenRef) { this.imagenRef = imagenRef; }

    public void setIdEmpresa(Integer idEmpresa) { this.idEmpresa = idEmpresa; }

    public void setNombreEmpresa(String nombreEmpresa) { this.nombreEmpresa = nombreEmpresa; }
}
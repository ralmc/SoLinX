package com.example.solinx.RESPONSE;

import java.io.Serializable;

public class SolicitudResponse implements Serializable {
    private Integer idSolicitud;
    private String fechaSolicitud;
    private String nombreProyecto;
    private String carreraAlumno;
    private Integer boletaAlumno;
    private Integer idProyecto;
    private String estadoSolicitud;

    public Integer getIdSolicitud() { return idSolicitud; }
    public String getFechaSolicitud() { return fechaSolicitud; }
    public String getNombreProyecto() { return nombreProyecto; }
    public String getCarreraAlumno() { return carreraAlumno; }
    public Integer getBoletaAlumno() { return boletaAlumno; }
    public Integer getIdProyecto() { return idProyecto; }
    public String getEstadoSolicitud() { return estadoSolicitud; }

    // Si más adelante necesitas Setters, puedes agregarlos
}
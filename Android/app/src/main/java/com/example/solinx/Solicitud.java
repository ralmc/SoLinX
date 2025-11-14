package com.example.solinx;

public class Solicitud {
    String id;
    String nombreAlumno;
    String boleta;
    String nombreEmpresa;
    String tipoServicio;
    String carrera;
    String horario;

    public Solicitud(String id, String nombreAlumno, String boleta, String nombreEmpresa, String tipoServicio, String carrera, String horario) {
        this.id = id;
        this.nombreAlumno = nombreAlumno;
        this.boleta = boleta;
        this.nombreEmpresa = nombreEmpresa;
        this.tipoServicio = tipoServicio;
        this.carrera = carrera;
        this.horario = horario;
    }
    public String getId() { return id; }
    public String getNombreAlumno() { return nombreAlumno; }
    public String getBoleta() { return boleta; }
    public String getNombreEmpresa() { return nombreEmpresa; }
    public String getTipoServicio() { return tipoServicio; }
    public String getCarrera() { return carrera; }
    public String getHorario() { return horario; }
}
package com.example.solinx;

import java.io.Serializable;

public class Supervisor implements Serializable {
    private int idSupervisor;
    private int idUsuario;
    private String nombre;
    private String correo;
    private String telefono;
    private String area;
    private int idEmpresa;
    private String nombreEmpresa;

    public Supervisor() {}

    // Getters y Setters
    public int getIdSupervisor() { return idSupervisor; }
    public void setIdSupervisor(int idSupervisor) { this.idSupervisor = idSupervisor; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public int getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(int idEmpresa) { this.idEmpresa = idEmpresa; }

    public String getNombreEmpresa() { return nombreEmpresa; }
    public void setNombreEmpresa(String nombreEmpresa) { this.nombreEmpresa = nombreEmpresa; }
}
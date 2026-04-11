package com.example.solinx.DTO;

import java.io.Serializable;

public class EmpresaDTO implements Serializable {
    private int idEmpresa;
    private String nombreEmpresa;
    private String telefono;
    private String correo;

    public int getIdEmpresa() { return idEmpresa; }
    public String getNombreEmpresa() { return nombreEmpresa; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }

    public void setIdEmpresa(int idEmpresa) { this.idEmpresa = idEmpresa; }
    public void setNombreEmpresa(String nombreEmpresa) { this.nombreEmpresa = nombreEmpresa; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setCorreo(String correo) { this.correo = correo; }
}

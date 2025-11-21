package com.example.solinx.DTO;

public class RegistroEmpresaDTO {
    private String nombreEmpresa;
    private String correo;
    private String telefono;
    private String userPassword;

    public RegistroEmpresaDTO(String nombreEmpresa, String correo, String telefono, String userPassword) {
        this.nombreEmpresa = nombreEmpresa;
        this.correo = correo;
        this.telefono = telefono;
        this.userPassword = userPassword;
    }

    public String getNombreEmpresa() { return nombreEmpresa; }
    public String getCorreo() { return correo; }
    public String getTelefono() { return telefono; }
    public String getUserPassword() { return userPassword; }
}
package com.example.solinx.DTO;

public class RegistroEmpresaResponseDTO {
    private Integer idUsuario;
    private String nombre;
    private String correo;
    private String rol;
    private Integer idEmpresa;
    private String nombreEmpresa;

    public Integer getIdUsuario() { return idUsuario; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getRol() { return rol; }
    public Integer getIdEmpresa() { return idEmpresa; }
    public String getNombreEmpresa() { return nombreEmpresa; }
}
package com.example.solinx.DTO;

public class RegistroSupervisorResponseDTO {
    private Integer idUsuario;
    private String nombre;
    private String correo;
    private String rol;
    private Integer idSupervisor;
    private String area;
    private Integer idEmpresa;

    public Integer getIdUsuario() { return idUsuario; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getRol() { return rol; }
    public Integer getIdSupervisor() { return idSupervisor; }
    public String getArea() { return area; }
    public Integer getIdEmpresa() { return idEmpresa; }
}
package com.example.solinx.DTO;

public class LoginResponseDTO {
    private Integer idUsuario;
    private String nombre;
    private String correo;
    private String rol;
    private String tipoUsuario;

    private Integer idEmpresa;

    public Integer getIdUsuario() { return idUsuario; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getRol() { return rol; }
    public String getTipoUsuario() { return tipoUsuario; }

    public Integer getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(Integer idEmpresa) { this.idEmpresa = idEmpresa; }
}
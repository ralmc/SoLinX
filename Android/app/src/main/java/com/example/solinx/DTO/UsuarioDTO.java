package com.example.solinx.DTO;

public class UsuarioDTO {
    private Integer idUsuario;
    private String nombre;
    private String correo;
    private String telefono;
    private String rol;

    public Integer getIdUsuario() { return idUsuario; }
    public String getNombre()     { return nombre; }
    public String getCorreo()     { return correo; }
    public String getTelefono()   { return telefono; }
    public String getRol()        { return rol; }
}
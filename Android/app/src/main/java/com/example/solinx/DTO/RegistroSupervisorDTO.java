package com.example.solinx.DTO;

public class RegistroSupervisorDTO {
    private String nombreSupervisor;
    private String area;
    private Integer idEmpresa;
    private String correo;
    private String telefono;
    private String userPassword;

    public RegistroSupervisorDTO(String nombreSupervisor, String area, Integer idEmpresa,
                                 String correo, String telefono, String userPassword) {
        this.nombreSupervisor = nombreSupervisor;
        this.area = area;
        this.idEmpresa = idEmpresa;
        this.correo = correo;
        this.telefono = telefono;
        this.userPassword = userPassword;
    }

    public String getNombreSupervisor() { return nombreSupervisor; }
    public String getArea() { return area; }
    public Integer getIdEmpresa() { return idEmpresa; }
    public String getCorreo() { return correo; }
    public String getTelefono() { return telefono; }
    public String getUserPassword() { return userPassword; }
}
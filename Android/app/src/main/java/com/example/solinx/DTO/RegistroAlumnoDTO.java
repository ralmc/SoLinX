package com.example.solinx.DTO;

public class RegistroAlumnoDTO {
    private String nombreUsuario;
    private Integer boleta;
    private String carrera;
    private String escuela;
    private String correo;
    private String confirmarCorreo;
    private String contraseña;
    private String confirmarContraseña;

    public RegistroAlumnoDTO(String nombreUsuario, Integer boleta, String carrera, String escuela,
                             String correo, String confirmarCorreo, String contraseña, String confirmarContraseña) {
        this.nombreUsuario = nombreUsuario;
        this.boleta = boleta;
        this.carrera = carrera;
        this.escuela = escuela;
        this.correo = correo;
        this.confirmarCorreo = confirmarCorreo;
        this.contraseña = contraseña;
        this.confirmarContraseña = confirmarContraseña;
    }

    // Getters
    public String getNombreUsuario() { return nombreUsuario; }
    public Integer getBoleta() { return boleta; }
    public String getCarrera() { return carrera; }
    public String getEscuela() { return escuela; }
    public String getCorreo() { return correo; }
    public String getConfirmarCorreo() { return confirmarCorreo; }
    public String getContraseña() { return contraseña; }
    public String getConfirmarContraseña() { return confirmarContraseña; }

    // Setters
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public void setBoleta(Integer boleta) { this.boleta = boleta; }
    public void setCarrera(String carrera) { this.carrera = carrera; }
    public void setEscuela(String escuela) { this.escuela = escuela; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setConfirmarCorreo(String confirmarCorreo) { this.confirmarCorreo = confirmarCorreo; }
    public void setContraseña(String contraseña) { this.contraseña = contraseña; }
    public void setConfirmarContraseña(String confirmarContraseña) { this.confirmarContraseña = confirmarContraseña; }
}

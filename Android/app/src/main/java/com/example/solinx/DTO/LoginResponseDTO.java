package com.example.solinx.DTO;

public class LoginResponseDTO {
    private Integer idUsuario;
    private String nombre;
    private String correo;
    private String rol;
    private String tipoUsuario;
    private Integer idEmpresa;

    private Integer idSupervisor;
    private String area;

    private Integer boleta;
    private String carrera;
    private String escuela;
    private String telefono;

    // ============================================
    // GETTERS Y SETTERS BÁSICOS
    // ============================================

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public Integer getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(Integer idEmpresa) { this.idEmpresa = idEmpresa; }

    // ============================================
    // GETTERS Y SETTERS SUPERVISOR ✅ NUEVO
    // ============================================

    public Integer getIdSupervisor() { return idSupervisor; }
    public void setIdSupervisor(Integer idSupervisor) { this.idSupervisor = idSupervisor; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    // ============================================
    // GETTERS Y SETTERS ESTUDIANTE
    // ============================================

    public Integer getBoleta() { return boleta; }
    public void setBoleta(Integer boleta) { this.boleta = boleta; }

    public String getCarrera() { return carrera; }
    public void setCarrera(String carrera) { this.carrera = carrera; }

    public String getEscuela() { return escuela; }
    public void setEscuela(String escuela) { this.escuela = escuela; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
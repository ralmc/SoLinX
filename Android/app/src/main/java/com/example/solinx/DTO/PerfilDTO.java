package com.example.solinx.DTO;

public class PerfilDTO {
    private Integer idPerfil;
    private String foto;
    private String tema;
    private Integer idUsuario;

    public PerfilDTO() {}

    public Integer getIdPerfil() { return idPerfil; }
    public String getFoto() { return foto; }
    public String getTema() { return tema; }
    public Integer getIdUsuario() { return idUsuario; }

    public void setIdPerfil(Integer idPerfil) { this.idPerfil = idPerfil; }
    public void setFoto(String foto) { this.foto = foto; }
    public void setTema(String tema) { this.tema = tema; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
}

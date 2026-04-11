package com.example.solinx.RESPONSE;

import java.util.List;

public class ProyectoAlumnoResponse {
    private boolean enProyecto;
    private ProyectoResponse proyectoAsignado;
    private String correoEmpresa;
    private List<ProyectoResponse> proyectos;

    public ProyectoAlumnoResponse() {}

    public boolean isEnProyecto() { return enProyecto; }
    public void setEnProyecto(boolean enProyecto) { this.enProyecto = enProyecto; }

    public ProyectoResponse getProyectoAsignado() { return proyectoAsignado; }
    public void setProyectoAsignado(ProyectoResponse proyectoAsignado) { this.proyectoAsignado = proyectoAsignado; }

    public String getCorreoEmpresa() { return correoEmpresa; }
    public void setCorreoEmpresa(String correoEmpresa) { this.correoEmpresa = correoEmpresa; }

    public List<ProyectoResponse> getProyectos() { return proyectos; }
    public void setProyectos(List<ProyectoResponse> proyectos) { this.proyectos = proyectos; }
}

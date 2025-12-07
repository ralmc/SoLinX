package com.example.solinx.RESPONSE;

import com.example.solinx.Solicitudes;
import java.util.List;

public class SolicitudesResponse {
    private boolean success;
    private String message;
    private List<Solicitudes> solicitudes;

    public SolicitudesResponse() {}

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<Solicitudes> getSolicitudes() { return solicitudes; }  // ← CAMBIO AQUÍ
    public void setSolicitudes(List<Solicitudes> solicitudes) { this.solicitudes = solicitudes; }  // ← CAMBIO AQUÍ
}
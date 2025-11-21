package com.example.solinx.REQUEST;

public class LoginRequest {
    private String correo;
    private String userPassword;

    public LoginRequest(String correo, String userPassword) {
        this.correo = correo;
        this.userPassword = userPassword;
    }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getUserPassword() { return userPassword; }
    public void setUserPassword(String userPassword) { this.userPassword = userPassword; }
}

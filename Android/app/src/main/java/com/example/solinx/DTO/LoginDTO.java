package com.example.solinx.DTO;

public class LoginDTO {
    private String correo;
    private String userPassword;

    public LoginDTO(String correo, String userPassword) {
        this.correo = correo;
        this.userPassword = userPassword;
    }

    public String getCorreo() {
        return correo;
    }

    public String getUserPassword() {
        return userPassword;
    }
}

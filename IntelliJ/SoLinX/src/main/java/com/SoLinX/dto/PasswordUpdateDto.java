package com.SoLinX.dto;

import lombok.Data;

@Data
public class PasswordUpdateDto {
    private String correo;
    private String oldPassword;
    private String newPassword;
}

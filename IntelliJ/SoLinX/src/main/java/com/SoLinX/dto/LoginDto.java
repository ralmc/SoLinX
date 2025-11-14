package com.SoLinX.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginDto {
    private String correo;
    private String userPassword;
}

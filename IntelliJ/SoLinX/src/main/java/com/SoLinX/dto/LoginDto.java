package com.SoLinX.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor //AÑADIDO
@AllArgsConstructor //AÑADIDO
public class LoginDto {
    private String correo;
    private String userPassword;
}

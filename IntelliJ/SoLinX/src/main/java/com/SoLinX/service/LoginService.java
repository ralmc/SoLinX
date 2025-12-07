package com.SoLinX.service;

import com.SoLinX.dto.LoginDto;
import com.SoLinX.dto.LoginResponseDto;

public interface LoginService {
    LoginResponseDto login(LoginDto loginDto);
}

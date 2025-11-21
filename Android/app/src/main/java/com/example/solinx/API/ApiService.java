package com.example.solinx.API;

import com.example.solinx.DTO.LoginDTO;
import com.example.solinx.DTO.LoginResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("login")
    Call<LoginResponseDTO> login(@Body LoginDTO loginDto);
}
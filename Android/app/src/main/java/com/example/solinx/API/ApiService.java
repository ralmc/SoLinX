package com.example.solinx.API;

import com.example.solinx.DTO.LoginDTO;
import com.example.solinx.DTO.LoginResponseDTO;
import com.example.solinx.DTO.RegistroAlumnoDTO;
import com.example.solinx.DTO.RegistroEmpresaDTO;
import com.example.solinx.DTO.RegistroEmpresaResponseDTO;
import com.example.solinx.RESPONSE.ProyectoResponse;
import com.example.solinx.RESPONSE.SolicitudResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("login")
    Call<LoginResponseDTO> login(@Body LoginDTO loginDto);

    @POST("registro/empresa")
    Call<RegistroEmpresaResponseDTO> registrarEmpresa(@Body RegistroEmpresaDTO dto);

    @POST("registro")
    Call<String> registrarAlumno(@Body RegistroAlumnoDTO dto);

    @GET("proyecto")
    Call<List<ProyectoResponse>> obtenerProyectos();

    @GET("proyecto/empresa/{id}")
    Call<List<ProyectoResponse>> obtenerProyectosPorEmpresa(@Path("id") int idEmpresa);

    @POST("proyecto")
    Call<ProyectoResponse> crearProyecto(@Body ProyectoResponse proyecto);

    @PUT("proyecto/{id}")
    Call<ProyectoResponse> actualizarProyecto(@Path("id") int id, @Body ProyectoResponse proyecto);

    @DELETE("proyecto/{id}")
    Call<Void> eliminarProyecto(@Path("id") int id);

    @GET("solicitud/empresa/{idEmpresa}")
    Call<List<SolicitudResponse>> obtenerSolicitudesPorEmpresa(@Path("idEmpresa") int idEmpresa);

    @PUT("solicitud/{idSolicitud}/estado")
    Call<Void> actualizarEstadoSolicitud(
            @Path("idSolicitud") int idSolicitud,
            @Query("nuevoEstado") String nuevoEstado);
}
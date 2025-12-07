package com.example.solinx.API;

import com.example.solinx.DTO.LoginDTO;
import com.example.solinx.DTO.LoginResponseDTO;
import com.example.solinx.DTO.RegistroAlumnoDTO;
import com.example.solinx.DTO.RegistroEmpresaDTO;
import com.example.solinx.DTO.RegistroEmpresaResponseDTO;

// Importes de la versión upstream
<<<<<<< HEAD
import com.example.solinx.DTO.RegistroSupervisorDTO;
import com.example.solinx.DTO.RegistroSupervisorResponseDTO;
=======
import com.example.solinx.DTO.SolicitudDTO;
>>>>>>> 98e00f9a5b902558cbc469c99ea9c0558e702bf4
import com.example.solinx.RESPONSE.ProyectoResponse;
import com.example.solinx.RESPONSE.SolicitudResponse;

// Importes de la versión stashed
import com.example.solinx.RESPONSE.AprobacionResponse;
import com.example.solinx.RESPONSE.SolicitudesResponse;
import com.example.solinx.RESPONSE.SupervisorResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;

public interface ApiService {

    // -------------------------
    // Autenticación y registro
    // -------------------------

    @POST("login")
    Call<LoginResponseDTO> login(@Body LoginDTO loginDto);

    @POST("registro/empresa")
    Call<RegistroEmpresaResponseDTO> registrarEmpresa(@Body RegistroEmpresaDTO dto);

    @POST("registro")
    Call<String> registrarAlumno(@Body RegistroAlumnoDTO dto);

    // -------------------------
    // CRUD de Proyectos
    // -------------------------

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

    // -------------------------
    // Solicitudes por Empresa
    // -------------------------

    @GET("solicitud/empresa/{idEmpresa}")
    Call<List<SolicitudResponse>> obtenerSolicitudesPorEmpresa(@Path("idEmpresa") int idEmpresa);

    @PUT("solicitud/{idSolicitud}/estado")
    Call<Void> actualizarEstadoSolicitud(
            @Path("idSolicitud") int idSolicitud,
            @Query("nuevoEstado") String nuevoEstado
    );

    // -------------------------
    // Datos del Supervisor
    // -------------------------

    @GET("supervisor/datos")
    Call<SupervisorResponse> getSupervisorData(
            @Query("idUsuario") int idUsuario
    );

    @GET("supervisor/solicitudes-enviadas")
    Call<SolicitudesResponse> getSolicitudesEnviadas(
            @Query("idSupervisor") int idSupervisor
    );

    @GET("supervisor/solicitudes-aceptadas")
    Call<SolicitudesResponse> getSolicitudesAceptadas(
            @Query("idEmpresa") int idEmpresa
    );

    @FormUrlEncoded
    @POST("supervisor/actualizar-solicitud")
    Call<AprobacionResponse> actualizarSolicitud(
            @Field("idSolicitud") int idSolicitud,
            @Field("nuevoEstado") String nuevoEstado
    );
<<<<<<< HEAD

    @POST("registro/supervisor")
    Call<RegistroSupervisorResponseDTO> registrarSupervisor(@Body RegistroSupervisorDTO dto);
=======
    @GET("solicitudes/estudiante/{boleta}")
    Call<List<SolicitudDTO>> obtenerSolicitudesEstudiante(@Path("boleta") Integer boleta);

    // Enviar solicitud
    @POST("solicitud")
    Call<SolicitudDTO> enviarSolicitud(@Body SolicitudDTO solicitudDTO);
>>>>>>> 98e00f9a5b902558cbc469c99ea9c0558e702bf4
}

package com.example.solinx.API;

import com.example.solinx.DTO.DocumentoDTO;
import com.example.solinx.DTO.LoginDTO;
import com.example.solinx.DTO.LoginResponseDTO;
import com.example.solinx.DTO.NotificacionDTO;
import com.example.solinx.DTO.PerfilDTO;
import com.example.solinx.DTO.RegistroAlumnoDTO;
import com.example.solinx.DTO.RegistroEmpresaDTO;
import com.example.solinx.DTO.RegistroEmpresaResponseDTO;
import com.example.solinx.DTO.HorarioDTO;
import com.example.solinx.DTO.SolicitudAcceptDTO;
import com.example.solinx.DTO.SolicitudDTO;
import com.example.solinx.RESPONSE.ProyectoResponse;
import com.example.solinx.RESPONSE.SolicitudResponse;
import com.example.solinx.RESPONSE.AprobacionResponse;
import com.example.solinx.RESPONSE.SolicitudesResponse;
import com.example.solinx.RESPONSE.SupervisorResponse;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // ── Auth ──────────────────────────────────────────────────────────────────
    @POST("login")
    Call<LoginResponseDTO> login(@Body LoginDTO loginDto);

    @POST("auth/verificar/reenviar")
    Call<String> reenviarVerificacion(@Query("correo") String correo);

    // ── Registro ──────────────────────────────────────────────────────────────
    @POST("registro/empresa")
    Call<RegistroEmpresaResponseDTO> registrarEmpresa(@Body RegistroEmpresaDTO dto);

    @POST("registro")
    Call<String> registrarAlumno(@Body RegistroAlumnoDTO dto);

    @POST("horario/{boleta}")
    Call<HorarioDTO> crearHorario(@Path("boleta") int boleta, @Body HorarioDTO dto);

    // ── Proyectos ─────────────────────────────────────────────────────────────
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

    // ── Solicitudes ───────────────────────────────────────────────────────────
    @GET("solicitud/empresa/{idEmpresa}")
    Call<List<SolicitudResponse>> obtenerSolicitudesPorEmpresa(@Path("idEmpresa") int idEmpresa);

    @PUT("solicitud/{idSolicitud}/estado")
    Call<Void> actualizarEstadoSolicitud(
            @Path("idSolicitud") int idSolicitud,
            @Query("nuevoEstado") String nuevoEstado
    );

    @GET("solicitudes/estudiante/{boleta}")
    Call<List<SolicitudDTO>> obtenerSolicitudesEstudiante(@Path("boleta") Integer boleta);

    @POST("solicitud")
    Call<SolicitudDTO> enviarSolicitud(@Body SolicitudDTO solicitudDTO);

    @POST("solicitud/accept")
    Call<Void> aceptarSolicitud(@Body SolicitudAcceptDTO dto);

    // ── Supervisor ────────────────────────────────────────────────────────────
    @GET("supervisor/datos")
    Call<SupervisorResponse> getSupervisorData(@Query("idUsuario") int idUsuario);

    @GET("supervisor/solicitudes-enviadas")
    Call<SolicitudesResponse> getSolicitudesEnviadas(@Query("idSupervisor") int idSupervisor);

    @GET("supervisor/solicitudes-aceptadas")
    Call<SolicitudesResponse> getSolicitudesAceptadas(@Query("idEmpresa") int idEmpresa);

    @FormUrlEncoded
    @POST("supervisor/actualizar-solicitud")
    Call<AprobacionResponse> actualizarSolicitud(
            @Field("idSolicitud") int idSolicitud,
            @Field("nuevoEstado") String nuevoEstado
    );

    // ── Horario ───────────────────────────────────────────────────────────────
    @GET("horario/{boleta}")
    Call<HorarioDTO> obtenerHorario(@Path("boleta") int boleta);

    // ── Documentos ────────────────────────────────────────────────────────────
    @GET("documento/{boleta}")
    Call<List<DocumentoDTO>> getDocumentos(@Path("boleta") Integer boleta);

    @Multipart
    @POST("documento/{boleta}/{periodo}")
    Call<DocumentoDTO> subirDocumento(
            @Path("boleta") Integer boleta,
            @Path("periodo") Integer periodo,
            @Part MultipartBody.Part archivo
    );

    // ── Perfil ────────────────────────────────────────────────────────────────
    @GET("perfil/usuario/{idUsuario}")
    Call<PerfilDTO> obtenerPerfil(@Path("idUsuario") int idUsuario);

    @PUT("perfil/usuario/{idUsuario}/foto")
    @Headers("Content-Type: application/json")
    Call<String> actualizarFoto(@Path("idUsuario") int idUsuario,
                                @Body java.util.Map<String, String> body);

    // ── Notificaciones ────────────────────────────────────────────────────────
    @GET("notificacion/usuario/{idUsuario}")
    Call<List<NotificacionDTO>> obtenerNotificaciones(@Path("idUsuario") int idUsuario);

    @GET("notificacion/usuario/{idUsuario}/no-leidas")
    Call<Integer> contarNoLeidas(@Path("idUsuario") int idUsuario);

    @PUT("notificacion/{idNotificacion}/leida")
    Call<Void> marcarComoLeida(@Path("idNotificacion") int idNotificacion);

    @PUT("notificacion/usuario/{idUsuario}/leidas")
    Call<Void> marcarTodasComoLeidas(@Path("idUsuario") int idUsuario);
}
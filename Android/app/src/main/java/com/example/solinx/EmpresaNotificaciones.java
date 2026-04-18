package com.example.solinx;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.solinx.API.ApiClient;
import com.example.solinx.API.ApiService;
import com.example.solinx.DTO.PerfilDTO;
import com.example.solinx.DTO.SolicitudAcceptDTO;
import com.example.solinx.RESPONSE.SolicitudResponse;
import com.example.solinx.UTIL.ThemeUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmpresaNotificaciones extends AppCompatActivity implements View.OnClickListener {

    LinearLayout contenedorNotificaciones;
    TextView btnMenu, btnNoti, tvMensajeVacio;
    ImageView logoEmpresa, imgPerfilEmpresa;

    private int idEmpresaSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_notificaciones);

        idEmpresaSesion = obtenerIdEmpresaActual();

        inicializarVistas();
        cargarFotoPerfil();

        if (idEmpresaSesion != -1) {
            cargarSolicitudes();
        } else {
            Toast.makeText(this, "Sesión no válida. Vuelve a iniciar.", Toast.LENGTH_LONG).show();
        }
    }

    private int obtenerIdEmpresaActual() {
        SharedPreferences prefs = getSharedPreferences("sesion_usuario", MODE_PRIVATE);
        return prefs.getInt("id_empresa_activa", -1);
    }
    @Override
    protected void onResume() {
        super.onResume();
        cargarFotoPerfil();
    }

    private void inicializarVistas() {
        contenedorNotificaciones = findViewById(R.id.contenedorNotificaciones);
        tvMensajeVacio = findViewById(R.id.tvMensajeVacio);

        btnMenu = findViewById(R.id.btnMenu);
        btnNoti = findViewById(R.id.btnNoti);
        logoEmpresa = findViewById(R.id.logoEmpresa);
        imgPerfilEmpresa = findViewById(R.id.imgPerfilEmpresa);

        btnMenu.setOnClickListener(this);
        logoEmpresa.setOnClickListener(this);
        if (imgPerfilEmpresa != null) {
            imgPerfilEmpresa.setOnClickListener(this);
        }
    }
    private void cargarFotoPerfil() {
        if (imgPerfilEmpresa == null) return;

        int idUsuario = getSharedPreferences("sesion_usuario", MODE_PRIVATE)
                .getInt("idUsuario", -1);
        if (idUsuario == -1) return;

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.obtenerPerfil(idUsuario).enqueue(new Callback<PerfilDTO>() {
            @Override
            public void onResponse(Call<PerfilDTO> call, Response<PerfilDTO> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getFoto() != null
                        && !response.body().getFoto().isEmpty()) {
                    String b64 = response.body().getFoto();
                    byte[] bytes = Base64.decode(b64, Base64.DEFAULT);
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    runOnUiThread(() -> imgPerfilEmpresa.setImageBitmap(bmp));
                }
            }

            @Override
            public void onFailure(Call<PerfilDTO> call, Throwable t) {
                // dejar el icono default
            }
        });
    }

    private void cargarSolicitudes() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.obtenerSolicitudesPorEmpresa(idEmpresaSesion).enqueue(new Callback<List<SolicitudResponse>>() {
            @Override
            public void onResponse(Call<List<SolicitudResponse>> call, Response<List<SolicitudResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    renderizarSolicitudes(response.body());
                } else {
                    renderizarSolicitudes(java.util.Collections.emptyList());
                }
            }

            @Override
            public void onFailure(Call<List<SolicitudResponse>> call, Throwable t) {
                Toast.makeText(EmpresaNotificaciones.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderizarSolicitudes(List<SolicitudResponse> lista) {
        contenedorNotificaciones.removeAllViews();

        if (lista.isEmpty()) {
            tvMensajeVacio.setVisibility(View.VISIBLE);
            return;
        }
        tvMensajeVacio.setVisibility(View.GONE);

        int contadorVisual = 1;

        for (SolicitudResponse solicitud : lista) {

            View tarjeta = LayoutInflater.from(this).inflate(R.layout.activity_item_solicitud, contenedorNotificaciones, false);

            TextView tvNum = tarjeta.findViewById(R.id.tvNumeroSolicitud);
            TextView tvProyecto = tarjeta.findViewById(R.id.tvNombreProyecto);
            TextView tvBoleta = tarjeta.findViewById(R.id.tvBoleta);
            TextView tvCarrera = tarjeta.findViewById(R.id.tvCarreraAlumno);
            TextView tvEstado = tarjeta.findViewById(R.id.tvEstadoSolicitud);
            TextView btnAdmitir = tarjeta.findViewById(R.id.btnAdmitir);
            TextView btnRechazar = tarjeta.findViewById(R.id.btnRechazar);
            TextView btnCorreo = tarjeta.findViewById(R.id.btnEnviarCorreoAlumno);
            LinearLayout layoutBotones = tarjeta.findViewById(R.id.layoutBotonesAccion);

            tvNum.setText("Solicitud #" + contadorVisual);
            contadorVisual++;

            tvProyecto.setText("Proyecto: " + solicitud.getNombreProyecto());
            tvBoleta.setText("Boleta: " + solicitud.getBoletaAlumno());
            tvCarrera.setText("Carrera: " + solicitud.getCarreraAlumno());

            // Botón de correo siempre visible
            if (btnCorreo != null) {
                btnCorreo.setVisibility(View.VISIBLE);
                btnCorreo.setOnClickListener(v -> abrirCorreoAlumno(solicitud));
            }

            // ─── Traducir estado a texto amigable y decidir si mostrar botones ───
            String estado = solicitud.getEstadoSolicitud();
            String estadoAmigable;
            int colorEstado;
            boolean mostrarBotones;

            if ("aprobada_supervisor".equalsIgnoreCase(estado)) {
                estadoAmigable = "Pendiente de tu revisión";
                colorEstado = Color.parseColor("#FF9800");
                mostrarBotones = true;
            } else if ("aceptada".equalsIgnoreCase(estado)) {
                estadoAmigable = "Aprobada por ti";
                colorEstado = Color.parseColor("#4CAF50");
                mostrarBotones = false;
            } else if ("rechazada_empresa".equalsIgnoreCase(estado)) {
                estadoAmigable = "Rechazada por ti";
                colorEstado = Color.parseColor("#F44336");
                mostrarBotones = false;
            } else if ("aprobada".equalsIgnoreCase(estado)) {
                estadoAmigable = "Confirmada oficialmente";
                colorEstado = Color.parseColor("#4CAF50");
                mostrarBotones = false;
            } else {
                estadoAmigable = estado != null ? estado : "Pendiente";
                colorEstado = Color.parseColor("#FF9800");
                mostrarBotones = false;
            }

            tvEstado.setText("Estado: " + estadoAmigable);
            tvEstado.setTextColor(colorEstado);

            if (mostrarBotones) {
                layoutBotones.setVisibility(View.VISIBLE);
                btnAdmitir.setOnClickListener(v -> actualizarEstado(solicitud.getIdSolicitud(), "aceptada"));
                btnRechazar.setOnClickListener(v -> actualizarEstado(solicitud.getIdSolicitud(), "rechazada"));
            } else {
                layoutBotones.setVisibility(View.GONE);
            }

            contenedorNotificaciones.addView(tarjeta);
        }
    }

    private void abrirCorreoAlumno(SolicitudResponse solicitud) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String nombreAlumno = solicitud.getNombreEstudiante() != null
                ? solicitud.getNombreEstudiante()
                : "Alumno (Boleta " + solicitud.getBoletaAlumno() + ")";
        builder.setTitle("Enviar correo a " + nombreAlumno);

        final EditText input = new EditText(this);
        input.setHint("Escribe tu mensaje aquí...");
        input.setMinLines(3);
        input.setPadding(40, 20, 40, 20);
        builder.setView(input);

        builder.setPositiveButton("Enviar", (dialog, which) -> {
            String mensaje = input.getText().toString().trim();
            if (mensaje.isEmpty()) {
                Toast.makeText(this, "Escribe un mensaje", Toast.LENGTH_SHORT).show();
                return;
            }

            String destinatario = (solicitud.getCorreoEstudiante() != null && !solicitud.getCorreoEstudiante().isEmpty())
                    ? solicitud.getCorreoEstudiante()
                    : "";

            String cuerpo = "Empresa enviando mensaje al alumno:\n"
                    + "Alumno: " + nombreAlumno + "\n"
                    + "Boleta: " + solicitud.getBoletaAlumno() + "\n"
                    + "Proyecto: " + (solicitud.getNombreProyecto() != null ? solicitud.getNombreProyecto() : "") + "\n\n"
                    + "Mensaje:\n" + mensaje;

            String subject = "SoLinX - Empresa: Sobre tu solicitud al proyecto " +
                    (solicitud.getNombreProyecto() != null ? solicitud.getNombreProyecto() : "");

            String mailtoUri = "mailto:" + destinatario
                    + "?subject=" + Uri.encode(subject)
                    + "&body=" + Uri.encode(cuerpo);

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse(mailtoUri));

            try {
                startActivity(Intent.createChooser(emailIntent, "Enviar correo con..."));
            } catch (Exception e) {
                Toast.makeText(this, "No hay app de correo disponible", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void actualizarEstado(int idSolicitud, String nuevoEstado) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        if ("aceptada".equalsIgnoreCase(nuevoEstado)) {
            SolicitudAcceptDTO dto = new SolicitudAcceptDTO(idSolicitud, true);
            apiService.aceptarSolicitud(dto).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(EmpresaNotificaciones.this,
                                "Solicitud aceptada ✓", Toast.LENGTH_SHORT).show();
                        cargarSolicitudes();
                    } else {
                        Toast.makeText(EmpresaNotificaciones.this,
                                "Error al aceptar", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(EmpresaNotificaciones.this,
                            "Fallo de conexión", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            apiService.actualizarEstadoSolicitud(idSolicitud, nuevoEstado).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(EmpresaNotificaciones.this,
                                "Solicitud rechazada", Toast.LENGTH_SHORT).show();
                        cargarSolicitudes();
                    } else {
                        Toast.makeText(EmpresaNotificaciones.this,
                                "Error al rechazar", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(EmpresaNotificaciones.this,
                            "Fallo de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnMenu) {
            startActivity(new Intent(this, EmpresaVista.class));

        } else if (id == R.id.logoEmpresa) {
            Toast.makeText(this, "Actualizando...", Toast.LENGTH_SHORT).show();
            cargarSolicitudes();

        } else if (id == R.id.imgPerfilEmpresa) {
            startActivity(new Intent(this, EmpresaVistaCuenta.class));
        }

    }
}

package com.example.solinx;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.solinx.API.ApiClient;
import com.example.solinx.API.ApiService;
import com.example.solinx.RESPONSE.SolicitudResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmpresaNotificaciones extends AppCompatActivity implements View.OnClickListener {

    // UI
    LinearLayout contenedorNotificaciones;
    TextView btnMenu, btnNoti, tvMensajeVacio;
    ImageView logoEmpresa, btnCerrarSesion;

    // Variables de Sesión
    private int idEmpresaSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_notificaciones);

        // 1. Obtener ID de la sesión guardada
        idEmpresaSesion = obtenerIdEmpresaActual();

        inicializarVistas();

        // 2. Cargar datos si la sesión es válida
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

    private void inicializarVistas() {
        contenedorNotificaciones = findViewById(R.id.contenedorNotificaciones);
        tvMensajeVacio = findViewById(R.id.tvMensajeVacio);

        // Encabezado
        btnMenu = findViewById(R.id.btnMenu);
        btnNoti = findViewById(R.id.btnNoti);
        logoEmpresa = findViewById(R.id.logoEmpresa);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        // Listeners
        btnMenu.setOnClickListener(this);
        logoEmpresa.setOnClickListener(this);
        btnCerrarSesion.setOnClickListener(this);
    }

    // --- LÓGICA DE CARGA ---

    private void cargarSolicitudes() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.obtenerSolicitudesPorEmpresa(idEmpresaSesion).enqueue(new Callback<List<SolicitudResponse>>() {
            @Override
            public void onResponse(Call<List<SolicitudResponse>> call, Response<List<SolicitudResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    renderizarSolicitudes(response.body());
                } else {
                    // Si está vacío o hay error, limpiamos
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

        // --- INICIO CONTADOR VISUAL ---
        int contadorVisual = 1;

        for (SolicitudResponse solicitud : lista) {

            // Asegúrate de que el nombre del layout sea correcto (activity_item_solicitud o item_solicitud)
            View tarjeta = LayoutInflater.from(this).inflate(R.layout.activity_item_solicitud, contenedorNotificaciones, false);

            // Enlazar Vistas
            TextView tvNum = tarjeta.findViewById(R.id.tvNumeroSolicitud); // <--- NUEVO: Enlazamos el número
            TextView tvProyecto = tarjeta.findViewById(R.id.tvNombreProyecto);
            TextView tvBoleta = tarjeta.findViewById(R.id.tvBoleta);
            TextView tvCarrera = tarjeta.findViewById(R.id.tvCarreraAlumno);
            TextView tvEstado = tarjeta.findViewById(R.id.tvEstadoSolicitud);
            TextView btnAdmitir = tarjeta.findViewById(R.id.btnAdmitir);
            TextView btnRechazar = tarjeta.findViewById(R.id.btnRechazar);
            LinearLayout layoutBotones = tarjeta.findViewById(R.id.layoutBotonesAccion);

            // Llenar Datos

            // --- ASIGNAMOS EL NÚMERO SECUENCIAL ---
            tvNum.setText("Solicitud #" + contadorVisual);
            contadorVisual++;
            // --------------------------------------

            tvProyecto.setText("Proyecto: " + solicitud.getNombreProyecto());
            tvBoleta.setText("Boleta: " + solicitud.getBoletaAlumno());
            tvCarrera.setText("Carrera: " + solicitud.getCarreraAlumno());
            tvEstado.setText("Estado: " + solicitud.getEstadoSolicitud());

            if ("enviada".equalsIgnoreCase(solicitud.getEstadoSolicitud())) {

                tvEstado.setTextColor(Color.parseColor("#FF9800")); // Naranja
                layoutBotones.setVisibility(View.VISIBLE);

                btnAdmitir.setOnClickListener(v -> actualizarEstado(solicitud.getIdSolicitud(), "aceptada"));
                btnRechazar.setOnClickListener(v -> actualizarEstado(solicitud.getIdSolicitud(), "rechazada"));

            } else {
                // Si ya se respondió, ocultamos botones y cambiamos color
                layoutBotones.setVisibility(View.GONE);

                if ("aceptada".equalsIgnoreCase(solicitud.getEstadoSolicitud())) {
                    tvEstado.setTextColor(Color.parseColor("#4CAF50")); // Verde
                } else {
                    tvEstado.setTextColor(Color.parseColor("#F44336")); // Rojo
                }
            }

            contenedorNotificaciones.addView(tarjeta);
        }
    }

    // --- LÓGICA DE ACEPTAR / RECHAZAR ---

    private void actualizarEstado(int idSolicitud, String nuevoEstado) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.actualizarEstadoSolicitud(idSolicitud, nuevoEstado).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EmpresaNotificaciones.this, "Solicitud " + nuevoEstado, Toast.LENGTH_SHORT).show();
                    // Recargamos la lista para ver el cambio reflejado
                    cargarSolicitudes();
                } else {
                    Toast.makeText(EmpresaNotificaciones.this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EmpresaNotificaciones.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- MANEJO DE CLICKS (NAVEGACIÓN) ---

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnMenu) {
            // Regresar al menú principal
            finish();

        } else if (id == R.id.logoEmpresa) {
            // Refrescar la lista
            Toast.makeText(this, "Actualizando...", Toast.LENGTH_SHORT).show();
            cargarSolicitudes();

        } else if (id == R.id.btnCerrarSesion) {
            // Cerrar sesión completo
            SharedPreferences prefs = getSharedPreferences("sesion_usuario", MODE_PRIVATE);
            prefs.edit().clear().apply();

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}
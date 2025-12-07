package com.example.solinx;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.solinx.API.ApiClient;
import com.example.solinx.API.ApiService;
import com.example.solinx.DTO.SolicitudDTO;
import com.example.solinx.UTIL.ThemeUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlumnoEnviarSolicitud extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AlumnoEnviarSolicitud";

    ImageView btnRegresar, imgLogoProyecto;
    TextView btnEnviar, txtNombreEmpresa, txtNombreProyecto, fechaini, fechafin;
    TextView txtCarreraEnfocada, telefono, vacantes, ubi, obj;

    private Integer proyectoId;
    private Integer idEmpresa;
    private Integer boletaAlumno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alumno_enviar_solicitud);

        btnRegresar = findViewById(R.id.btnRegresar);
        imgLogoProyecto = findViewById(R.id.imgLogoProyecto);
        btnEnviar = findViewById(R.id.btnEnviar);
        txtNombreEmpresa = findViewById(R.id.txtNombreEmpresa);
        txtNombreProyecto = findViewById(R.id.txtNombreProyecto);
        fechaini = findViewById(R.id.fechini);
        fechafin = findViewById(R.id.fechfin);
        txtCarreraEnfocada = findViewById(R.id.txtCarreraEnfocada);
        telefono = findViewById(R.id.telefono);
        vacantes = findViewById(R.id.vacantes);
        ubi = findViewById(R.id.ubicacion);
        obj = findViewById(R.id.objetivo);

        btnEnviar.setOnClickListener(this);
        btnRegresar.setOnClickListener(this);

        // Obtener la boleta del estudiante de SharedPreferences
        obtenerBoletaEstudiante();

        recibirDatosDelProyecto();
    }

    private void obtenerBoletaEstudiante() {
        SharedPreferences prefs = getSharedPreferences("SoLinXPrefs", MODE_PRIVATE);
        String boletaStr = prefs.getString("boleta", null);

        if (boletaStr != null && !boletaStr.equals("N/A")) {
            try {
                boletaAlumno = Integer.parseInt(boletaStr);
                Log.d(TAG, "Boleta del estudiante: " + boletaAlumno);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error al parsear boleta: " + boletaStr);
                boletaAlumno = null;
            }
        } else {
            Log.e(TAG, "Boleta no encontrada en SharedPreferences");
            boletaAlumno = null;
        }
    }

    private void recibirDatosDelProyecto() {
        Intent intent = getIntent();
        if (intent != null) {
            proyectoId = intent.getIntExtra("proyectoId", 0);
            idEmpresa = intent.getIntExtra("idEmpresa", 0);

            String nombreEmpresa = intent.getStringExtra("nombreEmpresa");
            String nombreProyecto = intent.getStringExtra("nombreProyecto");
            String fechaInicio = intent.getStringExtra("fechaInicio");
            String fechaFin = intent.getStringExtra("fechaFin");
            String carreraEnfocada = intent.getStringExtra("carreraEnfocada");
            String telefonoEmpresa = intent.getStringExtra("telefono");
            int vacantesDisponibles = intent.getIntExtra("vacantes", 0);
            String ubicacion = intent.getStringExtra("ubicacion");
            String objetivo = intent.getStringExtra("objetivo");
            String imagenRef = intent.getStringExtra("imagenRef");

            if (nombreEmpresa != null && !nombreEmpresa.isEmpty()) {
                txtNombreEmpresa.setText("Empresa: " + nombreEmpresa);
            } else {
                txtNombreEmpresa.setVisibility(View.GONE);
            }

            if (nombreProyecto != null && !nombreProyecto.isEmpty()) {
                txtNombreProyecto.setText("Proyecto: " + nombreProyecto);
            } else {
                txtNombreProyecto.setText("Proyecto sin nombre");
            }

            if (fechaInicio != null && !fechaInicio.isEmpty()) {
                fechaini.setText("Fecha Inicio: " + fechaInicio);
            } else {
                fechaini.setText("Fecha Inicio: No disponible");
            }

            if (fechaFin != null && !fechaFin.isEmpty()) {
                fechafin.setText("Fecha Termino: " + fechaFin);
            } else {
                fechafin.setText("Fecha Termino: No disponible");
            }

            if (carreraEnfocada != null && !carreraEnfocada.isEmpty()) {
                txtCarreraEnfocada.setText("Carrera Enfocada: " + carreraEnfocada);
            } else {
                txtCarreraEnfocada.setText("Carrera Enfocada: No especificada");
            }

            if (telefonoEmpresa != null && !telefonoEmpresa.isEmpty()) {
                telefono.setText("Teléfono: " + telefonoEmpresa);
            } else {
                telefono.setText("Teléfono: No disponible");
            }

            vacantes.setText("Vacantes: " + vacantesDisponibles);

            if (ubicacion != null && !ubicacion.isEmpty()) {
                ubi.setText("Ubicación: " + ubicacion);
            } else {
                ubi.setText("Ubicación: No especificada");
            }

            if (objetivo != null && !objetivo.isEmpty()) {
                obj.setText("Objetivo: " + objetivo);
            } else {
                obj.setText("Objetivo: No especificado");
            }
            imgLogoProyecto.setImageResource(R.drawable.solinx_logo);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnRegresar) {
            finish();
        } else if (id == R.id.btnEnviar) {
            enviarSolicitud();
        }
    }

    private void enviarSolicitud() {
        Log.d(TAG, "🔵 INICIO enviarSolicitud");

        // Validar que tengamos los datos necesarios
        if (boletaAlumno == null) {
            Toast.makeText(this, "Error: No se pudo obtener tu boleta", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "❌ boletaAlumno es null");
            return;
        }

        if (proyectoId == null || proyectoId == 0) {
            Toast.makeText(this, "Error: Proyecto no válido", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "❌ proyectoId es null o 0");
            return;
        }

        // Deshabilitar el botón para evitar doble click
        btnEnviar.setEnabled(false);
        btnEnviar.setText("ENVIANDO...");

        Log.d(TAG, "🔵 Enviando solicitud - Boleta: " + boletaAlumno + " | Proyecto: " + proyectoId);

        // Crear el DTO de la solicitud
        SolicitudDTO solicitudDTO = new SolicitudDTO();
        solicitudDTO.setBoletaAlumno(boletaAlumno);
        solicitudDTO.setIdProyecto(proyectoId);
        solicitudDTO.setEstadoSolicitud("enviada");
        solicitudDTO.setFechaSolicitud(null); // El backend lo asigna automáticamente

        Log.d(TAG, "🔵 DTO creado: boletaAlumno=" + solicitudDTO.getBoletaAlumno() +
                ", idProyecto=" + solicitudDTO.getIdProyecto() +
                ", estado=" + solicitudDTO.getEstadoSolicitud());

        // Enviar al backend
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<SolicitudDTO> call = apiService.enviarSolicitud(solicitudDTO);

        call.enqueue(new Callback<SolicitudDTO>() {
            @Override
            public void onResponse(Call<SolicitudDTO> call, Response<SolicitudDTO> response) {
                Log.d(TAG, "🔵 Response Code: " + response.code());
                Log.d(TAG, "🔵 Response Message: " + response.message());
                Log.d(TAG, "🔵 Response Body: " + response.body());

                // Intentar leer el error body si existe
                if (response.errorBody() != null) {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e(TAG, "❌ Error Body: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "❌ No se pudo leer error body: " + e.getMessage());
                    }
                }

                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "✅ Solicitud enviada exitosamente. ID: " + response.body().getIdSolicitud());
                    Toast.makeText(AlumnoEnviarSolicitud.this, "¡Solicitud enviada con éxito!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e(TAG, "❌ Error al enviar solicitud. Código: " + response.code());
                    Toast.makeText(AlumnoEnviarSolicitud.this, "Error al enviar la solicitud (Código: " + response.code() + ")", Toast.LENGTH_LONG).show();
                    btnEnviar.setEnabled(true);
                    btnEnviar.setText("ENVIAR SOLICITUD");
                }
            }

            @Override
            public void onFailure(Call<SolicitudDTO> call, Throwable t) {
                Log.e(TAG, "❌ Error de red al enviar solicitud: " + t.getMessage());
                t.printStackTrace();
                Toast.makeText(AlumnoEnviarSolicitud.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                btnEnviar.setEnabled(true);
                btnEnviar.setText("ENVIAR SOLICITUD");
            }
        });
    }
}
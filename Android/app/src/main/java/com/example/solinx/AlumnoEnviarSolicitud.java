package com.example.solinx;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
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

    ImageView btnRegresar, imgLogoProyecto, fotoPerfilHeader;
    TextView btnEnviar, txtNombreEmpresa, txtNombreProyecto, fechaini, fechafin;
    TextView txtCarreraEnfocada, telefono, vacantes, ubi, obj;
    TextView btnboleta;

    private Integer proyectoId;
    private Integer idEmpresa;
    private Integer boletaAlumno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alumno_enviar_solicitud);

        btnRegresar       = findViewById(R.id.btnRegresar);
        imgLogoProyecto   = findViewById(R.id.imgLogoProyecto);
        btnEnviar         = findViewById(R.id.btnEnviar);
        txtNombreEmpresa  = findViewById(R.id.txtNombreEmpresa);
        txtNombreProyecto = findViewById(R.id.txtNombreProyecto);
        fechaini          = findViewById(R.id.fechini);
        fechafin          = findViewById(R.id.fechfin);
        txtCarreraEnfocada= findViewById(R.id.txtCarreraEnfocada);
        telefono          = findViewById(R.id.telefono);
        vacantes          = findViewById(R.id.vacantes);
        ubi               = findViewById(R.id.ubicacion);
        obj               = findViewById(R.id.objetivo);
        btnboleta         = findViewById(R.id.btnboleta);
        fotoPerfilHeader  = findViewById(R.id.fotoPerfilHeader);

        btnEnviar.setOnClickListener(this);
        btnRegresar.setOnClickListener(this);

        obtenerBoletaEstudiante();
        cargarFotoYBoleta();
        recibirDatosDelProyecto();
    }

    private void obtenerBoletaEstudiante() {
        SharedPreferences prefs = getSharedPreferences("SoLinXPrefs", MODE_PRIVATE);
        String boletaStr = prefs.getString("boleta", null);

        Log.d(TAG, "Boleta leída de SharedPreferences: " + boletaStr);

        if (boletaStr != null && !boletaStr.equals("N/A")) {
            try {
                boletaAlumno = Integer.parseInt(boletaStr);
                Log.d(TAG, "Boleta parseada: " + boletaAlumno);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error al parsear boleta: " + boletaStr);
                boletaAlumno = null;
            }
        } else {
            Log.e(TAG, "Boleta no encontrada o N/A");
            boletaAlumno = null;
        }
    }

    private void cargarFotoYBoleta() {
        SharedPreferences prefs = getSharedPreferences("SoLinXPrefs", MODE_PRIVATE);
        String boleta = prefs.getString("boleta", "N/A");

        Log.d(TAG, "Cargando foto y boleta para: " + boleta);

        if (btnboleta != null) {
            btnboleta.setText(boleta);
        }

        if (fotoPerfilHeader != null) {
            String b64 = getSharedPreferences("SoLinXFotos", MODE_PRIVATE)
                    .getString("foto_perfil_" + boleta, null);

            Log.d(TAG, "Foto encontrada: " + (b64 != null ? "SÍ" : "NO"));

            if (b64 != null) {
                byte[] bytes = Base64.decode(b64, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                fotoPerfilHeader.setImageBitmap(bitmap);
            }
        }
    }

    private void recibirDatosDelProyecto() {
        Intent intent = getIntent();
        if (intent != null) {
            proyectoId = intent.getIntExtra("proyectoId", 0);
            idEmpresa  = intent.getIntExtra("idEmpresa", 0);

            String nombreEmpresa       = intent.getStringExtra("nombreEmpresa");
            String nombreProyecto      = intent.getStringExtra("nombreProyecto");
            String fechaInicio         = intent.getStringExtra("fechaInicio");
            String fechaFin            = intent.getStringExtra("fechaFin");
            String carreraEnfocada     = intent.getStringExtra("carreraEnfocada");
            String telefonoEmpresa     = intent.getStringExtra("telefono");
            int    vacantesDisponibles = intent.getIntExtra("vacantes", 0);
            String ubicacion           = intent.getStringExtra("ubicacion");
            String objetivo            = intent.getStringExtra("objetivo");

            txtNombreEmpresa.setText(nombreEmpresa != null && !nombreEmpresa.isEmpty()
                    ? "Empresa: " + nombreEmpresa : "");
            txtNombreProyecto.setText(nombreProyecto != null && !nombreProyecto.isEmpty()
                    ? "Proyecto: " + nombreProyecto : "Proyecto sin nombre");
            fechaini.setText("Fecha Inicio: " + (fechaInicio != null && !fechaInicio.isEmpty()
                    ? fechaInicio : "No disponible"));
            fechafin.setText("Fecha Termino: " + (fechaFin != null && !fechaFin.isEmpty()
                    ? fechaFin : "No disponible"));
            txtCarreraEnfocada.setText("Carrera Enfocada: " + (carreraEnfocada != null && !carreraEnfocada.isEmpty()
                    ? carreraEnfocada : "No especificada"));
            telefono.setText("Teléfono: " + (telefonoEmpresa != null && !telefonoEmpresa.isEmpty()
                    ? telefonoEmpresa : "No disponible"));
            vacantes.setText("Vacantes: " + vacantesDisponibles);
            ubi.setText("Ubicación: " + (ubicacion != null && !ubicacion.isEmpty()
                    ? ubicacion : "No especificada"));
            obj.setText("Objetivo: " + (objetivo != null && !objetivo.isEmpty()
                    ? objetivo : "No especificado"));

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
        if (boletaAlumno == null) {
            Toast.makeText(this, "Error: No se pudo obtener tu boleta", Toast.LENGTH_SHORT).show();
            return;
        }
        if (proyectoId == null || proyectoId == 0) {
            Toast.makeText(this, "Error: Proyecto no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        btnEnviar.setEnabled(false);
        btnEnviar.setText("ENVIANDO...");

        SolicitudDTO solicitudDTO = new SolicitudDTO();
        solicitudDTO.setBoletaAlumno(boletaAlumno);
        solicitudDTO.setIdProyecto(proyectoId);
        solicitudDTO.setEstadoSolicitud("enviada");
        solicitudDTO.setFechaSolicitud(null);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.enviarSolicitud(solicitudDTO).enqueue(new Callback<SolicitudDTO>() {
            @Override
            public void onResponse(Call<SolicitudDTO> call, Response<SolicitudDTO> response) {
                Log.d(TAG, "Response: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AlumnoEnviarSolicitud.this,
                            "¡Solicitud enviada con éxito!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AlumnoEnviarSolicitud.this,
                            "Error al enviar la solicitud (Código: " + response.code() + ")",
                            Toast.LENGTH_LONG).show();
                    btnEnviar.setEnabled(true);
                    btnEnviar.setText("ENVIAR SOLICITUD");
                }
            }

            @Override
            public void onFailure(Call<SolicitudDTO> call, Throwable t) {
                Log.e(TAG, "Error de red: " + t.getMessage());
                Toast.makeText(AlumnoEnviarSolicitud.this,
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                btnEnviar.setEnabled(true);
                btnEnviar.setText("ENVIAR SOLICITUD");
            }
        });
    }
}
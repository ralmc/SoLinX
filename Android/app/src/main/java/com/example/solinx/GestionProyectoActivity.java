package com.example.solinx;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.solinx.API.ApiClient;
import com.example.solinx.API.ApiService;
import com.example.solinx.RESPONSE.ProyectoResponse;
import com.example.solinx.UTIL.ThemeUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GestionProyectoActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etCarrera, etNombreProyecto, etObjetivo, etVacantes, etUbicacion, etImagenRef;
    Button btnGuardar;
    ImageView logoEmpresa, btnCerrarSesion;
    TextView tvNotificaciones;

    ApiService apiService;
    boolean esEdicion = false;
    int idProyectoEditar = -1;
    int idEmpresaEditar = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_proyecto);

        inicializarVistas();
        apiService = ApiClient.getClient().create(ApiService.class);
        verificarModoEdicion();

        btnGuardar.setOnClickListener(this);
        logoEmpresa.setOnClickListener(this);
        tvNotificaciones.setOnClickListener(this);
        btnCerrarSesion.setOnClickListener(this);
    }

    private void inicializarVistas() {
        etCarrera = findViewById(R.id.etCarrera);
        etNombreProyecto = findViewById(R.id.etNombreProyecto);
        etObjetivo = findViewById(R.id.etObjetivo);
        etVacantes = findViewById(R.id.etVacantes);
        etUbicacion = findViewById(R.id.etUbicacion);
        etImagenRef = findViewById(R.id.etImagenRef);
        btnGuardar = findViewById(R.id.btnGuardar);
        logoEmpresa = findViewById(R.id.logoEmpresa);
        tvNotificaciones = findViewById(R.id.notificaciones);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
    }

    private int obtenerIdEmpresaActual() {
        SharedPreferences prefs = getSharedPreferences("sesion_usuario", MODE_PRIVATE);
        return prefs.getInt("id_empresa_activa", -1);
    }

    private void verificarModoEdicion() {
        if (getIntent().hasExtra("idProyecto")) {
            esEdicion = true;
            idProyectoEditar = getIntent().getIntExtra("idProyecto", -1);
            idEmpresaEditar = getIntent().getIntExtra("idEmpresa", -1);

            btnGuardar.setText("ACTUALIZAR PROYECTO");
            etCarrera.setText(getIntent().getStringExtra("carrera"));
            etNombreProyecto.setText(getIntent().getStringExtra("nombre"));
            etObjetivo.setText(getIntent().getStringExtra("objetivo"));
            etVacantes.setText(String.valueOf(getIntent().getIntExtra("vacantes", 1)));
            etUbicacion.setText(getIntent().getStringExtra("ubicacion"));
            etImagenRef.setText(getIntent().getStringExtra("imagen"));
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnGuardar) {
            if (validarCampos()) {
                if (esEdicion) actualizarProyecto();
                else crearNuevoProyecto();
            }
        } else if (id == R.id.logoEmpresa) {
            finish();
        } else if (id == R.id.notificaciones) {
            startActivity(new Intent(this, EmpresaNotificaciones.class));
        } else if (id == R.id.btnCerrarSesion) {
            SharedPreferences prefs = getSharedPreferences("sesion_usuario", MODE_PRIVATE);
            prefs.edit().clear().apply();

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private boolean validarCampos() {
        boolean esValido = true;
        String vacantesTxt = etVacantes.getText().toString().trim();

        // 1. Carrera
        if (etCarrera.getText().toString().trim().isEmpty()) {
            etCarrera.setError("Campo de Carrera requerido");
            esValido = false;
        }

        // 2. Nombre del Proyecto
        if (etNombreProyecto.getText().toString().trim().isEmpty()) {
            etNombreProyecto.setError("Campo de Nombre de Proyecto requerido");
            esValido = false;
        }

        // 3. Objetivo
        if (etObjetivo.getText().toString().trim().isEmpty()) {
            etObjetivo.setError("Campo de Objetivo requerido");
            esValido = false;
        }

        // 4. Ubicación
        if (etUbicacion.getText().toString().trim().isEmpty()) {
            etUbicacion.setError("Campo de Ubicación requerido");
            esValido = false;
        }

        // 5. Vacantes (Validación de Número y Requerido)
        if (vacantesTxt.isEmpty()) {
            etVacantes.setError("Número de Vacantes requerido");
            esValido = false;
        } else {
            try {
                int vacantes = Integer.parseInt(vacantesTxt);
                if (vacantes <= 0) {
                    etVacantes.setError("El número debe ser mayor a cero");
                    esValido = false;
                }
            } catch (NumberFormatException e) {
                etVacantes.setError("Debe ser un número entero");
                esValido = false;
            }
        }


        return esValido;
    }

    private ProyectoResponse armarObjetoProyecto() {
        ProyectoResponse p = new ProyectoResponse();
        p.setCarreraEnfocada(etCarrera.getText().toString().trim());
        p.setNombreProyecto(etNombreProyecto.getText().toString().trim());
        p.setObjetivo(etObjetivo.getText().toString().trim());
        p.setUbicacion(etUbicacion.getText().toString().trim());

        String imagenTxt = etImagenRef.getText().toString().trim();
        p.setImagenRef(imagenTxt.isEmpty() ? "img_default_proyecto" : imagenTxt);

        try {
            String vacantesTxt = etVacantes.getText().toString().trim();
            p.setVacantes(Integer.parseInt(vacantesTxt));
        } catch (NumberFormatException e) {
            p.setVacantes(1);
        }

        int idEmpresa;
        if (esEdicion && idEmpresaEditar != -1) {
            idEmpresa = idEmpresaEditar;
        } else {
            idEmpresa = obtenerIdEmpresaActual();
            if (idEmpresa == -1) {
                Toast.makeText(this, "Sesión perdida, relogueate", Toast.LENGTH_SHORT).show();
                idEmpresa = 1;
            }
        }

        p.setIdEmpresa(idEmpresa);

        return p;
    }

    private void crearNuevoProyecto() {
        ProyectoResponse nuevoProyecto = armarObjetoProyecto();
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.crearProyecto(nuevoProyecto).enqueue(new Callback<ProyectoResponse>() {
            @Override
            public void onResponse(Call<ProyectoResponse> call, Response<ProyectoResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(GestionProyectoActivity.this, "Creado", Toast.LENGTH_SHORT).show();
                    finish();
                } else Toast.makeText(GestionProyectoActivity.this, "Error " + response.code(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Call<ProyectoResponse> call, Throwable t) { Toast.makeText(GestionProyectoActivity.this, "Error red", Toast.LENGTH_SHORT).show(); }
        });
    }

    private void actualizarProyecto() {
        ProyectoResponse proyectoEditado = armarObjetoProyecto();
        proyectoEditado.setIdProyecto(idProyectoEditar);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.actualizarProyecto(idProyectoEditar, proyectoEditado).enqueue(new Callback<ProyectoResponse>() {
            @Override
            public void onResponse(Call<ProyectoResponse> call, Response<ProyectoResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(GestionProyectoActivity.this, "Actualizado", Toast.LENGTH_SHORT).show();
                    finish();
                } else Toast.makeText(GestionProyectoActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Call<ProyectoResponse> call, Throwable t) { Toast.makeText(GestionProyectoActivity.this, "Error red", Toast.LENGTH_SHORT).show(); }
        });
    }
}
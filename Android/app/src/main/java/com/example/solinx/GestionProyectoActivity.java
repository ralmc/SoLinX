package com.example.solinx;

import android.content.Intent;
import android.content.SharedPreferences; // IMPORTANTE
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        if (etCarrera.getText().toString().trim().isEmpty()) { etCarrera.setError("Requerido"); return false; }
        if (etNombreProyecto.getText().toString().trim().isEmpty()) { etNombreProyecto.setError("Requerido"); return false; }
        return true;
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
            p.setVacantes(!vacantesTxt.isEmpty() ? Integer.parseInt(vacantesTxt) : 1);
        } catch (NumberFormatException e) { p.setVacantes(1); }

        int idSesion = obtenerIdEmpresaActual();
        if (idSesion != -1) {
            p.setIdEmpresa(idSesion);
        } else {
            Toast.makeText(this, "Sesión perdida, relogueate", Toast.LENGTH_SHORT).show();
            p.setIdEmpresa(1); // Fallback
        }

        return p;
    }

    private void crearNuevoProyecto() {
        ProyectoResponse nuevoProyecto = armarObjetoProyecto();
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
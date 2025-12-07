package com.example.solinx;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.solinx.API.ApiClient;
import com.example.solinx.API.ApiService;
import com.example.solinx.DTO.LoginDTO;
import com.example.solinx.DTO.LoginResponseDTO;
import com.example.solinx.UTIL.ThemeUtils;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IniciarSesion extends AppCompatActivity {

    private static final String TAG = "IniciarSesion";

    TextInputEditText etUsuario, etContrasena;
    TextView tvRegistroLink;
    Button btnEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        tvRegistroLink = findViewById(R.id.tvRegistroLink);
        etUsuario = findViewById(R.id.etUsuario);
        etContrasena = findViewById(R.id.etContrasena);
        btnEnviar = findViewById(R.id.btnEnviar);

        btnEnviar.setOnClickListener(v -> hacerLogin());
        tvRegistroLink.setOnClickListener(v -> registroAlumno());
    }

    private void registroAlumno() {
        Intent intent = new Intent(this, AlumnoCrearCuenta.class);
        startActivity(intent);
    }

    private void hacerLogin() {
        String correo = etUsuario.getText().toString().trim();
        String password = etContrasena.getText().toString().trim();

        if (correo.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa ambos campos", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginDTO loginDTO = new LoginDTO(correo, password);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<LoginResponseDTO> call = apiService.login(loginDTO);

        call.enqueue(new Callback<LoginResponseDTO>() {
            @Override
            public void onResponse(Call<LoginResponseDTO> call, Response<LoginResponseDTO> response) {

                if (response.code() == 401) {
                    Toast.makeText(IniciarSesion.this, "Contraseña/Email Incorrectos", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(IniciarSesion.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                    return;
                }

                LoginResponseDTO loginResponse = response.body();

                // Log para debugging
                Log.d(TAG, "Login exitoso - Usuario: " + loginResponse.getNombre());
                Log.d(TAG, "Rol: " + loginResponse.getRol());

                // Guardar sesión básica
                guardarSesionBasica(loginResponse);

                String rol = loginResponse.getRol();

                // ============================================
                // NAVEGACIÓN SEGÚN ROL
                // ============================================

                if ("estudiante".equalsIgnoreCase(rol)) {
                    // ESTUDIANTE → Guardar datos adicionales y navegar a AlumnoMenuEmpresas
                    guardarDatosEstudiante(loginResponse);
                    navegarVistaAlumno(loginResponse);

                } else if ("empresa".equalsIgnoreCase(rol)) {
                    // EMPRESA → EmpresaVista
                    navegarVistaEmpresa(loginResponse);

                } else if ("supervisor".equalsIgnoreCase(rol)) {
                    // SUPERVISOR → MenuSupervisorActivity
                    navegarVistaSupervisor(loginResponse);

                } else if ("administrador".equalsIgnoreCase(rol)) {
                    // ADMINISTRADOR (opcional, si tienes esta vista)
                    Toast.makeText(IniciarSesion.this, "Panel de administrador próximamente", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(IniciarSesion.this, "Rol no permitido: " + rol, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponseDTO> call, Throwable t) {
                Log.e(TAG, "Error de red: " + t.getMessage());
                Toast.makeText(IniciarSesion.this, "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void guardarSesionBasica(LoginResponseDTO loginResponse) {
        SharedPreferences preferences = getSharedPreferences("sesion_usuario", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("idUsuario", loginResponse.getIdUsuario());
        editor.putString("rol", loginResponse.getRol());
        editor.putString("nombre", loginResponse.getNombre());
        editor.putString("correo", loginResponse.getCorreo());
        editor.apply();

        Log.d(TAG, "Sesión básica guardada");
    }

    private void guardarDatosEstudiante(LoginResponseDTO loginResponse) {
        SharedPreferences prefs = getSharedPreferences("SoLinXPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Guardar datos del estudiante
        String boleta = loginResponse.getBoleta() != null ?
                loginResponse.getBoleta().toString() : "N/A";

        editor.putString("boleta", boleta);
        editor.putString("nombre", loginResponse.getNombre());
        editor.putString("correo", loginResponse.getCorreo());
        editor.putString("carrera", loginResponse.getCarrera() != null ?
                loginResponse.getCarrera() : "N/A");
        editor.putString("escuela", loginResponse.getEscuela() != null ?
                loginResponse.getEscuela() : "N/A");
        editor.putString("telefono", loginResponse.getTelefono() != null ?
                loginResponse.getTelefono() : "N/A");
        editor.apply();

        Log.d(TAG, "Datos del estudiante guardados - Boleta: " + boleta);
    }

    private void navegarVistaAlumno(LoginResponseDTO loginResponse) {
        Intent intent = new Intent(IniciarSesion.this, AlumnoMenuEmpresas.class);

        // Enviar datos básicos necesarios para el menú
        intent.putExtra("idUsuario", loginResponse.getIdUsuario());
        intent.putExtra("nombre", loginResponse.getNombre());
        intent.putExtra("correo", loginResponse.getCorreo());
        intent.putExtra("rol", loginResponse.getRol());

        Log.d(TAG, "Navegando a AlumnoMenuEmpresas");
        startActivity(intent);
        finish();
    }

    private void navegarVistaEmpresa(LoginResponseDTO loginResponse) {
        Integer idEmpresa = loginResponse.getIdEmpresa();

        Log.d(TAG, "============================================");
        Log.d(TAG, "NAVEGANDO A EMPRESA VISTA");
        Log.d(TAG, "idEmpresa recibido del backend: " + idEmpresa);
        Log.d(TAG, "============================================");

        if (idEmpresa == null || idEmpresa == 0) {
            Log.e(TAG, "❌ ERROR: idEmpresa es null o 0");
            Toast.makeText(this, "Error crítico: ID Empresa no encontrado. Contacta soporte.", Toast.LENGTH_LONG).show();
            return; // ✅ NO NAVEGAR SI NO HAY idEmpresa
        }

        SharedPreferences prefs = getSharedPreferences("sesion_usuario", MODE_PRIVATE);
        boolean guardado = prefs.edit()
                .putInt("id_empresa_activa", idEmpresa)
                .commit(); // ✅ CAMBIAR apply() por commit()

        if (guardado) {
            Log.d(TAG, "✅ ID Empresa guardado exitosamente en SharedPreferences: " + idEmpresa);
        } else {
            Log.e(TAG, "❌ ERROR al guardar idEmpresa en SharedPreferences");
        }

        Intent intent = new Intent(IniciarSesion.this, EmpresaVista.class);
        intent.putExtra("ID_EMPRESA_ACTUAL", idEmpresa);
        intent.putExtra("nombre", loginResponse.getNombre());

        Log.d(TAG, "✅ Navegando a EmpresaVista con idEmpresa: " + idEmpresa);
        startActivity(intent);
        finish();
    }

    private void navegarVistaSupervisor(LoginResponseDTO loginResponse) {
        Intent intent = new Intent(IniciarSesion.this, MenuSupervisorActivity.class);
        intent.putExtra("idUsuario", loginResponse.getIdUsuario());
        intent.putExtra("nombre", loginResponse.getNombre());
        intent.putExtra("correo", loginResponse.getCorreo());
        intent.putExtra("rol", loginResponse.getRol());

        Log.d(TAG, "Navegando a MenuSupervisorActivity");
        startActivity(intent);
        finish();
    }
}
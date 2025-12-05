package com.example.solinx;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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

                // Guardar sesión
                SharedPreferences preferences = getSharedPreferences("sesion_usuario", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("idUsuario", loginResponse.getIdUsuario());
                editor.putString("rol", loginResponse.getRol());
                editor.putString("nombre", loginResponse.getNombre());
                editor.putString("correo", loginResponse.getCorreo());
                editor.apply();

                String rol = loginResponse.getRol();

                // ============================================
                // NAVEGACIÓN SEGÚN ROL
                // ============================================

                if ("estudiante".equalsIgnoreCase(rol)) {
                    // ESTUDIANTE → AlumnoMenuEmpresas
                    Intent intent = new Intent(IniciarSesion.this, AlumnoMenuEmpresas.class);
                    intent.putExtra("idUsuario", loginResponse.getIdUsuario());
                    intent.putExtra("nombre", loginResponse.getNombre());
                    intent.putExtra("correo", loginResponse.getCorreo());
                    intent.putExtra("rol", loginResponse.getRol());
                    startActivity(intent);
                    finish();

                } else if ("empresa".equalsIgnoreCase(rol)) {
                    // EMPRESA → EmpresaVista
                    Intent intent = new Intent(IniciarSesion.this, EmpresaVista.class);

                    Integer idEmpresaReal = loginResponse.getIdEmpresa();
                    if (idEmpresaReal == null || idEmpresaReal == 0) {
                        idEmpresaReal = 1;
                    }

                    intent.putExtra("ID_EMPRESA_ACTUAL", idEmpresaReal);
                    intent.putExtra("nombre", loginResponse.getNombre());
                    startActivity(intent);
                    finish();

                } else if ("supervisor".equalsIgnoreCase(rol)) {
                    Intent intent = new Intent(IniciarSesion.this, MenuSupervisorActivity.class);
                    intent.putExtra("idUsuario", loginResponse.getIdUsuario());
                    intent.putExtra("nombre", loginResponse.getNombre());
                    intent.putExtra("correo", loginResponse.getCorreo());
                    intent.putExtra("rol", loginResponse.getRol());
                    startActivity(intent);
                    finish();

                } else if ("administrador".equalsIgnoreCase(rol)) {
                    // ADMINISTRADOR (opcional, si tienes esta vista)
                    Toast.makeText(IniciarSesion.this, "Panel de administrador próximamente", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(IniciarSesion.this, "Rol no permitido: " + rol, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponseDTO> call, Throwable t) {
                Toast.makeText(IniciarSesion.this, "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
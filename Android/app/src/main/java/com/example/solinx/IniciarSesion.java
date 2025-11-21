package com.example.solinx;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.solinx.API.ApiClient;
import com.example.solinx.API.ApiService;
import com.example.solinx.DTO.LoginDTO;
import com.example.solinx.DTO.LoginResponseDTO;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IniciarSesion extends AppCompatActivity {

    TextInputEditText etUsuario, etContrasena;
    Button btnEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        etUsuario = findViewById(R.id.etUsuario);
        etContrasena = findViewById(R.id.etContrasena);
        btnEnviar = findViewById(R.id.btnEnviar);

        btnEnviar.setOnClickListener(v -> hacerLogin());
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
                    Toast.makeText(IniciarSesion.this,
                            "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(IniciarSesion.this,
                            "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                    return;
                }

                LoginResponseDTO loginResponse = response.body();

                // Mostrar mensaje
                Toast.makeText(IniciarSesion.this,
                        "Bienvenido " + loginResponse.getNombre(),
                        Toast.LENGTH_LONG).show();

                // Obtener rol
                String rol = loginResponse.getRol();

                // --- RUTEO POR ROL ---
                if ("estudiante".equalsIgnoreCase(rol)) {

                    Intent intent = new Intent(IniciarSesion.this, AlumnoMenuEmpresas.class);
                    intent.putExtra("idUsuario", loginResponse.getIdUsuario());
                    intent.putExtra("nombre", loginResponse.getNombre());
                    intent.putExtra("correo", loginResponse.getCorreo());
                    intent.putExtra("rol", loginResponse.getRol());
                    intent.putExtra("tipoUsuario", loginResponse.getTipoUsuario());

                    startActivity(intent);
                    finish();
                    return;
                }

                // Otros roles aquí en el futuro
                Toast.makeText(IniciarSesion.this,
                        "Rol no permitido en esta app", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<LoginResponseDTO> call, Throwable t) {
                Toast.makeText(IniciarSesion.this,
                        "Error de red: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}

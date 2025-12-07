package com.example.solinx;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.solinx.API.ApiClient;
import com.example.solinx.API.ApiService;
import com.example.solinx.DTO.RegistroSupervisorDTO;
import com.example.solinx.DTO.RegistroSupervisorResponseDTO;
import com.example.solinx.UTIL.ThemeUtils;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupervisorCrear extends AppCompatActivity {

    TextInputEditText etNombreSupervisor, etArea, etIdEmpresa, etCorreo,
            etConfirmarCorreo, etTelefono, etPassword, etConfirmarPassword;
    Button btnRegister;
    TextView tvLoginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_crear);

        etNombreSupervisor = findViewById(R.id.et_nombre_supervisor);
        etArea = findViewById(R.id.et_area);
        etIdEmpresa = findViewById(R.id.et_id_empresa);
        etCorreo = findViewById(R.id.et_email_supervisor);
        etConfirmarCorreo = findViewById(R.id.et_confirm_email);
        etTelefono = findViewById(R.id.et_telefono);
        etPassword = findViewById(R.id.et_password_register);
        etConfirmarPassword = findViewById(R.id.et_confirm_password);

        btnRegister = findViewById(R.id.btn_register);
        tvLoginLink = findViewById(R.id.tv_login_link);

        btnRegister.setOnClickListener(v -> registrarSupervisor());
        tvLoginLink.setOnClickListener(v -> irALogin());
    }

    private void registrarSupervisor() {
        String nombreSupervisor = etNombreSupervisor.getText().toString().trim();
        String area = etArea.getText().toString().trim();
        String idEmpresaStr = etIdEmpresa.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String confirmarCorreo = etConfirmarCorreo.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmarPassword = etConfirmarPassword.getText().toString().trim();

        if (nombreSupervisor.isEmpty() || area.isEmpty() || idEmpresaStr.isEmpty() ||
                correo.isEmpty() || telefono.isEmpty() || password.isEmpty() ||
                confirmarCorreo.isEmpty() || confirmarPassword.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!correo.equals(confirmarCorreo)) {
            Toast.makeText(this, "Los correos no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmarPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (telefono.length() != 10) {
            Toast.makeText(this, "El teléfono debe tener 10 dígitos", Toast.LENGTH_SHORT).show();
            return;
        }

        Integer idEmpresa;
        try {
            idEmpresa = Integer.parseInt(idEmpresaStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID de empresa inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        RegistroSupervisorDTO dto = new RegistroSupervisorDTO(
                nombreSupervisor, area, idEmpresa, correo, telefono, password);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<RegistroSupervisorResponseDTO> call = apiService.registrarSupervisor(dto);

        call.enqueue(new Callback<RegistroSupervisorResponseDTO>() {
            @Override
            public void onResponse(Call<RegistroSupervisorResponseDTO> call, Response<RegistroSupervisorResponseDTO> response) {

                if (response.code() == 409) {
                    Toast.makeText(SupervisorCrear.this,
                            "El correo ya está registrado", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(SupervisorCrear.this,
                            "Error al registrar supervisor", Toast.LENGTH_SHORT).show();
                    return;
                }

                RegistroSupervisorResponseDTO resultado = response.body();

                Toast.makeText(SupervisorCrear.this,
                        "¡Supervisor registrado exitosamente!", Toast.LENGTH_LONG).show();

                irALogin();
            }

            @Override
            public void onFailure(Call<RegistroSupervisorResponseDTO> call, Throwable t) {
                Toast.makeText(SupervisorCrear.this,
                        "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void irALogin() {
        Intent intent = new Intent(SupervisorCrear.this, IniciarSesion.class);
        startActivity(intent);
        finish();
    }
}
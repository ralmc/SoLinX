package com.example.solinx;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.solinx.API.ApiClient;
import com.example.solinx.API.ApiService;
import com.example.solinx.DTO.RegistroEmpresaDTO;
import com.example.solinx.DTO.RegistroEmpresaResponseDTO;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmpresaCrear extends AppCompatActivity {

    TextInputEditText etNombreEmpresa, etCorreo, etConfirmarCorreo,
            etTelefono, etPassword, etConfirmarPassword;
    Button btnRegister;
    TextView tvLoginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_crear);

        etNombreEmpresa = findViewById(R.id.et_nombre_empresa);
        etCorreo = findViewById(R.id.et_email_empresa);
        etConfirmarCorreo = findViewById(R.id.et_confirm_email);
        etTelefono = findViewById(R.id.et_telefono);
        etPassword = findViewById(R.id.et_password_register);
        etConfirmarPassword = findViewById(R.id.et_confirm_password);

        btnRegister = findViewById(R.id.btn_register);
        tvLoginLink = findViewById(R.id.tv_login_link);

        btnRegister.setOnClickListener(v -> registrarEmpresa());
        tvLoginLink.setOnClickListener(v -> irALogin());
    }

    private void registrarEmpresa() {
        String nombreEmpresa = etNombreEmpresa.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String confirmarCorreo = etConfirmarCorreo.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmarPassword = etConfirmarPassword.getText().toString().trim();

        if (nombreEmpresa.isEmpty() || correo.isEmpty() || telefono.isEmpty() ||
                password.isEmpty() || confirmarCorreo.isEmpty() || confirmarPassword.isEmpty()) {
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

        RegistroEmpresaDTO dto = new RegistroEmpresaDTO(nombreEmpresa, correo, telefono, password);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<RegistroEmpresaResponseDTO> call = apiService.registrarEmpresa(dto);

        call.enqueue(new Callback<RegistroEmpresaResponseDTO>() {
            @Override
            public void onResponse(Call<RegistroEmpresaResponseDTO> call, Response<RegistroEmpresaResponseDTO> response) {

                if (response.code() == 409) {
                    Toast.makeText(EmpresaCrear.this,
                            "El correo ya está registrado", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(EmpresaCrear.this,
                            "Error al registrar empresa", Toast.LENGTH_SHORT).show();
                    return;
                }

                RegistroEmpresaResponseDTO resultado = response.body();

                Toast.makeText(EmpresaCrear.this,
                        "¡Empresa registrada exitosamente!", Toast.LENGTH_LONG).show();

                irALogin();
            }

            @Override
            public void onFailure(Call<RegistroEmpresaResponseDTO> call, Throwable t) {
                Toast.makeText(EmpresaCrear.this,
                        "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void irALogin() {
        Intent intent = new Intent(EmpresaCrear.this, IniciarSesion.class);
        startActivity(intent);
        finish();
    }
}
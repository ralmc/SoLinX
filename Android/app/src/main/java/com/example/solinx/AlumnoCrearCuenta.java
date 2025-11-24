package com.example.solinx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.solinx.API.ApiClient;
import com.example.solinx.API.ApiService;
import com.example.solinx.DTO.RegistroAlumnoDTO;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlumnoCrearCuenta extends AppCompatActivity implements View.OnClickListener {

    // Campos de texto
    TextInputEditText etNombreUsuario, etBoleta, etCorreo, etConfirmarCorreo,
            etContrasena, etConfirmarContrasena;

    // Dropdowns (AutoCompleteTextView)
    AutoCompleteTextView spEscuela, spCarrera;

    // Botón
    Button btnEnviar;

    // Link a login
    TextView tvLoginLink;

    // API Service
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alumno_crear_cuenta);

        // Inicializar API Service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Inicializar vistas
        etNombreUsuario = findViewById(R.id.etNombreUsuario);
        etBoleta = findViewById(R.id.etBoleta);
        etCorreo = findViewById(R.id.etCorreo);
        etConfirmarCorreo = findViewById(R.id.etConfirmarCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        etConfirmarContrasena = findViewById(R.id.etConfirmarContrasena);

        spEscuela = findViewById(R.id.spEscuela);
        spCarrera = findViewById(R.id.spCarrera);
        btnEnviar = findViewById(R.id.btnEnviar);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        btnEnviar.setOnClickListener(this);

        // Listener para ir al login
        tvLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(AlumnoCrearCuenta.this, IniciarSesion.class);
            startActivity(intent);
            finish();
        });

        // Configurar dropdowns con estilo moderno
        String[] escuelas = {
                "ESCOM",
                "UPIICSA",
                "UPIITA",
                "ESIA Ticomán",
                "ESIA Tecamachalco",
                "ESIME Azcapotzalco",
                "ESIME Zacatenco",
                "Cecyt 9",
                "UPIBI",
                "UPIIG"
        };

        String[] carreras = {
                "Ingeniería en Sistemas Computacionales",
                "Ingeniería en Software",
                "Ingeniería Industrial",
                "Ingeniería Mecatrónica",
                "Ingeniería Informática",
                "Ingeniería en Inteligencia Artificial",
                "Ingeniería Aeronáutica",
                "Ingeniería Biónica",
                "Programación"
        };

        // Adapter para Escuela
        ArrayAdapter<String> adapterEscuela = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                escuelas
        );
        spEscuela.setAdapter(adapterEscuela);

        // Adapter para Carrera
        ArrayAdapter<String> adapterCarrera = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                carreras
        );
        spCarrera.setAdapter(adapterCarrera);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnEnviar) {
            registrarAlumno();
        }
    }

    private void registrarAlumno() {
        // Obtener valores de los campos
        String nombreUsuario = etNombreUsuario.getText().toString().trim();
        String boletaStr = etBoleta.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String confirmarCorreo = etConfirmarCorreo.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();
        String confirmarContrasena = etConfirmarContrasena.getText().toString().trim();
        String escuela = spEscuela.getText().toString().trim();
        String carrera = spCarrera.getText().toString().trim();

        // Validaciones básicas
        if (nombreUsuario.isEmpty()) {
            Toast.makeText(this, "Ingresa tu nombre de usuario", Toast.LENGTH_SHORT).show();
            etNombreUsuario.requestFocus();
            return;
        }

        if (boletaStr.isEmpty()) {
            Toast.makeText(this, "Ingresa tu boleta", Toast.LENGTH_SHORT).show();
            etBoleta.requestFocus();
            return;
        }

        Integer boleta;
        try {
            boleta = Integer.parseInt(boletaStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "La boleta debe ser un número válido", Toast.LENGTH_SHORT).show();
            etBoleta.requestFocus();
            return;
        }

        // Validar que la boleta tenga exactamente 10 dígitos
        if (boletaStr.length() != 10) {
            Toast.makeText(this, "La boleta debe tener 10 dígitos", Toast.LENGTH_SHORT).show();
            etBoleta.requestFocus();
            return;
        }

        if (escuela.isEmpty()) {
            Toast.makeText(this, "Selecciona una escuela", Toast.LENGTH_SHORT).show();
            spEscuela.requestFocus();
            return;
        }

        if (carrera.isEmpty()) {
            Toast.makeText(this, "Selecciona una carrera", Toast.LENGTH_SHORT).show();
            spCarrera.requestFocus();
            return;
        }

        if (correo.isEmpty()) {
            Toast.makeText(this, "Ingresa tu correo", Toast.LENGTH_SHORT).show();
            etCorreo.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Ingresa un correo válido", Toast.LENGTH_SHORT).show();
            etCorreo.requestFocus();
            return;
        }

        if (confirmarCorreo.isEmpty()) {
            Toast.makeText(this, "Confirma tu correo", Toast.LENGTH_SHORT).show();
            etConfirmarCorreo.requestFocus();
            return;
        }

        if (!correo.equals(confirmarCorreo)) {
            Toast.makeText(this, "Los correos no coinciden", Toast.LENGTH_SHORT).show();
            etConfirmarCorreo.requestFocus();
            return;
        }

        if (contrasena.isEmpty()) {
            Toast.makeText(this, "Ingresa tu contraseña", Toast.LENGTH_SHORT).show();
            etContrasena.requestFocus();
            return;
        }

        if (contrasena.length() < 4) {
            Toast.makeText(this, "La contraseña debe tener al menos 4 caracteres", Toast.LENGTH_SHORT).show();
            etContrasena.requestFocus();
            return;
        }

        if (confirmarContrasena.isEmpty()) {
            Toast.makeText(this, "Confirma tu contraseña", Toast.LENGTH_SHORT).show();
            etConfirmarContrasena.requestFocus();
            return;
        }

        if (!contrasena.equals(confirmarContrasena)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            etConfirmarContrasena.requestFocus();
            return;
        }

        // Crear objeto DTO
        RegistroAlumnoDTO dto = new RegistroAlumnoDTO(
                nombreUsuario,
                boleta,
                carrera,
                escuela,
                correo,
                confirmarCorreo,
                contrasena,
                confirmarContrasena
        );

        // Deshabilitar botón mientras se procesa
        btnEnviar.setEnabled(false);
        btnEnviar.setText("Registrando...");

        // Hacer la petición HTTP
        Call<String> call = apiService.registrarAlumno(dto);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                // Rehabilitar botón
                btnEnviar.setEnabled(true);
                btnEnviar.setText("Crear Cuenta");

                if (response.isSuccessful() && response.body() != null) {
                    String mensaje = response.body();

                    if (mensaje.equals("Registro exitoso")) {
                        Toast.makeText(AlumnoCrearCuenta.this,
                                "¡Cuenta creada exitosamente!",
                                Toast.LENGTH_LONG).show();

                        // Regresar a MainActivity o a la pantalla de login
                        Intent intent = new Intent(AlumnoCrearCuenta.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Mostrar mensaje de error del servidor
                        Toast.makeText(AlumnoCrearCuenta.this,
                                mensaje,
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Leer el mensaje de error del servidor (para códigos 400, 404, etc.)
                    String mensajeError = "Error en el servidor";
                    try {
                        if (response.errorBody() != null) {
                            mensajeError = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        mensajeError = "Error: " + response.code();
                    }

                    Toast.makeText(AlumnoCrearCuenta.this,
                            mensajeError,
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Rehabilitar botón
                btnEnviar.setEnabled(true);
                btnEnviar.setText("Crear Cuenta");

                Toast.makeText(AlumnoCrearCuenta.this,
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
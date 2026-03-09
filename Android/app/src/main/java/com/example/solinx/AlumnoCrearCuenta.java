package com.example.solinx;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.solinx.API.ApiClient;
import com.example.solinx.API.ApiService;
import com.example.solinx.CONEXION.InicioHelper;
import com.example.solinx.DTO.RegistroAlumnoDTO;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlumnoCrearCuenta extends Fragment implements View.OnClickListener {

    private TextInputEditText etNombreUsuario, etBoleta, etCorreo, etConfirmarCorreo,
            etContrasena, etConfirmarContrasena;
    private AutoCompleteTextView spEscuela, spCarrera;
    private Button btnEnviar;
    private TextView tvLoginLink;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_alumno_crear_cuenta, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = ApiClient.getClient().create(ApiService.class);

        etNombreUsuario = view.findViewById(R.id.etNombreUsuario);
        etBoleta = view.findViewById(R.id.etBoleta);
        etCorreo = view.findViewById(R.id.etCorreo);
        etConfirmarCorreo = view.findViewById(R.id.etConfirmarCorreo);
        etContrasena = view.findViewById(R.id.etContrasena);
        etConfirmarContrasena = view.findViewById(R.id.etConfirmarContrasena);
        spEscuela = view.findViewById(R.id.spEscuela);
        spCarrera = view.findViewById(R.id.spCarrera);
        btnEnviar = view.findViewById(R.id.btnEnviar);
        tvLoginLink = view.findViewById(R.id.tvLoginLink);

        btnEnviar.setOnClickListener(this);

        // Ir a IniciarSesion
        tvLoginLink.setOnClickListener(v ->
                ((InicioHelper) requireActivity()).Iniciar());

        // Escuelas
        String[] escuelas = {
                "ESCOM", "UPIICSA", "UPIITA", "ESIA Ticomán",
                "ESIA Tecamachalco", "ESIME Azcapotzalco", "ESIME Zacatenco",
                "Cecyt 9", "UPIBI", "UPIIG"
        };

        // Carreras
        String[] carreras = {
                "Ingeniería en Sistemas Computacionales", "Ingeniería en Software",
                "Ingeniería Industrial", "Ingeniería Mecatrónica",
                "Ingeniería Informática", "Ingeniería en Inteligencia Artificial",
                "Ingeniería Aeronáutica", "Ingeniería Biónica", "Programación"
        };

        spEscuela.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, escuelas));
        spCarrera.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, carreras));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnEnviar) {
            registrarAlumno();
        }
    }

    private void registrarAlumno() {
        String nombreUsuario        = etNombreUsuario.getText().toString().trim();
        String boletaStr            = etBoleta.getText().toString().trim();
        String correo               = etCorreo.getText().toString().trim();
        String confirmarCorreo      = etConfirmarCorreo.getText().toString().trim();
        String contrasena           = etContrasena.getText().toString().trim();
        String confirmarContrasena  = etConfirmarContrasena.getText().toString().trim();
        String escuela              = spEscuela.getText().toString().trim();
        String carrera              = spCarrera.getText().toString().trim();

        if (nombreUsuario.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa tu nombre de usuario", Toast.LENGTH_SHORT).show();
            etNombreUsuario.requestFocus(); return;
        }
        if (boletaStr.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa tu boleta", Toast.LENGTH_SHORT).show();
            etBoleta.requestFocus(); return;
        }

        Integer boleta;
        try {
            boleta = Integer.parseInt(boletaStr);
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "La boleta debe ser un número válido", Toast.LENGTH_SHORT).show();
            etBoleta.requestFocus(); return;
        }

        if (boletaStr.length() != 10) {
            Toast.makeText(requireContext(), "La boleta debe tener 10 dígitos", Toast.LENGTH_SHORT).show();
            etBoleta.requestFocus(); return;
        }
        if (escuela.isEmpty()) {
            Toast.makeText(requireContext(), "Selecciona una escuela", Toast.LENGTH_SHORT).show();
            spEscuela.requestFocus(); return;
        }
        if (carrera.isEmpty()) {
            Toast.makeText(requireContext(), "Selecciona una carrera", Toast.LENGTH_SHORT).show();
            spCarrera.requestFocus(); return;
        }
        if (correo.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa tu correo", Toast.LENGTH_SHORT).show();
            etCorreo.requestFocus(); return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(requireContext(), "Ingresa un correo válido", Toast.LENGTH_SHORT).show();
            etCorreo.requestFocus(); return;
        }
        if (confirmarCorreo.isEmpty()) {
            Toast.makeText(requireContext(), "Confirma tu correo", Toast.LENGTH_SHORT).show();
            etConfirmarCorreo.requestFocus(); return;
        }
        if (!correo.equals(confirmarCorreo)) {
            Toast.makeText(requireContext(), "Los correos no coinciden", Toast.LENGTH_SHORT).show();
            etConfirmarCorreo.requestFocus(); return;
        }
        if (contrasena.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa tu contraseña", Toast.LENGTH_SHORT).show();
            etContrasena.requestFocus(); return;
        }
        if (contrasena.length() < 4) {
            Toast.makeText(requireContext(), "La contraseña debe tener al menos 4 caracteres", Toast.LENGTH_SHORT).show();
            etContrasena.requestFocus(); return;
        }
        if (confirmarContrasena.isEmpty()) {
            Toast.makeText(requireContext(), "Confirma tu contraseña", Toast.LENGTH_SHORT).show();
            etConfirmarContrasena.requestFocus(); return;
        }
        if (!contrasena.equals(confirmarContrasena)) {
            Toast.makeText(requireContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            etConfirmarContrasena.requestFocus(); return;
        }

        RegistroAlumnoDTO dto = new RegistroAlumnoDTO(
                nombreUsuario, boleta, carrera, escuela,
                correo, confirmarCorreo, contrasena, confirmarContrasena
        );

        btnEnviar.setEnabled(false);
        btnEnviar.setText("Registrando...");

        Integer finalBoleta = boleta;
        apiService.registrarAlumno(dto).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call,
                                   @NonNull Response<String> response) {
                btnEnviar.setEnabled(true);
                btnEnviar.setText("Crear Cuenta");

                if (response.isSuccessful() && response.body() != null) {
                    String mensaje = response.body();
                    if (mensaje.equals("Registro exitoso")) {
                        Toast.makeText(requireContext(),
                                "¡Cuenta creada exitosamente!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(requireContext(), InicioHelper.class);
                        intent.putExtra("boleta", finalBoleta);
                        startActivity(intent);
                        requireActivity().finish();
                    } else {
                        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show();
                    }
                } else {
                    String mensajeError = "Error en el servidor";
                    try {
                        if (response.errorBody() != null)
                            mensajeError = response.errorBody().string();
                    } catch (Exception e) {
                        mensajeError = "Error: " + response.code();
                    }
                    Toast.makeText(requireContext(), mensajeError, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                btnEnviar.setEnabled(true);
                btnEnviar.setText("Crear Cuenta");
                Toast.makeText(requireContext(),
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
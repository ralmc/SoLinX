package com.example.solinx;

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

import com.example.solinx.CONEXION.InicioHelper;
import com.google.android.material.textfield.TextInputEditText;

public class AlumnoCrearCuenta extends Fragment {

    private TextInputEditText etNombreUsuario, etBoleta, etCorreo, etConfirmarCorreo,
            etContrasena, etConfirmarContrasena;
    private AutoCompleteTextView spEscuela, spCarrera;
    private Button btnEnviar;

    private static final String[] ESCUELAS = {
            "ESCOM", "UPIICSA", "UPIITA", "ESIA Ticomán",
            "ESIA Tecamachalco", "ESIME Azcapotzalco", "ESIME Zacatenco",
            "CECyT 9", "UPIBI", "UPIIG"
    };

    private static final String[] CARRERAS = {
            "Ingeniería en Sistemas Computacionales", "Ingeniería en Software",
            "Ingeniería Industrial", "Ingeniería Mecatrónica",
            "Ingeniería Informática", "Ingeniería en Inteligencia Artificial",
            "Ingeniería Aeronáutica", "Ingeniería Biónica", "Programación"
    };

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

        etNombreUsuario       = view.findViewById(R.id.etNombreUsuario);
        etBoleta              = view.findViewById(R.id.etBoleta);
        etCorreo              = view.findViewById(R.id.etCorreo);
        etConfirmarCorreo     = view.findViewById(R.id.etConfirmarCorreo);
        etContrasena          = view.findViewById(R.id.etContrasena);
        etConfirmarContrasena = view.findViewById(R.id.etConfirmarContrasena);
        spEscuela             = view.findViewById(R.id.spEscuela);
        spCarrera             = view.findViewById(R.id.spCarrera);
        btnEnviar             = view.findViewById(R.id.btnEnviar);

        TextView tvLoginLink = view.findViewById(R.id.tvLoginLink);
        tvLoginLink.setOnClickListener(v ->
                ((InicioHelper) requireActivity()).mostrarIniciarSesion());
        btnEnviar.setOnClickListener(v -> validarYContinuar());

        spEscuela.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, ESCUELAS));
        spCarrera.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, CARRERAS));

        // Botón flecha de regreso (en fragments, regresa al activity anterior)
        try {
            android.widget.ImageButton btnRegresarFlecha = view.findViewById(R.id.btnRegresarFlecha);
            if (btnRegresarFlecha != null) {
                btnRegresarFlecha.setOnClickListener(v -> {
                    if (getActivity() != null) getActivity().finish();
                });
            }
        } catch (Exception ignored) {}
    }

    private void validarYContinuar() {
        String nombreUsuario = etNombreUsuario.getText().toString().trim();
        String boletaStr = etBoleta.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String confirmarCorreo = etConfirmarCorreo.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();
        String confirmarContrasena = etConfirmarContrasena.getText().toString().trim();
        String escuela = spEscuela.getText().toString().trim();
        String carrera = spCarrera.getText().toString().trim();

        if (nombreUsuario.isEmpty()) { showError(etNombreUsuario, "Ingresa tu nombre de usuario"); return; }
        if (boletaStr.isEmpty()) { showError(etBoleta, "Ingresa tu boleta"); return; }
        if (boletaStr.length() != 10) { showError(etBoleta, "La boleta debe tener 10 dígitos"); return; }

        Integer boleta;
        try {
            boleta = Integer.parseInt(boletaStr);
        } catch (NumberFormatException e) {
            showError(etBoleta, "La boleta debe ser un número válido");
            return;
        }

        if (escuela.isEmpty()) { showToast("Selecciona una escuela"); spEscuela.requestFocus(); return; }
        if (carrera.isEmpty()) { showToast("Selecciona una carrera"); spCarrera.requestFocus(); return; }

        if (correo.isEmpty()) { showError(etCorreo, "Ingresa tu correo"); return; }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) { showError(etCorreo, "Ingresa un correo válido"); return; }
        if (confirmarCorreo.isEmpty()) { showError(etConfirmarCorreo, "Confirma tu correo"); return; }
        if (!correo.equals(confirmarCorreo)) { showError(etConfirmarCorreo, "Los correos no coinciden"); return; }

        if (contrasena.isEmpty()) { showError(etContrasena, "Ingresa tu contraseña"); return; }
        if (!esPasswordSeguro(contrasena)) {
            showError(etContrasena, "Mínimo 8 caracteres, una mayúscula, minúscula, número y símbolo (@$!%*?&)");
            return;
        }
        if (confirmarContrasena.isEmpty()) { showError(etConfirmarContrasena, "Confirma tu contraseña"); return; }
        if (!contrasena.equals(confirmarContrasena)) { showError(etConfirmarContrasena, "Las contraseñas no coinciden"); return; }

        Bundle bundle = new Bundle();
        bundle.putString("nombreUsuario",       nombreUsuario);
        bundle.putInt   ("boleta",              boleta);
        bundle.putString("carrera",             carrera);
        bundle.putString("escuela",             escuela);
        bundle.putString("correo",              correo);
        bundle.putString("confirmarCorreo",     confirmarCorreo);
        bundle.putString("contrasena",          contrasena);
        bundle.putString("confirmarContrasena", confirmarContrasena);

        ((InicioHelper) requireActivity()).mostrarHorarioConDatos(bundle);
    }

    private boolean esPasswordSeguro(String password) {
        if (password == null) return false;
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*\\d.*") &&
                password.matches(".*[@$!%*?&].*");
    }

    private void showError(TextInputEditText campo, String mensaje) {
        showToast(mensaje);
        campo.requestFocus();
    }

    private void showToast(String msg) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
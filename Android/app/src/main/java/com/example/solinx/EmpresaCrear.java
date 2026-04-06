package com.example.solinx;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.solinx.API.ApiClient;
import com.example.solinx.API.ApiService;
import com.example.solinx.CONEXION.InicioHelper;
import com.example.solinx.DTO.RegistroEmpresaDTO;
import com.example.solinx.DTO.RegistroEmpresaResponseDTO;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmpresaCrear extends Fragment {

    private TextInputEditText etNombreEmpresa, etCorreo, etConfirmarCorreo,
            etTelefono, etPassword, etConfirmarPassword;
    private Button btnRegister;
    private TextView tvLoginLink;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_empresa_crear, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etNombreEmpresa    = view.findViewById(R.id.et_nombre_empresa);
        etCorreo           = view.findViewById(R.id.et_email_empresa);
        etConfirmarCorreo  = view.findViewById(R.id.et_confirm_email);
        etTelefono         = view.findViewById(R.id.et_telefono);
        etPassword         = view.findViewById(R.id.et_password_register);
        etConfirmarPassword = view.findViewById(R.id.et_confirm_password);
        btnRegister        = view.findViewById(R.id.btn_register);
        tvLoginLink        = view.findViewById(R.id.tv_login_link);

        btnRegister.setOnClickListener(v -> registrarEmpresa());

        tvLoginLink.setOnClickListener(v ->
                ((InicioHelper) requireActivity()).mostrarIniciarSesion()
        );
    }

    private void registrarEmpresa() {
        String nombreEmpresa    = etNombreEmpresa.getText().toString().trim();
        String correo           = etCorreo.getText().toString().trim();
        String confirmarCorreo  = etConfirmarCorreo.getText().toString().trim();
        String telefono         = etTelefono.getText().toString().trim();
        String password         = etPassword.getText().toString().trim();
        String confirmarPassword = etConfirmarPassword.getText().toString().trim();

        if (nombreEmpresa.isEmpty() || correo.isEmpty() || telefono.isEmpty() ||
                password.isEmpty() || confirmarCorreo.isEmpty() || confirmarPassword.isEmpty()) {
            Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!correo.equals(confirmarCorreo)) {
            Toast.makeText(requireContext(), "Los correos no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmarPassword)) {
            Toast.makeText(requireContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (telefono.length() != 10) {
            Toast.makeText(requireContext(), "El teléfono debe tener 10 dígitos", Toast.LENGTH_SHORT).show();
            return;
        }

        RegistroEmpresaDTO dto = new RegistroEmpresaDTO(nombreEmpresa, correo, telefono, password);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.registrarEmpresa(dto).enqueue(new Callback<RegistroEmpresaResponseDTO>() {
            @Override
            public void onResponse(@NonNull Call<RegistroEmpresaResponseDTO> call,
                                   @NonNull Response<RegistroEmpresaResponseDTO> response) {
                if (response.code() == 409) {
                    Toast.makeText(requireContext(),
                            "El correo ya está registrado", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(requireContext(),
                            "Error al registrar empresa", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(requireContext(),
                        "¡Empresa registrada exitosamente!", Toast.LENGTH_LONG).show();

                ((InicioHelper) requireActivity()).mostrarIniciarSesion();
            }

            @Override
            public void onFailure(@NonNull Call<RegistroEmpresaResponseDTO> call,
                                  @NonNull Throwable t) {
                Toast.makeText(requireContext(),
                        "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
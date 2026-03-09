package com.example.solinx;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.example.solinx.DTO.LoginDTO;
import com.example.solinx.DTO.LoginResponseDTO;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class IniciarSesion extends Fragment {

    private static final String TAG = "IniciarSesion";

    private TextInputEditText etUsuario, etContrasena;
    private TextView tvRegistroLink;
    private Button btnEnviar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_iniciar_sesion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvRegistroLink = view.findViewById(R.id.tvRegistroLink);
        etUsuario      = view.findViewById(R.id.etUsuario);
        etContrasena   = view.findViewById(R.id.etContrasena);
        btnEnviar      = view.findViewById(R.id.btnEnviar);

        btnEnviar.setOnClickListener(v -> hacerLogin());
        tvRegistroLink.setOnClickListener(v ->
                ((InicioHelper)requireActivity()).Crear());
    }
    private void hacerLogin() {
        String correo   = etUsuario.getText().toString().trim();
        String password = etContrasena.getText().toString().trim();

        if (correo.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Completa ambos campos", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginDTO loginDTO = new LoginDTO(correo, password);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<LoginResponseDTO> call = apiService.login(loginDTO);

        call.enqueue(new Callback<LoginResponseDTO>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponseDTO> call,
                                   @NonNull Response<LoginResponseDTO> response) {

                if (response.code() == 401) {
                    Toast.makeText(requireContext(), "Contraseña/Email Incorrectos", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(requireContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                    return;
                }

                LoginResponseDTO loginResponse = response.body();
                Log.d(TAG, "Login exitoso - Usuario: " + loginResponse.getNombre());
                Log.d(TAG, "Rol: " + loginResponse.getRol());

                guardarSesionBasica(loginResponse);

                String rol = loginResponse.getRol();

                if ("estudiante".equalsIgnoreCase(rol)) {
                    guardarDatosEstudiante(loginResponse);
                    navegarVistaAlumno(loginResponse);
                } else if ("empresa".equalsIgnoreCase(rol)) {
                    navegarVistaEmpresa(loginResponse);
                } else if ("supervisor".equalsIgnoreCase(rol)) {
                    navegarVistaSupervisor(loginResponse);
                } else {
                    Toast.makeText(requireContext(), "Rol no permitido: " + rol, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponseDTO> call, @NonNull Throwable t) {
                Log.e(TAG, "Error de red: " + t.getMessage());
                Toast.makeText(requireContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void guardarSesionBasica(LoginResponseDTO loginResponse) {
        SharedPreferences preferences = requireActivity().getSharedPreferences("sesion_usuario", MODE_PRIVATE);
        preferences.edit()
                .putInt("idUsuario", loginResponse.getIdUsuario())
                .putString("rol", loginResponse.getRol())
                .putString("nombre", loginResponse.getNombre())
                .putString("correo", loginResponse.getCorreo())
                .apply();
        Log.d(TAG, "Sesión básica guardada");
    }

    private void guardarDatosEstudiante(LoginResponseDTO loginResponse) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("SoLinXPrefs", MODE_PRIVATE);
        String boleta = loginResponse.getBoleta() != null ?
                loginResponse.getBoleta().toString() : "N/A";
        prefs.edit()
                .putString("boleta", boleta)
                .putString("nombre", loginResponse.getNombre())
                .putString("correo", loginResponse.getCorreo())
                .putString("carrera", loginResponse.getCarrera() != null ? loginResponse.getCarrera() : "N/A")
                .putString("escuela", loginResponse.getEscuela() != null ? loginResponse.getEscuela() : "N/A")
                .putString("telefono", loginResponse.getTelefono() != null ? loginResponse.getTelefono() : "N/A")
                .apply();
        Log.d(TAG, "Datos del estudiante guardados - Boleta: " + boleta);
    }

    private void navegarVistaAlumno(LoginResponseDTO loginResponse) {
        Intent intent = new Intent(requireContext(), AlumnoMenuEmpresas.class);
        intent.putExtra("idUsuario", loginResponse.getIdUsuario());
        intent.putExtra("nombre", loginResponse.getNombre());
        intent.putExtra("correo", loginResponse.getCorreo());
        intent.putExtra("rol", loginResponse.getRol());
        Log.d(TAG, "Navegando a AlumnoMenuEmpresas");
        startActivity(intent);
        requireActivity().finish();
    }

    private void navegarVistaEmpresa(LoginResponseDTO loginResponse) {
        Integer idEmpresa = loginResponse.getIdEmpresa();

        if (idEmpresa == null || idEmpresa == 0) {
            Log.e(TAG, "ERROR: idEmpresa es null o 0");
            Toast.makeText(requireContext(), "Error crítico: ID Empresa no encontrado.", Toast.LENGTH_LONG).show();
            return;
        }

        requireActivity().getSharedPreferences("sesion_usuario", MODE_PRIVATE)
                .edit()
                .putInt("id_empresa_activa", idEmpresa)
                .apply();

        Intent intent = new Intent(requireContext(), EmpresaVista.class);
        intent.putExtra("ID_EMPRESA_ACTUAL", idEmpresa);
        intent.putExtra("nombre", loginResponse.getNombre());
        Log.d(TAG, "Navegando a EmpresaVista con idEmpresa: " + idEmpresa);
        startActivity(intent);
        requireActivity().finish();
    }

    private void navegarVistaSupervisor(LoginResponseDTO loginResponse) {
        Integer idSupervisor = loginResponse.getIdSupervisor();
        Integer idEmpresa    = loginResponse.getIdEmpresa();

        if (idSupervisor == null || idSupervisor == 0) {
            Log.e(TAG, "ERROR: idSupervisor es null o 0");
            Toast.makeText(requireContext(), "Error: Datos de supervisor no encontrados.", Toast.LENGTH_LONG).show();
            return;
        }

        requireActivity().getSharedPreferences("sesion_usuario", MODE_PRIVATE)
                .edit()
                .putInt("idSupervisor", idSupervisor)
                .putInt("idEmpresa", idEmpresa != null ? idEmpresa : -1)
                .putString("area", loginResponse.getArea())
                .apply();

        Intent intent = new Intent(requireContext(), MenuSupervisorActivity.class);
        intent.putExtra("idUsuario", loginResponse.getIdUsuario());
        intent.putExtra("idSupervisor", idSupervisor);
        intent.putExtra("idEmpresa", idEmpresa);
        intent.putExtra("nombre", loginResponse.getNombre());
        intent.putExtra("correo", loginResponse.getCorreo());
        intent.putExtra("area", loginResponse.getArea());
        intent.putExtra("rol", loginResponse.getRol());
        Log.d(TAG, "Navegando a MenuSupervisorActivity");
        startActivity(intent);
        requireActivity().finish();
    }
}
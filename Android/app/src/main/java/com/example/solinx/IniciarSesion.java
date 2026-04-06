package com.example.solinx;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
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
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class IniciarSesion extends Fragment {

    private static final String TAG          = "IniciarSesion";
    private static final String PREFS_SESION = "sesion_usuario";
    private static final String PREFS_SOLINX = "SoLinXPrefs";

    private TextInputLayout   tilUsuario, tilContrasena;
    private TextInputEditText etUsuario, etContrasena;
    private Button            btnEnviar;

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

        tilUsuario    = view.findViewById(R.id.til_usuario);
        tilContrasena = view.findViewById(R.id.til_contrasena);
        etUsuario     = view.findViewById(R.id.etUsuario);
        etContrasena  = view.findViewById(R.id.etContrasena);
        btnEnviar     = view.findViewById(R.id.btnEnviar);

        TextView tvRegistroLink = view.findViewById(R.id.tvRegistroLink);
        btnEnviar.setOnClickListener(v -> hacerLogin());
        tvRegistroLink.setOnClickListener(v ->
                ((InicioHelper) requireActivity()).mostrarCrearCuenta());
    }

    // ─── Validación ────────────────────────────────────────────────────────────
    private boolean validarCampos(String correo, String password) {
        boolean valido = true;
        if (correo.isEmpty()) {
            tilUsuario.setError("Ingresa tu correo"); valido = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            tilUsuario.setError("Correo no válido"); valido = false;
        } else {
            tilUsuario.setError(null);
        }
        if (password.isEmpty()) {
            tilContrasena.setError("Ingresa tu contraseña"); valido = false;
        } else {
            tilContrasena.setError(null);
        }
        return valido;
    }

    // ─── Login ─────────────────────────────────────────────────────────────────
    private void hacerLogin() {
        String correo   = etUsuario.getText().toString().trim();
        String password = etContrasena.getText().toString().trim();

        if (!validarCampos(correo, password)) return;

        setLoading(true);

        ApiClient.getClient().create(ApiService.class)
                .login(new LoginDTO(correo, password))
                .enqueue(new Callback<LoginResponseDTO>() {
                    @Override
                    public void onResponse(@NonNull Call<LoginResponseDTO> call,
                                           @NonNull Response<LoginResponseDTO> response) {
                        setLoading(false);
                        if (response.code() == 401) { showToast("Correo o contraseña incorrectos"); return; }
                        if (!response.isSuccessful() || response.body() == null) { showToast("Error al conectar con el servidor"); return; }
                        manejarLoginExitoso(response.body());
                    }

                    @Override
                    public void onFailure(@NonNull Call<LoginResponseDTO> call, @NonNull Throwable t) {
                        setLoading(false);
                        Log.e(TAG, "Error de red", t);
                        showToast("Sin conexión: " + t.getMessage());
                    }
                });
    }

    private void manejarLoginExitoso(LoginResponseDTO resp) {
        Log.d(TAG, "Login OK — usuario: " + resp.getNombre() + " | rol: " + resp.getRol());
        guardarSesionBasica(resp);

        switch (resp.getRol().toLowerCase()) {
            case "estudiante":
                guardarDatosEstudiante(resp);
                navegarA(AlumnoMenuEmpresas.class, resp, false);
                break;
            case "empresa":
                if (!idValido(resp.getIdEmpresa())) { showToast("Error crítico: ID de empresa no encontrado"); return; }
                guardarIdEmpresa(resp.getIdEmpresa());
                navegarA(EmpresaVista.class, resp, true);
                break;
            case "supervisor":
                if (!idValido(resp.getIdSupervisor())) { showToast("Error: Datos de supervisor no encontrados"); return; }
                guardarDatosSupervisor(resp);
                navegarA(MenuSupervisorActivity.class, resp, true);
                break;
            default:
                showToast("Rol no permitido: " + resp.getRol());
        }
    }

    // ─── Persistencia ──────────────────────────────────────────────────────────
    private void guardarSesionBasica(LoginResponseDTO r) {
        prefs(PREFS_SESION).edit()
                .putInt("idUsuario", r.getIdUsuario())
                .putString("rol",    r.getRol())
                .putString("nombre", r.getNombre())
                .putString("correo", r.getCorreo())
                .apply();
    }

    private void guardarDatosEstudiante(LoginResponseDTO r) {
        String boleta = r.getBoleta() != null ? r.getBoleta().toString() : "N/A";
        prefs(PREFS_SOLINX).edit()
                .putString("boleta",   boleta)
                .putString("carrera",  orNA(r.getCarrera()))
                .putString("escuela",  orNA(r.getEscuela()))
                .putString("telefono", orNA(r.getTelefono()))
                .apply();
    }

    private void guardarIdEmpresa(int idEmpresa) {
        prefs(PREFS_SESION).edit().putInt("id_empresa_activa", idEmpresa).apply();
    }

    private void guardarDatosSupervisor(LoginResponseDTO r) {
        prefs(PREFS_SESION).edit()
                .putInt("idSupervisor", r.getIdSupervisor())
                .putInt("idEmpresa",    r.getIdEmpresa() != null ? r.getIdEmpresa() : -1)
                .putString("area",      r.getArea())
                .apply();
    }

    // ─── Navegación ────────────────────────────────────────────────────────────
    private void navegarA(Class<?> destino, LoginResponseDTO r, boolean incluirExtras) {
        Intent intent = new Intent(requireContext(), destino);
        if (incluirExtras) {
            intent.putExtra("idUsuario",         r.getIdUsuario());
            intent.putExtra("nombre",            r.getNombre());
            intent.putExtra("correo",            r.getCorreo());
            intent.putExtra("rol",               r.getRol());
            intent.putExtra("idSupervisor",      r.getIdSupervisor());
            intent.putExtra("idEmpresa",         r.getIdEmpresa());
            intent.putExtra("area",              r.getArea());
            intent.putExtra("ID_EMPRESA_ACTUAL", r.getIdEmpresa());
        }
        Log.d(TAG, "Navegando a " + destino.getSimpleName());
        startActivity(intent);
        requireActivity().finish();
    }

    // ─── Helpers ───────────────────────────────────────────────────────────────
    private SharedPreferences prefs(String name) {
        return requireActivity().getSharedPreferences(name, MODE_PRIVATE);
    }

    private void setLoading(boolean loading) {
        btnEnviar.setEnabled(!loading);
        btnEnviar.setText(loading ? "Entrando..." : "Enviar");
    }

    private void showToast(String msg) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private boolean idValido(Integer id) { return id != null && id != 0; }

    private String orNA(String val) { return val != null ? val : "N/A"; }
}
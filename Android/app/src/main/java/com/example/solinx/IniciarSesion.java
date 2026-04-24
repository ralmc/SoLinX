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
import androidx.appcompat.app.AlertDialog;
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

        try {
            android.widget.ImageButton btnRegresarFlecha = view.findViewById(R.id.btnRegresarFlecha);
            if (btnRegresarFlecha != null) {
                btnRegresarFlecha.setOnClickListener(v -> {
                    if (getActivity() != null) getActivity().finish();
                });
            }
        } catch (Exception ignored) {}
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

                        if (response.code() == 401) {
                            String motivo = response.headers().get("X-Login-Error");
                            if ("NO_VERIFICADO".equals(motivo)) {
                                mostrarDialogoNoVerificado(correo);
                            } else {
                                showToast("Correo o contraseña incorrectos");
                            }
                            return;
                        }

                        if (!response.isSuccessful() || response.body() == null) {
                            showToast("Error al conectar con el servidor");
                            return;
                        }

                        LoginResponseDTO loginResponse = response.body();
                        Log.d(TAG, "Login exitoso - Usuario: " + loginResponse.getNombre() + " | Rol: " + loginResponse.getRol());
                        guardarSesionBasica(loginResponse);
                        cargarYAplicarTema(loginResponse);
                    }

                    @Override
                    public void onFailure(@NonNull Call<LoginResponseDTO> call,
                                          @NonNull Throwable t) {
                        setLoading(false);
                        Log.e(TAG, "Error de red", t);
                        showToast("Sin conexión: " + t.getMessage());
                    }
                });
    }

    // ─── Diálogo cuenta no verificada ──────────────────────────────────────────
    private void mostrarDialogoNoVerificado(String correo) {
        if (getContext() == null) return;

        new AlertDialog.Builder(requireContext())
                .setTitle("Cuenta no verificada")
                .setMessage("Tu cuenta aún no ha sido verificada.\n\nRevisa tu bandeja de entrada (y spam) o solicita un nuevo enlace.")
                .setPositiveButton("Reenviar correo", (dialog, which) ->
                        reenviarCorreoVerificacion(correo))
                .setNegativeButton("Cerrar", null)
                .show();
    }

    private void reenviarCorreoVerificacion(String correo) {
        setLoading(true);

        ApiClient.getClient().create(ApiService.class)
                .reenviarVerificacion(correo)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call,
                                           @NonNull Response<String> response) {
                        setLoading(false);
                        if (response.isSuccessful()) {
                            showToast("Correo de verificación enviado");
                        } else {
                            showToast("No se pudo reenviar el correo");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call,
                                          @NonNull Throwable t) {
                        setLoading(false);
                        showToast("Sin conexión: " + t.getMessage());
                    }
                });
    }

    // ─── Aplicar Tema y Navegar  ───────────────────────────────────────────────
    private void cargarYAplicarTema(LoginResponseDTO loginResponse) {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.obtenerPerfil(loginResponse.getIdUsuario()).enqueue(new Callback<com.example.solinx.DTO.PerfilDTO>() {
            @Override
            public void onResponse(@NonNull Call<com.example.solinx.DTO.PerfilDTO> call,
                                   @NonNull Response<com.example.solinx.DTO.PerfilDTO> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getTema() != null) {
                    String tema = response.body().getTema();
                    Log.d(TAG, "Tema recibido de la BD: " + tema);
                    com.example.solinx.UTIL.ThemeUtils.applyThemeFromBackend(requireContext(), tema);
                } else {
                    Log.d(TAG, "Sin tema en BD, usando claro por defecto");
                    com.example.solinx.UTIL.ThemeUtils.forceLightModeLocal(requireContext());
                }
                manejarLoginExitoso(loginResponse);
            }

            @Override
            public void onFailure(@NonNull Call<com.example.solinx.DTO.PerfilDTO> call, @NonNull Throwable t) {
                Log.e(TAG, "Error al cargar tema: " + t.getMessage());
                // Navegar igual con tema default
                manejarLoginExitoso(loginResponse);
            }
        });
    }

    private void manejarLoginExitoso(LoginResponseDTO resp) {
        switch (resp.getRol().toLowerCase()) {
            case "estudiante":
                guardarDatosEstudiante(resp);
                navegarA(AlumnoMenuEmpresas.class, resp, true);
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
                // Se agregan los datos completos de ambas versiones
                .putString("nombre",   orNA(r.getNombre()))
                .putString("correo",   orNA(r.getCorreo()))
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
package com.example.solinx;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.solinx.API.ApiClient;
import com.example.solinx.API.ApiService;
import com.example.solinx.RESPONSE.SupervisorResponse;
import com.example.solinx.UTIL.ThemeUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupervisorVistaCuenta extends AppCompatActivity {

    private static final String TAG = "SupervisorVistaCuenta";
    private static final String CORREO_SOPORTE = "solinx.soporte@gmail.com";

    private ImageButton btnRegresar;
    private TextView tvNombre, tvCorreo, tvTelefono;
    private TextView btnContactarSoporte, btnCerrarSesion;
    private ImageView imgPerfil;
    private View viewModoClaro, viewModoOscuro;

    private Integer idUsuario;
    private String nombreSupervisor = "";
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_vista_cuenta);

        preferences = getSharedPreferences("sesion_usuario", MODE_PRIVATE);
        idUsuario = preferences.getInt("idUsuario", -1);

        initViews();
        cargarDatosSupervisor();
        setupListeners();
        actualizarIndicadoresTema();
    }

    private void initViews() {
        btnRegresar         = findViewById(R.id.regresar);
        tvNombre            = findViewById(R.id.tvNombre);
        tvCorreo            = findViewById(R.id.tvCorreo);
        tvTelefono          = findViewById(R.id.tvTelefono);
        imgPerfil           = findViewById(R.id.imgPerfil);
        viewModoClaro       = findViewById(R.id.viewModoClaro);
        viewModoOscuro      = findViewById(R.id.viewModoOscuro);
        btnContactarSoporte = findViewById(R.id.btnContactarSoporte);
        btnCerrarSesion     = findViewById(R.id.btnCerrarSesion);
    }

    private void cargarDatosSupervisor() {
        if (isFinishing() || isDestroyed()) return;
        if (idUsuario == -1) {
            if (!isFinishing() && !isDestroyed()) {
                Toast.makeText(this, "Sesión no válida", Toast.LENGTH_SHORT).show();
            }
            finish();
            return;
        }

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getSupervisorData(idUsuario).enqueue(new Callback<SupervisorResponse>() {
            @Override
            public void onResponse(@NonNull Call<SupervisorResponse> call,
                                   @NonNull Response<SupervisorResponse> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()
                        && response.body().getSupervisor() != null) {

                    Supervisor s = response.body().getSupervisor();
                    nombreSupervisor = s.getNombre() != null ? s.getNombre() : "";
                    runOnUiThread(() -> {
                        tvNombre.setText(s.getNombre() != null ? s.getNombre() : "N/A");
                        tvCorreo.setText(s.getCorreo() != null ? s.getCorreo() : "N/A");
                        tvTelefono.setText(s.getTelefono() != null ? s.getTelefono() : "N/A");
                    });
                } else {
                    Toast.makeText(SupervisorVistaCuenta.this,
                            "No se pudieron cargar los datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<SupervisorResponse> call, @NonNull Throwable t) {
                Toast.makeText(SupervisorVistaCuenta.this,
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        btnRegresar.setOnClickListener(v -> finish());

        viewModoClaro.setOnClickListener(v -> {
            ThemeUtils.setLightMode(this);
            actualizarIndicadoresTema();
            Toast.makeText(this, "Modo claro activado", Toast.LENGTH_SHORT).show();
            recreate();
        });

        viewModoOscuro.setOnClickListener(v -> {
            ThemeUtils.setDarkMode(this);
            actualizarIndicadoresTema();
            Toast.makeText(this, "Modo oscuro activado", Toast.LENGTH_SHORT).show();
            recreate();
        });

        if (btnContactarSoporte != null) {
            btnContactarSoporte.setOnClickListener(v -> contactarSoporte());
        }

        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
    }

    private void actualizarIndicadoresTema() {
        if (ThemeUtils.isDarkMode(this)) {
            viewModoClaro.setAlpha(0.5f);
            viewModoOscuro.setAlpha(1.0f);
        } else {
            viewModoClaro.setAlpha(1.0f);
            viewModoOscuro.setAlpha(0.5f);
        }
    }

    private void contactarSoporte() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Contactar Soporte");

        final EditText input = new EditText(this);
        input.setHint("Describe tu problema o consulta...");
        input.setMinLines(3);
        input.setPadding(40, 20, 40, 20);
        builder.setView(input);

        builder.setPositiveButton("Enviar", (dialog, which) -> {
            String mensaje = input.getText().toString().trim();
            if (mensaje.isEmpty()) {
                Toast.makeText(this, "Escribe un mensaje", Toast.LENGTH_SHORT).show();
                return;
            }

            String cuerpo = "Supervisor: " + nombreSupervisor + "\n"
                    + "Correo: " + tvCorreo.getText().toString() + "\n\n"
                    + "Mensaje:\n" + mensaje;

            String subject = "SoLinX - Soporte Supervisor: " + nombreSupervisor;

            String mailtoUri = "mailto:" + CORREO_SOPORTE
                    + "?subject=" + Uri.encode(subject)
                    + "&body=" + Uri.encode(cuerpo);

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse(mailtoUri));

            try {
                startActivity(Intent.createChooser(emailIntent, "Enviar correo con..."));
            } catch (Exception e) {
                Toast.makeText(this, "No hay app de correo disponible", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void cerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Seguro que quieres salir?")
                .setPositiveButton("Sí", (d, w) -> {
                    // 1. Primero forzar modo claro (puede disparar recreate si estaba en oscuro)
                    try {
                        ThemeUtils.forceLightModeLocal(this);
                    } catch (Exception ignored) {}

                    // 2. Lanzar MainActivity con CLEAR_TASK ANTES de limpiar las prefs.
                    //    Esto destruye esta activity y toda la pila.
                    try {
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } catch (Exception ignored) {}

                    // 3. Limpiar las prefs con un pequeño retardo para que cualquier
                    //    recreate ya haya terminado y no lea prefs vacías.
                    new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                        try {
                            getSharedPreferences("sesion_usuario", MODE_PRIVATE).edit().clear().apply();
                        } catch (Exception ignored) {}
                    }, 300);

                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}

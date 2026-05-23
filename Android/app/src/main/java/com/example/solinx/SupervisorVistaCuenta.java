package com.example.solinx;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import com.example.solinx.DTO.PerfilDTO;
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
    private Button btnContactarSoporte, btnCerrarSesion;
    private ImageView imgPerfil;
    private View viewModoClaro, viewModoOscuro;

    private Integer idUsuario;
    private String nombreSupervisor = "";
    private String correoSupervisor = "";
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_vista_cuenta);

        preferences = getSharedPreferences("sesion_usuario", MODE_PRIVATE);
        idUsuario   = preferences.getInt("idUsuario", -1);

        initViews();
        cargarDatosSupervisor();
        cargarFotoPerfil();
        setupListeners();
        actualizarIndicadoresTema();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarFotoPerfil();
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
        if (idUsuario == -1) { finish(); return; }

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
                    correoSupervisor = s.getCorreo() != null ? s.getCorreo() : "";

                    runOnUiThread(() -> {
                        tvNombre.setText(!nombreSupervisor.isEmpty() ? nombreSupervisor : "N/A");
                        tvCorreo.setText(!correoSupervisor.isEmpty() ? correoSupervisor : "N/A");
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
                        "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarFotoPerfil() {
        if (idUsuario == null || idUsuario == -1 || imgPerfil == null) return;
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.obtenerPerfil(idUsuario).enqueue(new Callback<PerfilDTO>() {
            @Override
            public void onResponse(@NonNull Call<PerfilDTO> call,
                                   @NonNull Response<PerfilDTO> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getFoto() != null
                        && !response.body().getFoto().isEmpty()) {
                    byte[] bytes = Base64.decode(response.body().getFoto(), Base64.DEFAULT);
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    runOnUiThread(() -> imgPerfil.setImageBitmap(bmp));
                }
            }
            @Override
            public void onFailure(@NonNull Call<PerfilDTO> call, @NonNull Throwable t) {
                Log.e(TAG, "Error foto: " + t.getMessage());
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

        if (btnContactarSoporte != null)
            btnContactarSoporte.setOnClickListener(v -> contactarSoporte());

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

    // ─── SOPORTE ──────────────────────────────────────────────────────────────
    private void contactarSoporte() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_contactar_soporte, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));

        EditText etMensaje = dialogView.findViewById(R.id.etMensaje);
        dialogView.findViewById(R.id.btnCancelar).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btnEnviar).setOnClickListener(v -> {
            String mensaje = etMensaje.getText().toString().trim();
            if (mensaje.isEmpty()) {
                Toast.makeText(this, "Escribe un mensaje", Toast.LENGTH_SHORT).show();
                return;
            }
            String cuerpo = "Supervisor: " + nombreSupervisor
                    + "\nCorreo: " + correoSupervisor
                    + "\n\nMensaje:\n" + mensaje;
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:" + CORREO_SOPORTE
                    + "?subject=" + Uri.encode("SoLinX - Soporte Supervisor: " + nombreSupervisor)
                    + "&body=" + Uri.encode(cuerpo)));
            try {
                startActivity(Intent.createChooser(emailIntent, "Enviar correo con..."));
                dialog.dismiss();
            } catch (Exception e) {
                Toast.makeText(this, "No hay app de correo disponible", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(
                (int)(getResources().getDisplayMetrics().widthPixels * 0.85),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    // ─── CERRAR SESIÓN ────────────────────────────────────────────────────────
    private void cerrarSesion() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_cerrar_sesion, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialogView.findViewById(R.id.btnNo).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btnSi).setOnClickListener(v -> {
            dialog.dismiss();
            try { ThemeUtils.forceLightModeLocal(this); } catch (Exception ignored) {}
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() ->
                            getSharedPreferences("sesion_usuario", MODE_PRIVATE).edit().clear().apply(),
                    300);
            finish();
        });

        dialog.show();
        dialog.getWindow().setLayout(
                (int)(getResources().getDisplayMetrics().widthPixels * 0.85),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
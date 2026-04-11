package com.example.solinx;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.solinx.API.ApiClient;
import com.example.solinx.API.ApiService;
import com.example.solinx.DTO.EmpresaDTO;
import com.example.solinx.DTO.PerfilDTO;
import com.example.solinx.UTIL.ThemeUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmpresaVistaCuenta extends AppCompatActivity {

    private static final String TAG = "EmpresaVistaCuenta";
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String CORREO_SOPORTE = "solinx.soporte@gmail.com";

    private ImageButton btnRegresar;
    private TextView tvNombre, tvCorreo, tvTelefono;
    private TextView btnContactarSoporte, btnCerrarSesion;
    private ImageView imgPerfil;
    private View viewModoClaro, viewModoOscuro;

    private Integer idUsuario;
    private Integer idEmpresa;
    private String nombreEmpresa = "";
    private SharedPreferences preferences;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_vista_cuenta);

        preferences = getSharedPreferences("sesion_usuario", MODE_PRIVATE);
        idUsuario = preferences.getInt("idUsuario", -1);
        idEmpresa = preferences.getInt("id_empresa_activa", -1);

        initViews();
        setupImagePicker();
        cargarDatosEmpresa();
        cargarFotoPerfil();
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

    private void setupImagePicker() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK
                            && result.getData() != null
                            && result.getData().getData() != null) {
                        procesarImagen(result.getData().getData());
                    }
                }
        );
    }

    private void cargarDatosEmpresa() {
        if (isFinishing() || isDestroyed()) return;
        if (idEmpresa == -1) {
            if (!isFinishing() && !isDestroyed()) {
                Toast.makeText(this, "Sesión no válida", Toast.LENGTH_SHORT).show();
            }
            finish();
            return;
        }

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.obtenerEmpresaPorId(idEmpresa).enqueue(new Callback<EmpresaDTO>() {
            @Override
            public void onResponse(@NonNull Call<EmpresaDTO> call, @NonNull Response<EmpresaDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EmpresaDTO e = response.body();
                    nombreEmpresa = e.getNombreEmpresa() != null ? e.getNombreEmpresa() : "";
                    runOnUiThread(() -> {
                        tvNombre.setText(e.getNombreEmpresa() != null ? e.getNombreEmpresa() : "N/A");
                        tvCorreo.setText(e.getCorreo() != null ? e.getCorreo() : "N/A");
                        tvTelefono.setText(e.getTelefono() != null ? e.getTelefono() : "N/A");
                    });
                } else {
                    Toast.makeText(EmpresaVistaCuenta.this,
                            "No se pudieron cargar los datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<EmpresaDTO> call, @NonNull Throwable t) {
                Toast.makeText(EmpresaVistaCuenta.this,
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarFotoPerfil() {
        if (idUsuario == null || idUsuario == -1) return;

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.obtenerPerfil(idUsuario).enqueue(new Callback<PerfilDTO>() {
            @Override
            public void onResponse(@NonNull Call<PerfilDTO> call, @NonNull Response<PerfilDTO> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getFoto() != null
                        && !response.body().getFoto().isEmpty()) {
                    String b64 = response.body().getFoto();
                    byte[] bytes = Base64.decode(b64, Base64.DEFAULT);
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    runOnUiThread(() -> imgPerfil.setImageBitmap(bmp));
                }
            }

            @Override
            public void onFailure(@NonNull Call<PerfilDTO> call, @NonNull Throwable t) {
                Log.e(TAG, "Error al cargar foto: " + t.getMessage());
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

        imgPerfil.setOnClickListener(v -> mostrarOpcionesFoto());

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
            String cuerpo = "Empresa: " + nombreEmpresa + "\n"
                    + "Correo: " + tvCorreo.getText().toString() + "\n"
                    + "Teléfono: " + tvTelefono.getText().toString() + "\n\n"
                    + "Mensaje:\n" + mensaje;

            String subject = "SoLinX - Soporte Empresa: " + nombreEmpresa;

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

    private void mostrarOpcionesFoto() {
        new AlertDialog.Builder(this)
                .setTitle("Foto de perfil")
                .setItems(new String[]{"Seleccionar de galería", "Eliminar foto"},
                        (dialog, which) -> {
                            if (which == 0) verificarPermisos();
                            else            eliminarFotoPerfil();
                        })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void verificarPermisos() {
        String permiso = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permiso) == PackageManager.PERMISSION_GRANTED) {
            abrirGaleria();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permiso}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            abrirGaleria();
        } else {
            Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
        }
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void procesarImagen(Uri uri) {
        try {
            InputStream stream = getContentResolver().openInputStream(uri);
            Bitmap original = BitmapFactory.decodeStream(stream);
            if (stream != null) stream.close();
            Bitmap escalado = redimensionar(original, 500, 500);

            imgPerfil.setImageBitmap(escalado);
            guardarFoto(escalado);
            Toast.makeText(this, "Foto actualizada ✓", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap redimensionar(Bitmap bmp, int maxW, int maxH) {
        float ratio = (float) bmp.getWidth() / bmp.getHeight();
        int w = maxW, h = maxH;
        if ((float) maxW / maxH > ratio) w = (int) (maxH * ratio);
        else                              h = (int) (maxW / ratio);
        return Bitmap.createScaledBitmap(bmp, w, h, true);
    }

    private void guardarFoto(Bitmap bmp) {
        if (idUsuario == null || idUsuario == -1) return;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        String b64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

        HashMap<String, String> body = new HashMap<>();
        body.put("foto", b64);

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.actualizarFoto(idUsuario, body).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(EmpresaVistaCuenta.this,
                            "Error al guardar foto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "Fallo red al guardar foto: " + t.getMessage());
            }
        });
    }

    private void eliminarFotoPerfil() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar")
                .setMessage("¿Eliminar foto de perfil?")
                .setPositiveButton("Sí", (d, w) -> {
                    if (idUsuario == null || idUsuario == -1) return;

                    ApiService api = ApiClient.getClient().create(ApiService.class);
                    HashMap<String, String> body = new HashMap<>();
                    body.put("foto", "");
                    api.actualizarFoto(idUsuario, body).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            runOnUiThread(() -> {
                                imgPerfil.setImageResource(R.drawable.imagen_prederterminada);
                                Toast.makeText(EmpresaVistaCuenta.this,
                                        "Foto eliminada", Toast.LENGTH_SHORT).show();
                            });
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            Toast.makeText(EmpresaVistaCuenta.this,
                                    "Error de conexión", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("No", null)
                .show();
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

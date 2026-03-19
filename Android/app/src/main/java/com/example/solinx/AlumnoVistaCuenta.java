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
import android.widget.Button;
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
import com.example.solinx.DTO.HorarioDTO;
import com.example.solinx.DTO.SolicitudDTO;
import com.example.solinx.UTIL.ThemeUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlumnoVistaCuenta extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String TAG = "AlumnoVistaCuenta";

    // Views
    private ImageButton btnRegresar;
    private TextView tvBoleta, tvNombre, tvCorreo, tvEscuela, tvCarrera, tvPuntosStatus;
    private ImageView imgPerfil;
    private View viewModoClaro, viewModoOscuro;
    private Button btnCerrarSesion;

    // Horario
    private TextView tvHorarioLunes, tvHorarioMartes, tvHorarioMiercoles;
    private TextView tvHorarioJueves, tvHorarioViernes, tvHorarioSabado, tvHorarioDomingo;

    // Datos
    private String boleta, nombre, carrera, escuela, correo;

    private SharedPreferences preferences;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    // ─── Lifecycle ────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumno_vista_cuenta);

        preferences = getSharedPreferences("SoLinXPrefs", MODE_PRIVATE);

        initViews();
        setupImagePicker();
        cargarDatosUsuario();
        setupListeners();
        actualizarIndicadoresTema();
        cargarFotoPerfil();
        cargarSolicitudes();
        cargarHorario();
    }

    // ─── Init ─────────────────────────────────────────────────────────────────

    private void initViews() {
        btnRegresar     = findViewById(R.id.regresar);
        tvBoleta        = findViewById(R.id.tvBoleta);
        tvNombre        = findViewById(R.id.tvNombre);
        tvCorreo        = findViewById(R.id.tvCorreo);
        tvEscuela       = findViewById(R.id.tvEscuela);
        tvCarrera       = findViewById(R.id.tvCarrera);
        tvPuntosStatus  = findViewById(R.id.tvPuntosStatus);
        imgPerfil       = findViewById(R.id.imgPerfil);
        viewModoClaro   = findViewById(R.id.viewModoClaro);
        viewModoOscuro  = findViewById(R.id.viewModoOscuro);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        tvHorarioLunes     = findViewById(R.id.tvHorarioLunes);
        tvHorarioMartes    = findViewById(R.id.tvHorarioMartes);
        tvHorarioMiercoles = findViewById(R.id.tvHorarioMiercoles);
        tvHorarioJueves    = findViewById(R.id.tvHorarioJueves);
        tvHorarioViernes   = findViewById(R.id.tvHorarioViernes);
        tvHorarioSabado    = findViewById(R.id.tvHorarioSabado);
        tvHorarioDomingo   = findViewById(R.id.tvHorarioDomingo);
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

    // ─── Datos de usuario ─────────────────────────────────────────────────────

    private void cargarDatosUsuario() {
        Intent intent = getIntent();

        boleta  = obtenerDato(intent, "boleta",  "boleta",  "N/A");
        nombre  = obtenerDato(intent, "nombre",  "nombre",  "Usuario");
        carrera = obtenerDato(intent, "carrera", "carrera", "N/A");
        escuela = obtenerDato(intent, "escuela", "escuela", "N/A");
        correo  = obtenerDato(intent, "correo",  "correo",  "N/A");

        tvBoleta.setText(boleta);
        tvNombre.setText(nombre);
        tvCorreo.setText(correo);
        tvEscuela.setText(escuela);
        tvCarrera.setText(carrera);
        tvPuntosStatus.setText("Cargando solicitudes...");
    }

    private String obtenerDato(Intent intent, String intentKey,
                               String prefKey, String defVal) {
        String val = intent.getStringExtra(intentKey);
        return val != null ? val : preferences.getString(prefKey, defVal);
    }

    // ─── Solicitudes ──────────────────────────────────────────────────────────

    private void cargarSolicitudes() {
        if (boleta == null || boleta.equals("N/A")) {
            tvPuntosStatus.setText("No se pudo cargar la información de solicitudes.");
            return;
        }

        try {
            int boletaInt = Integer.parseInt(boleta);
            ApiService api = ApiClient.getClient().create(ApiService.class);
            api.obtenerSolicitudesEstudiante(boletaInt).enqueue(new Callback<List<SolicitudDTO>>() {

                @Override
                public void onResponse(@NonNull Call<List<SolicitudDTO>> call,
                                       @NonNull Response<List<SolicitudDTO>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        mostrarSolicitudes(response.body());
                    } else {
                        Log.e(TAG, "Error solicitudes: " + response.code());
                        runOnUiThread(() ->
                                tvPuntosStatus.setText("Error al cargar solicitudes."));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<SolicitudDTO>> call,
                                      @NonNull Throwable t) {
                    Log.e(TAG, "Fallo red solicitudes: " + t.getMessage());
                    runOnUiThread(() ->
                            tvPuntosStatus.setText("Error de conexión."));
                }
            });

        } catch (NumberFormatException e) {
            Log.e(TAG, "Boleta inválida: " + boleta);
            tvPuntosStatus.setText("Boleta inválida.");
        }
    }

    private void mostrarSolicitudes(List<SolicitudDTO> lista) {
        runOnUiThread(() -> {
            if (lista.isEmpty()) {
                tvPuntosStatus.setText("No tienes solicitudes enviadas.");
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (SolicitudDTO s : lista) {
                sb.append("Empresa: ").append(s.getNombreEmpresa()).append("\n");
                sb.append("Proyecto: ").append(s.getNombreProyecto()).append("\n");

                String estado = s.getEstadoSolicitud();
                if (estado == null) {
                    sb.append("Estado: Desconocido");
                } else if (estado.equalsIgnoreCase("aceptada")) {
                    sb.append("Estado: Admitido");
                } else if (estado.equalsIgnoreCase("rechazada")) {
                    sb.append("Estado: Rechazado");
                } else {
                    sb.append("Estado: Pendiente");
                }
                sb.append("\n\n");
            }
            tvPuntosStatus.setText(sb.toString().trim());
        });
    }

    // ─── Horario ──────────────────────────────────────────────────────────────

    private void cargarHorario() {
        if (boleta == null || boleta.equals("N/A")) return;

        try {
            int boletaInt = Integer.parseInt(boleta);
            ApiService api = ApiClient.getClient().create(ApiService.class);
            api.obtenerHorario(boletaInt).enqueue(new Callback<HorarioDTO>() {

                @Override
                public void onResponse(@NonNull Call<HorarioDTO> call,
                                       @NonNull Response<HorarioDTO> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        HorarioDTO h = response.body();
                        runOnUiThread(() -> {
                            tvHorarioLunes.setText(fmt(h.getLunInicio(), h.getLunFinal()));
                            tvHorarioMartes.setText(fmt(h.getMarInicio(), h.getMarFinal()));
                            tvHorarioMiercoles.setText(fmt(h.getMierInicio(), h.getMierFinal()));
                            tvHorarioJueves.setText(fmt(h.getJueInicio(), h.getJueFinal()));
                            tvHorarioViernes.setText(fmt(h.getVieInicio(), h.getVieFinal()));
                            tvHorarioSabado.setText(fmt(h.getSabInicio(), h.getSabFinal()));
                            tvHorarioDomingo.setText(fmt(h.getDomInicio(), h.getDomFinal()));
                        });
                    } else {
                        Log.w(TAG, "Sin horario: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<HorarioDTO> call, @NonNull Throwable t) {
                    Log.e(TAG, "Fallo red horario: " + t.getMessage());
                }
            });

        } catch (NumberFormatException e) {
            Log.e(TAG, "Boleta inválida al cargar horario: " + boleta);
        }
    }

    private String fmt(String inicio, String fin) {
        if (inicio == null || fin == null) return "Sin clase";
        String i = inicio.length() > 5 ? inicio.substring(0, 5) : inicio;
        String f = fin.length()   > 5 ? fin.substring(0, 5)   : fin;
        return i + " – " + f;
    }

    // ─── Listeners ────────────────────────────────────────────────────────────

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

    // ─── Foto de perfil ───────────────────────────────────────────────────────

    private void mostrarOpcionesFoto() {
        new AlertDialog.Builder(this)
                .setTitle("Foto de perfil")
                .setItems(new String[]{"Seleccionar de galería", "Eliminar foto"},
                        (dialog, which) -> {
                            if (which == 0) verificarPermisos();
                            else           eliminarFotoPerfil();
                        })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void verificarPermisos() {
        String permiso = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permiso)
                == PackageManager.PERMISSION_GRANTED) {
            abrirGaleria();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{permiso}, PERMISSION_REQUEST_CODE);
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
            Bitmap escalado = redimensionar(original, 500, 500);

            imgPerfil.setImageBitmap(escalado);
            guardarFoto(escalado);
            Toast.makeText(this, "Foto actualizada ✓", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
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

    // ← CAMBIO: SharedPreferences separado "SoLinXFotos" para que no se borre al cerrar sesión
    private void guardarFoto(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        String b64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        getSharedPreferences("SoLinXFotos", MODE_PRIVATE)
                .edit().putString("foto_perfil_" + boleta, b64).apply();
    }

    // ← CAMBIO: lee de "SoLinXFotos"
    private void cargarFotoPerfil() {
        String b64 = getSharedPreferences("SoLinXFotos", MODE_PRIVATE)
                .getString("foto_perfil_" + boleta, null);
        if (b64 != null) {
            byte[] bytes = Base64.decode(b64, Base64.DEFAULT);
            imgPerfil.setImageBitmap(
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
        }
    }

    // ← CAMBIO: elimina de "SoLinXFotos"
    private void eliminarFotoPerfil() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar")
                .setMessage("¿Eliminar foto de perfil?")
                .setPositiveButton("Sí", (d, w) -> {
                    getSharedPreferences("SoLinXFotos", MODE_PRIVATE)
                            .edit().remove("foto_perfil_" + boleta).apply();
                    imgPerfil.setImageResource(R.drawable.imagen_prederterminada);
                    Toast.makeText(this, "Foto eliminada", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }

    // ─── Cerrar sesión ────────────────────────────────────────────────────────

    // ← CAMBIO: solo limpia "SoLinXPrefs", las fotos en "SoLinXFotos" quedan intactas
    private void cerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Seguro que quieres salir?")
                .setPositiveButton("Sí", (d, w) -> {
                    preferences.edit().clear().apply();
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
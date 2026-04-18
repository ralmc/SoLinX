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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.solinx.API.ApiClient;
import com.example.solinx.API.ApiService;
import com.example.solinx.RESPONSE.ProyectoResponse;
import com.example.solinx.UTIL.ThemeUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GestionProyectoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "GestionProyecto";
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final long MAX_IMAGE_SIZE_BYTES = 2 * 1024 * 1024;

    EditText etCarrera, etNombreProyecto, etObjetivo, etVacantes, etUbicacion;
    Button btnGuardar, btnSeleccionarImagen;
    ImageView logoEmpresa, imgPreview;
    TextView tvNotificaciones, tvImagenStatus;

    ApiService apiService;
    boolean esEdicion = false;
    int idProyectoEditar = -1;
    int idEmpresaEditar = -1;

    private String imagenBase64 = null;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_proyecto);

        inicializarVistas();
        setupImagePicker();
        apiService = ApiClient.getClient().create(ApiService.class);
        verificarModoEdicion();

        btnGuardar.setOnClickListener(this);
        logoEmpresa.setOnClickListener(this);
        tvNotificaciones.setOnClickListener(this);
        btnSeleccionarImagen.setOnClickListener(this);
    
        // Botón flecha de regreso
        try {
            android.widget.ImageButton btnRegresarFlecha = findViewById(R.id.btnRegresarFlecha);
            if (btnRegresarFlecha != null) {
                btnRegresarFlecha.setOnClickListener(v -> finish());
            }
        } catch (Exception ignored) {}
}

    private void inicializarVistas() {
        etCarrera = findViewById(R.id.etCarrera);
        etNombreProyecto = findViewById(R.id.etNombreProyecto);
        etObjetivo = findViewById(R.id.etObjetivo);
        etVacantes = findViewById(R.id.etVacantes);
        etUbicacion = findViewById(R.id.etUbicacion);
        btnGuardar = findViewById(R.id.btnGuardar);
        logoEmpresa = findViewById(R.id.logoEmpresa);
        tvNotificaciones = findViewById(R.id.notificaciones);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        imgPreview = findViewById(R.id.imgPreview);
        tvImagenStatus = findViewById(R.id.tvImagenStatus);
    }

    private void setupImagePicker() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK
                            && result.getData() != null
                            && result.getData().getData() != null) {
                        procesarImagenProyecto(result.getData().getData());
                    }
                }
        );
    }

    private int obtenerIdEmpresaActual() {
        SharedPreferences prefs = getSharedPreferences("sesion_usuario", MODE_PRIVATE);
        return prefs.getInt("id_empresa_activa", -1);
    }

    private void verificarModoEdicion() {
        if (getIntent().hasExtra("idProyecto")) {
            esEdicion = true;
            idProyectoEditar = getIntent().getIntExtra("idProyecto", -1);
            idEmpresaEditar = getIntent().getIntExtra("idEmpresa", -1);

            btnGuardar.setText("ACTUALIZAR PROYECTO");
            etCarrera.setText(getIntent().getStringExtra("carrera"));
            etNombreProyecto.setText(getIntent().getStringExtra("nombre"));
            etObjetivo.setText(getIntent().getStringExtra("objetivo"));
            etVacantes.setText(String.valueOf(getIntent().getIntExtra("vacantes", 1)));
            etUbicacion.setText(getIntent().getStringExtra("ubicacion"));

            // Cargar imagen existente del proyecto si hay
            String imgExistente = getIntent().getStringExtra("imagenProyecto");
            if (imgExistente != null && !imgExistente.isEmpty()) {
                try {
                    byte[] bytes = Base64.decode(imgExistente, Base64.DEFAULT);
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    if (bmp != null) {
                        imgPreview.setImageBitmap(bmp);
                        imgPreview.setVisibility(View.VISIBLE);
                        tvImagenStatus.setText("Imagen actual del proyecto");
                        imagenBase64 = imgExistente;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error cargando imagen existente: " + e.getMessage());
                }
            }
        }
    }

    // ─── Imagen del Proyecto ──────────────────────────────────────────────────
    private void seleccionarImagen() {
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            abrirGaleria();
        } else {
            Toast.makeText(this, "Permiso denegado para acceder a imágenes", Toast.LENGTH_SHORT).show();
        }
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void procesarImagenProyecto(Uri uri) {
        try {
            InputStream checkStream = getContentResolver().openInputStream(uri);
            if (checkStream != null) {
                int fileSize = checkStream.available();
                checkStream.close();

                if (fileSize > 10 * 1024 * 1024) {
                    Toast.makeText(this, "La imagen es demasiado grande. Máximo 10MB.", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            InputStream stream = getContentResolver().openInputStream(uri);
            Bitmap original = BitmapFactory.decodeStream(stream);
            if (stream != null) stream.close();

            if (original == null) {
                Toast.makeText(this, "No se pudo leer la imagen", Toast.LENGTH_SHORT).show();
                return;
            }

            Bitmap escalado = redimensionar(original, 800, 800);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int calidad = 85;
            escalado.compress(Bitmap.CompressFormat.JPEG, calidad, baos);

            while (baos.toByteArray().length > MAX_IMAGE_SIZE_BYTES && calidad > 20) {
                baos.reset();
                calidad -= 10;
                escalado.compress(Bitmap.CompressFormat.JPEG, calidad, baos);
            }

            if (baos.toByteArray().length > MAX_IMAGE_SIZE_BYTES) {
                Toast.makeText(this,
                        "La imagen es demasiado pesada incluso después de comprimir.\nIntenta con una imagen más pequeña.",
                        Toast.LENGTH_LONG).show();
                return;
            }

            imagenBase64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

            imgPreview.setImageBitmap(escalado);
            imgPreview.setVisibility(View.VISIBLE);

            float sizeKB = baos.toByteArray().length / 1024f;
            String sizeText = sizeKB > 1024
                    ? String.format("%.1f MB", sizeKB / 1024)
                    : String.format("%.0f KB", sizeKB);
            tvImagenStatus.setText("Imagen seleccionada (" + sizeText + ")");

            Toast.makeText(this, "Imagen cargada correctamente", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Log.e(TAG, "Error al procesar imagen: " + e.getMessage());
            Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap redimensionar(Bitmap bmp, int maxW, int maxH) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        if (w <= maxW && h <= maxH) return bmp;

        float ratio = (float) w / h;
        int newW = maxW, newH = maxH;
        if ((float) maxW / maxH > ratio) newW = (int) (maxH * ratio);
        else newH = (int) (maxW / ratio);
        return Bitmap.createScaledBitmap(bmp, newW, newH, true);
    }

    // ─── Clicks ───────────────────────────────────────────────────────────────
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnGuardar) {
            if (validarCampos()) {
                if (esEdicion) actualizarProyecto();
                else crearNuevoProyecto();
            }
        } else if (id == R.id.logoEmpresa) {
            finish();
        } else if (id == R.id.notificaciones) {
            startActivity(new Intent(this, EmpresaNotificaciones.class));
        } else if (id == R.id.btnSeleccionarImagen) {
            seleccionarImagen();
        }
    }

    private boolean validarCampos() {
        boolean esValido = true;
        String vacantesTxt = etVacantes.getText().toString().trim();

        if (etCarrera.getText().toString().trim().isEmpty()) {
            etCarrera.setError("Campo de Carrera requerido");
            esValido = false;
        }
        if (etNombreProyecto.getText().toString().trim().isEmpty()) {
            etNombreProyecto.setError("Campo de Nombre de Proyecto requerido");
            esValido = false;
        }
        if (etObjetivo.getText().toString().trim().isEmpty()) {
            etObjetivo.setError("Campo de Objetivo requerido");
            esValido = false;
        }
        if (etUbicacion.getText().toString().trim().isEmpty()) {
            etUbicacion.setError("Campo de Ubicación requerido");
            esValido = false;
        }
        if (vacantesTxt.isEmpty()) {
            etVacantes.setError("Número de Vacantes requerido");
            esValido = false;
        } else {
            try {
                int vacantes = Integer.parseInt(vacantesTxt);
                if (vacantes <= 0) {
                    etVacantes.setError("El número debe ser mayor a cero");
                    esValido = false;
                }
            } catch (NumberFormatException e) {
                etVacantes.setError("Debe ser un número entero");
                esValido = false;
            }
        }
        return esValido;
    }

    private ProyectoResponse armarObjetoProyecto() {
        ProyectoResponse p = new ProyectoResponse();
        p.setCarreraEnfocada(etCarrera.getText().toString().trim());
        p.setNombreProyecto(etNombreProyecto.getText().toString().trim());
        p.setObjetivo(etObjetivo.getText().toString().trim());
        p.setUbicacion(etUbicacion.getText().toString().trim());

        p.setImagenRef("img_default_proyecto");

        if (imagenBase64 != null && !imagenBase64.isEmpty()) {
            p.setImagenProyecto(imagenBase64);
        }

        try {
            String vacantesTxt = etVacantes.getText().toString().trim();
            p.setVacantes(Integer.parseInt(vacantesTxt));
        } catch (NumberFormatException e) {
            p.setVacantes(1);
        }

        int idEmpresa;
        if (esEdicion && idEmpresaEditar != -1) {
            idEmpresa = idEmpresaEditar;
        } else {
            idEmpresa = obtenerIdEmpresaActual();
            if (idEmpresa == -1) {
                Toast.makeText(this, "Sesión perdida, relogueate", Toast.LENGTH_SHORT).show();
                idEmpresa = 1;
            }
        }

        p.setIdEmpresa(idEmpresa);
        return p;
    }

    private void crearNuevoProyecto() {
        ProyectoResponse nuevoProyecto = armarObjetoProyecto();
        apiService.crearProyecto(nuevoProyecto).enqueue(new Callback<ProyectoResponse>() {
            @Override
            public void onResponse(Call<ProyectoResponse> call, Response<ProyectoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int idCreado = response.body().getIdProyecto();

                    if (imagenBase64 != null && !imagenBase64.isEmpty()) {
                        subirImagenAlProyecto(idCreado);
                    } else {
                        Toast.makeText(GestionProyectoActivity.this, "Proyecto creado", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(GestionProyectoActivity.this, "Error " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ProyectoResponse> call, Throwable t) {
                Toast.makeText(GestionProyectoActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarProyecto() {
        ProyectoResponse proyectoEditado = armarObjetoProyecto();
        proyectoEditado.setIdProyecto(idProyectoEditar);
        apiService.actualizarProyecto(idProyectoEditar, proyectoEditado).enqueue(new Callback<ProyectoResponse>() {
            @Override
            public void onResponse(Call<ProyectoResponse> call, Response<ProyectoResponse> response) {
                if (response.isSuccessful()) {
                    if (imagenBase64 != null && !imagenBase64.isEmpty()) {
                        subirImagenAlProyecto(idProyectoEditar);
                    } else {
                        Toast.makeText(GestionProyectoActivity.this, "Proyecto actualizado", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(GestionProyectoActivity.this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ProyectoResponse> call, Throwable t) {
                Toast.makeText(GestionProyectoActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void subirImagenAlProyecto(int idProyecto) {
        Map<String, String> body = new HashMap<>();
        body.put("imagenProyecto", imagenBase64);

        apiService.actualizarImagenProyecto(idProyecto, body).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(GestionProyectoActivity.this, "Proyecto guardado con imagen", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GestionProyectoActivity.this, "Proyecto guardado, pero error al subir imagen", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(GestionProyectoActivity.this, "Proyecto guardado, pero error de red al subir imagen", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}

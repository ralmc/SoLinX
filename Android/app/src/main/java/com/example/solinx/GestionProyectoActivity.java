package com.example.solinx;

import android.Manifest;
import android.app.DatePickerDialog;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GestionProyectoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "GestionProyecto";
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final long MAX_IMAGE_SIZE_BYTES = 2 * 1024 * 1024;

    AutoCompleteTextView etCarrera;
    TextInputEditText etNombreProyecto, etObjetivo, etVacantes, etUbicacion;
    TextInputEditText etFechaInicio, etFechaTermino;
    TextInputLayout tilFechaInicio, tilFechaTermino;

    Button btnGuardar, btnSeleccionarImagen;
    ImageView logoEmpresa, imgPreview;
    TextView tvNotificaciones, tvImagenStatus;

    ApiService apiService;
    boolean esEdicion = false;
    int idProyectoEditar = -1;
    int idEmpresaEditar  = -1;

    private String imagenBase64 = null;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    private final String[] CARRERAS = {
            "Ingeniería en Sistemas Computacionales",
            "Ingeniería en Software",
            "Ingeniería Industrial",
            "Ingeniería Mecatrónica",
            "Ingeniería Informática",
            "Ingeniería en Inteligencia Artificial",
            "Ingeniería Aeronáutica",
            "Ingeniería Biónica",
            "Programación"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_proyecto);

        inicializarVistas();
        configurarDropdownCarrera();
        configurarDatePickers();
        setupImagePicker();

        apiService = ApiClient.getClient().create(ApiService.class);
        verificarModoEdicion();

        btnGuardar.setOnClickListener(this);
        logoEmpresa.setOnClickListener(this);
        tvNotificaciones.setOnClickListener(this);
        btnSeleccionarImagen.setOnClickListener(this);

        android.widget.ImageButton btnRegresarFlecha = findViewById(R.id.btnRegresarFlecha);
        if (btnRegresarFlecha != null) {
            btnRegresarFlecha.setOnClickListener(v -> finish());
        }
    }

    private void inicializarVistas() {
        etCarrera        = findViewById(R.id.etCarrera);
        etNombreProyecto = findViewById(R.id.etNombreProyecto);
        etObjetivo       = findViewById(R.id.etObjetivo);
        etVacantes       = findViewById(R.id.etVacantes);
        etUbicacion      = findViewById(R.id.etUbicacion);
        etFechaInicio    = findViewById(R.id.etFechaInicio);
        etFechaTermino   = findViewById(R.id.etFechaTermino);
        btnGuardar       = findViewById(R.id.btnGuardar);
        logoEmpresa      = findViewById(R.id.logoEmpresa);
        tvNotificaciones = findViewById(R.id.notificaciones);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        imgPreview       = findViewById(R.id.imgPreview);
        tvImagenStatus   = findViewById(R.id.tvImagenStatus);

        // Obtener los TextInputLayout de fechas para controlar el ícono
        tilFechaInicio  = (TextInputLayout) etFechaInicio.getParent().getParent();
        tilFechaTermino = (TextInputLayout) etFechaTermino.getParent().getParent();
    }

    private void configurarDropdownCarrera() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, CARRERAS);
        etCarrera.setAdapter(adapter);
        etCarrera.setOnClickListener(v -> etCarrera.showDropDown());
    }

    private void configurarDatePickers() {
        etFechaInicio.setOnClickListener(v -> mostrarDatePicker(etFechaInicio, tilFechaInicio));
        etFechaTermino.setOnClickListener(v -> mostrarDatePicker(etFechaTermino, tilFechaTermino));
    }

    private void mostrarDatePicker(TextInputEditText campo, TextInputLayout til) {
        Calendar cal = Calendar.getInstance();

        String fechaActual = campo.getText() != null ? campo.getText().toString() : "";
        if (!fechaActual.isEmpty()) {
            try {
                String[] partes = fechaActual.split("-");
                cal.set(Integer.parseInt(partes[0]),
                        Integer.parseInt(partes[1]) - 1,
                        Integer.parseInt(partes[2]));
            } catch (Exception ignored) {}
        }

        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    String fecha = String.format("%04d-%02d-%02d", year, month + 1, day);
                    campo.setText(fecha);
                    campo.setError(null);
                    til.setEndIconVisible(true); // ← restaurar ícono al seleccionar fecha
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));

        dialog.show();
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
                });
    }

    private int obtenerIdEmpresaActual() {
        SharedPreferences prefs = getSharedPreferences("sesion_usuario", MODE_PRIVATE);
        return prefs.getInt("id_empresa_activa", -1);
    }

    private void verificarModoEdicion() {
        if (!getIntent().hasExtra("idProyecto")) return;

        esEdicion        = true;
        idProyectoEditar = getIntent().getIntExtra("idProyecto", -1);
        idEmpresaEditar  = getIntent().getIntExtra("idEmpresa",  -1);

        btnGuardar.setText("ACTUALIZAR PROYECTO");
        etCarrera.setText(getIntent().getStringExtra("carrera"), false);
        etNombreProyecto.setText(getIntent().getStringExtra("nombre"));
        etObjetivo.setText(getIntent().getStringExtra("objetivo"));
        etVacantes.setText(String.valueOf(getIntent().getIntExtra("vacantes", 1)));
        etUbicacion.setText(getIntent().getStringExtra("ubicacion"));

        // Fechas
        String fi = getIntent().getStringExtra("fechaInicio");
        String ft = getIntent().getStringExtra("fechaTermino");
        if (fi != null && !fi.isEmpty()) etFechaInicio.setText(convertirFecha(fi));
        if (ft != null && !ft.isEmpty()) etFechaTermino.setText(convertirFecha(ft));

        // Imagen existente
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
                Log.e(TAG, "Error cargando imagen: " + e.getMessage());
            }
        }
    }

    private String convertirFecha(String fecha) {
        if (fecha == null || fecha.isEmpty()) return "";
        if (fecha.contains("-")) return fecha.substring(0, 10);
        String[] p = fecha.split("/");
        if (p.length == 3) return p[2] + "-" + p[1] + "-" + p[0];
        return fecha;
    }

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
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void procesarImagenProyecto(Uri uri) {
        try {
            InputStream check = getContentResolver().openInputStream(uri);
            if (check != null) {
                if (check.available() > 10 * 1024 * 1024) {
                    check.close();
                    Toast.makeText(this, "Imagen demasiado grande. Máx 10MB", Toast.LENGTH_LONG).show();
                    return;
                }
                check.close();
            }

            InputStream stream = getContentResolver().openInputStream(uri);
            Bitmap original = BitmapFactory.decodeStream(stream);
            if (stream != null) stream.close();

            if (original == null) {
                Toast.makeText(this, "No se pudo leer la imagen", Toast.LENGTH_SHORT).show();
                return;
            }

            Bitmap escalado = recortarA16x9(original, 480, 270);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int calidad = 85;
            escalado.compress(Bitmap.CompressFormat.JPEG, calidad, baos);

            while (baos.toByteArray().length > MAX_IMAGE_SIZE_BYTES && calidad > 20) {
                baos.reset();
                calidad -= 10;
                escalado.compress(Bitmap.CompressFormat.JPEG, calidad, baos);
            }

            if (baos.toByteArray().length > MAX_IMAGE_SIZE_BYTES) {
                Toast.makeText(this, "Imagen muy pesada. Usa una más pequeña.", Toast.LENGTH_LONG).show();
                return;
            }

            imagenBase64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
            imgPreview.setImageBitmap(escalado);
            imgPreview.setVisibility(View.VISIBLE);

            float sizeKB = baos.toByteArray().length / 1024f;
            tvImagenStatus.setText(sizeKB > 1024
                    ? String.format("Imagen seleccionada (%.1f MB)", sizeKB / 1024)
                    : String.format("Imagen seleccionada (%.0f KB)", sizeKB));

            Toast.makeText(this, "Imagen cargada ✓", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Log.e(TAG, "Error imagen: " + e.getMessage());
            Toast.makeText(this, "Error al cargar imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap recortarA16x9(Bitmap bmp, int targetW, int targetH) {
        float srcRatio = (float) bmp.getWidth() / bmp.getHeight();
        float dstRatio = (float) targetW / targetH;
        int sx, sy, sw, sh;
        if (srcRatio > dstRatio) {
            sh = bmp.getHeight();
            sw = (int) (bmp.getHeight() * dstRatio);
            sx = (bmp.getWidth() - sw) / 2;
            sy = 0;
        } else {
            sw = bmp.getWidth();
            sh = (int) (bmp.getWidth() / dstRatio);
            sx = 0;
            sy = (bmp.getHeight() - sh) / 2;
        }
        Bitmap recortado = Bitmap.createBitmap(bmp, sx, sy, sw, sh);
        return Bitmap.createScaledBitmap(recortado, targetW, targetH, true);
    }

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
        boolean ok = true;

        String carrera  = etCarrera.getText() != null ? etCarrera.getText().toString().trim() : "";
        String nombre   = etNombreProyecto.getText() != null ? etNombreProyecto.getText().toString().trim() : "";
        String objetivo = etObjetivo.getText() != null ? etObjetivo.getText().toString().trim() : "";
        String vacTxt   = etVacantes.getText() != null ? etVacantes.getText().toString().trim() : "";
        String ubic     = etUbicacion.getText() != null ? etUbicacion.getText().toString().trim() : "";
        String fi       = etFechaInicio.getText() != null ? etFechaInicio.getText().toString().trim() : "";
        String ft       = etFechaTermino.getText() != null ? etFechaTermino.getText().toString().trim() : "";

        if (carrera.isEmpty())  { etCarrera.setError("Selecciona una carrera"); ok = false; }
        if (nombre.isEmpty())   { etNombreProyecto.setError("Campo requerido"); ok = false; }
        if (objetivo.isEmpty()) { etObjetivo.setError("Campo requerido"); ok = false; }
        if (ubic.isEmpty())     { etUbicacion.setError("Campo requerido"); ok = false; }

        if (vacTxt.isEmpty()) {
            etVacantes.setError("Campo requerido"); ok = false;
        } else {
            try {
                int v = Integer.parseInt(vacTxt);
                if (v <= 0) { etVacantes.setError("Debe ser mayor a 0"); ok = false; }
            } catch (NumberFormatException e) {
                etVacantes.setError("Número inválido"); ok = false;
            }
        }

        // Fechas — ocultar ícono si hay error, mostrarlo si está bien
        if (fi.isEmpty()) {
            etFechaInicio.setError("Selecciona una fecha");
            tilFechaInicio.setEndIconVisible(false);
            ok = false;
        } else {
            etFechaInicio.setError(null);
            tilFechaInicio.setEndIconVisible(true);
        }

        if (ft.isEmpty()) {
            etFechaTermino.setError("Selecciona una fecha");
            tilFechaTermino.setEndIconVisible(false);
            ok = false;
        } else {
            etFechaTermino.setError(null);
            tilFechaTermino.setEndIconVisible(true);
        }

        if (!fi.isEmpty() && !ft.isEmpty() && ft.compareTo(fi) <= 0) {
            etFechaTermino.setError("Debe ser posterior a la fecha de inicio");
            tilFechaTermino.setEndIconVisible(false);
            ok = false;
        }

        return ok;
    }

    private ProyectoResponse armarObjetoProyecto() {
        ProyectoResponse p = new ProyectoResponse();
        p.setCarreraEnfocada(etCarrera.getText() != null ? etCarrera.getText().toString().trim() : "");
        p.setNombreProyecto(etNombreProyecto.getText() != null ? etNombreProyecto.getText().toString().trim() : "");
        p.setObjetivo(etObjetivo.getText() != null ? etObjetivo.getText().toString().trim() : "");
        p.setUbicacion(etUbicacion.getText() != null ? etUbicacion.getText().toString().trim() : "");
        p.setFechaInicio(etFechaInicio.getText() != null ? etFechaInicio.getText().toString().trim() : "");
        p.setFechaTermino(etFechaTermino.getText() != null ? etFechaTermino.getText().toString().trim() : "");
        p.setImagenRef("img_default_proyecto");

        if (imagenBase64 != null && !imagenBase64.isEmpty()) {
            p.setImagenProyecto(imagenBase64);
        }

        try {
            p.setVacantes(Integer.parseInt(
                    etVacantes.getText() != null ? etVacantes.getText().toString().trim() : "1"));
        } catch (NumberFormatException e) {
            p.setVacantes(1);
        }

        int idEmpresa = (esEdicion && idEmpresaEditar != -1)
                ? idEmpresaEditar : obtenerIdEmpresaActual();
        if (idEmpresa == -1) idEmpresa = 1;
        p.setIdEmpresa(idEmpresa);

        return p;
    }

    private void crearNuevoProyecto() {
        ProyectoResponse nuevo = armarObjetoProyecto();
        apiService.crearProyecto(nuevo).enqueue(new Callback<ProyectoResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProyectoResponse> call,
                                   @NonNull Response<ProyectoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int idCreado = response.body().getIdProyecto();
                    if (imagenBase64 != null && !imagenBase64.isEmpty()) {
                        subirImagenAlProyecto(idCreado);
                    } else {
                        Toast.makeText(GestionProyectoActivity.this, "Proyecto creado ✓", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(GestionProyectoActivity.this, "Error " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ProyectoResponse> call, @NonNull Throwable t) {
                Toast.makeText(GestionProyectoActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarProyecto() {
        ProyectoResponse editado = armarObjetoProyecto();
        editado.setIdProyecto(idProyectoEditar);
        apiService.actualizarProyecto(idProyectoEditar, editado).enqueue(new Callback<ProyectoResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProyectoResponse> call,
                                   @NonNull Response<ProyectoResponse> response) {
                if (response.isSuccessful()) {
                    if (imagenBase64 != null && !imagenBase64.isEmpty()) {
                        subirImagenAlProyecto(idProyectoEditar);
                    } else {
                        Toast.makeText(GestionProyectoActivity.this, "Proyecto actualizado ✓", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(GestionProyectoActivity.this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ProyectoResponse> call, @NonNull Throwable t) {
                Toast.makeText(GestionProyectoActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void subirImagenAlProyecto(int idProyecto) {
        Map<String, String> body = new HashMap<>();
        body.put("imagenProyecto", imagenBase64);
        apiService.actualizarImagenProyecto(idProyecto, body).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Toast.makeText(GestionProyectoActivity.this,
                        response.isSuccessful() ? "Proyecto guardado con imagen ✓" : "Guardado, error al subir imagen",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(GestionProyectoActivity.this,
                        "Guardado, error de red al subir imagen", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
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
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.solinx.UTIL.ThemeUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AlumnoVistaCuenta extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;

    private ImageButton btnRegresar;
    private TextView tvBoleta;
    private TextView tvPuntosInfo;
    private TextView tvPuntosStatus;
    private TextView tvEmpresa1Title;
    private TextView tvEmpresa2Title;
    private ImageView imgPerfil;
    private View viewModoClaro;
    private View viewModoOscuro;
    private Button btnCerrarSesion;

    private String boleta;
    private String nombre;
    private String carrera;
    private String escuela;
    private String correo;
    private String telefono;

    private SharedPreferences preferences;

    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumno_vista_cuenta);

        preferences = getSharedPreferences("SoLinXPrefs", MODE_PRIVATE);

        initViews();
        cargarDatosUsuario();
        setupListeners();
        actualizarIndicadoresTema();

        setupImagePicker();

        cargarFotoPerfil();
    }

    private void initViews() {
        btnRegresar = findViewById(R.id.regresar);
        tvBoleta = findViewById(R.id.tvBoleta);
        tvPuntosInfo = findViewById(R.id.tvPuntosInfo);
        tvPuntosStatus = findViewById(R.id.tvPuntosStatus);
        tvEmpresa1Title = findViewById(R.id.tvEmpresa1Title);
        tvEmpresa2Title = findViewById(R.id.tvEmpresa2Title);
        imgPerfil = findViewById(R.id.imgPerfil);
        viewModoClaro = findViewById(R.id.viewModoClaro);
        viewModoOscuro = findViewById(R.id.viewModoOscuro);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
    }

    private void setupImagePicker() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            procesarImagen(imageUri);
                        }
                    }
                }
        );
    }

    private void cargarDatosUsuario() {
        Intent intent = getIntent();

        boleta = intent.getStringExtra("boleta");
        if (boleta == null) {
            boleta = preferences.getString("boleta", "N/A");
        }

        nombre = intent.getStringExtra("nombre");
        if (nombre == null) {
            nombre = preferences.getString("nombre", "Usuario");
        }

        carrera = intent.getStringExtra("carrera");
        if (carrera == null) {
            carrera = preferences.getString("carrera", "N/A");
        }

        escuela = intent.getStringExtra("escuela");
        if (escuela == null) {
            escuela = preferences.getString("escuela", "N/A");
        }

        correo = intent.getStringExtra("correo");
        if (correo == null) {
            correo = preferences.getString("correo", "N/A");
        }

        telefono = intent.getStringExtra("telefono");
        if (telefono == null) {
            telefono = preferences.getString("telefono", "N/A");
        }

        mostrarDatos();
    }

    private void mostrarDatos() {
        tvBoleta.setText(boleta);

        String infoAlumno = "Nombre: " + nombre + "\n" +
                "Carrera: " + carrera + "\n" +
                "Escuela: " + escuela + "\n" +
                "Correo: " + correo + "\n" +
                "Teléfono: " + telefono;
        tvPuntosInfo.setText(infoAlumno);

        tvPuntosStatus.setText("Cargando solicitudes...");
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

        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
    }

    private void mostrarOpcionesFoto() {
        String[] opciones = {"Seleccionar de galería", "Eliminar foto"};

        new AlertDialog.Builder(this)
                .setTitle("Foto de perfil")
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        verificarPermisos();
                    } else {
                        eliminarFotoPerfil();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void verificarPermisos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_CODE);
            } else {
                abrirGaleria();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                abrirGaleria();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirGaleria();
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void procesarImagen(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            Bitmap bitmapRedimensionado = redimensionarBitmap(bitmap, 500, 500);

            imgPerfil.setImageBitmap(bitmapRedimensionado);

            guardarFotoPerfil(bitmapRedimensionado);

            Toast.makeText(this, "Foto actualizada", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap redimensionarBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float ratioBitmap = (float) width / (float) height;
        float ratioMax = (float) maxWidth / (float) maxHeight;

        int finalWidth = maxWidth;
        int finalHeight = maxHeight;

        if (ratioMax > ratioBitmap) {
            finalWidth = (int) ((float) maxHeight * ratioBitmap);
        } else {
            finalHeight = (int) ((float) maxWidth / ratioBitmap);
        }

        return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true);
    }

    private void guardarFotoPerfil(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] imageBytes = baos.toByteArray();
            String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("foto_perfil_" + boleta, imageString);
            editor.apply();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar la foto", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarFotoPerfil() {
        try {
            String imageString = preferences.getString("foto_perfil_" + boleta, null);

            if (imageString != null) {
                byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                imgPerfil.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void eliminarFotoPerfil() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar")
                .setMessage("¿Eliminar foto de perfil?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.remove("foto_perfil_" + boleta);
                    editor.apply();

                    imgPerfil.setImageResource(R.drawable.imagen_prederterminada);

                    Toast.makeText(this, "Foto eliminada", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
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

    private void cerrarSesion() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(AlumnoVistaCuenta.this, IniciarSesion.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
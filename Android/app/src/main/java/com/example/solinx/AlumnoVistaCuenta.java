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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.example.solinx.DTO.NotificacionDTO;
import com.example.solinx.DTO.PerfilDTO;
import com.example.solinx.DTO.SolicitudDTO;
import com.example.solinx.UTIL.ThemeUtils;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlumnoVistaCuenta extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String TAG = "AlumnoVistaCuenta";
    private static final String CORREO_SOPORTE = "solinx.soporte@gmail.com";
    private static final int ID_SUPERVISOR = 12;

    // Views
    private ImageButton btnRegresar;
    private TextView tvBoleta, tvNombre, tvCorreo, tvEscuela, tvCarrera, tvPuntosStatus;
    private TextView btnEditarNombre, btnEditarCarrera, btnEditarEscuela;
    private TextView tvAlertaPendiente;
    private ImageView imgPerfil;
    private View viewModoClaro, viewModoOscuro;
    private Button btnCerrarSesion;
    private TextView btnContactarSoporte;

    // Horario
    private TextView tvHorarioLunes, tvHorarioMartes, tvHorarioMiercoles;
    private TextView tvHorarioJueves, tvHorarioViernes, tvHorarioSabado, tvHorarioDomingo;

    // Datos
    private String boleta, nombre, carrera, escuela, correo;
    private Integer idUsuario;

    private SharedPreferences preferences;
    private SharedPreferences prefsPendientes;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    // Carreras y escuelas disponibles
    private static final String[] CARRERAS = {
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

    private static final String[] ESCUELAS = {
            "ESCOM", "UPIICSA", "UPIITA", "UPIBI",
            "ESIA Ticomán", "ESIA Zacatenco",
            "ESIME Culhuacán", "ESIME Zacatenco",
            "ESIQIE", "ENCB"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumno_vista_cuenta);

        preferences    = getSharedPreferences("SoLinXPrefs", MODE_PRIVATE);
        prefsPendientes = getSharedPreferences("SoLinXCambiosPendientes", MODE_PRIVATE);

        initViews();
        setupImagePicker();
        cargarDatosUsuario();
        setupListeners();
        actualizarIndicadoresTema();
        cargarFotoPerfil();
        cargarSolicitudes();
        cargarHorario();
        mostrarAlertaPendientes();
    }

    private void initViews() {
        btnRegresar          = findViewById(R.id.regresar);
        tvBoleta             = findViewById(R.id.tvBoleta);
        tvNombre             = findViewById(R.id.tvNombre);
        tvCorreo             = findViewById(R.id.tvCorreo);
        tvEscuela            = findViewById(R.id.tvEscuela);
        tvCarrera            = findViewById(R.id.tvCarrera);
        tvPuntosStatus       = findViewById(R.id.tvPuntosStatus);
        imgPerfil            = findViewById(R.id.imgPerfil);
        viewModoClaro        = findViewById(R.id.viewModoClaro);
        viewModoOscuro       = findViewById(R.id.viewModoOscuro);
        btnCerrarSesion      = findViewById(R.id.btnCerrarSesion);
        btnContactarSoporte  = findViewById(R.id.btnContactarSoporte);
        btnEditarNombre      = findViewById(R.id.btnEditarNombre);
        btnEditarCarrera     = findViewById(R.id.btnEditarCarrera);
        btnEditarEscuela     = findViewById(R.id.btnEditarEscuela);
        tvAlertaPendiente    = findViewById(R.id.tvAlertaPendiente);

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

    private void cargarDatosUsuario() {
        Intent intent = getIntent();
        idUsuario = getSharedPreferences("sesion_usuario", MODE_PRIVATE).getInt("idUsuario", -1);

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

    private String obtenerDato(Intent intent, String intentKey, String prefKey, String defVal) {
        String val = intent.getStringExtra(intentKey);
        return val != null ? val : preferences.getString(prefKey, defVal);
    }

    // ─── ALERTA DE CAMBIOS PENDIENTES ─────────────────────────────────────────
    private void mostrarAlertaPendientes() {
        String key = "pendientes_" + idUsuario;
        String pendientesJson = prefsPendientes.getString(key, "");
        if (pendientesJson.isEmpty()) {
            tvAlertaPendiente.setVisibility(View.GONE);
            return;
        }
        try {
            org.json.JSONArray arr = new org.json.JSONArray(pendientesJson);
            if (arr.length() == 0) {
                tvAlertaPendiente.setVisibility(View.GONE);
                return;
            }
            StringBuilder campos = new StringBuilder();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                if (i > 0) campos.append(", ");
                campos.append(obj.getString("campo"));
            }
            tvAlertaPendiente.setVisibility(View.VISIBLE);
            tvAlertaPendiente.setText("⏳ Cambios en revisión: " + campos);
        } catch (Exception e) {
            tvAlertaPendiente.setVisibility(View.GONE);
        }
    }

    private boolean tienePendiente(String campo) {
        String key = "pendientes_" + idUsuario;
        String pendientesJson = prefsPendientes.getString(key, "");
        if (pendientesJson.isEmpty()) return false;
        try {
            org.json.JSONArray arr = new org.json.JSONArray(pendientesJson);
            for (int i = 0; i < arr.length(); i++) {
                if (arr.getJSONObject(i).getString("campo").equals(campo)) return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    private void agregarPendiente(String campo, String valorAnterior, String valorNuevo) {
        String key = "pendientes_" + idUsuario;
        String pendientesJson = prefsPendientes.getString(key, "[]");
        try {
            org.json.JSONArray arr = new org.json.JSONArray(pendientesJson);
            JSONObject obj = new JSONObject();
            obj.put("campo", campo);
            obj.put("valorAnterior", valorAnterior);
            obj.put("valorNuevo", valorNuevo);
            arr.put(obj);
            prefsPendientes.edit().putString(key, arr.toString()).apply();
            mostrarAlertaPendientes();
        } catch (Exception ignored) {}
    }

    // ─── EDICIÓN DE CAMPOS ────────────────────────────────────────────────────
    private void abrirEdicionNombre() {
        if (tienePendiente("Nombre")) {
            Toast.makeText(this, "Ya tienes un cambio pendiente para Nombre", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cambiar Nombre");

        EditText input = new EditText(this);
        input.setText(nombre);
        input.setPadding(40, 20, 40, 20);
        builder.setView(input);
        builder.setMessage("El cambio será enviado al supervisor para aprobación.");

        builder.setPositiveButton("Solicitar cambio", (dialog, which) -> {
            String nuevoValor = input.getText().toString().trim();
            if (nuevoValor.isEmpty()) {
                Toast.makeText(this, "Ingresa un valor", Toast.LENGTH_SHORT).show();
                return;
            }
            if (nuevoValor.equals(nombre)) {
                Toast.makeText(this, "El valor es igual al actual", Toast.LENGTH_SHORT).show();
                return;
            }
            confirmarCambio("Nombre", nombre, nuevoValor);
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void abrirEdicionCarrera() {
        if (tienePendiente("Carrera")) {
            Toast.makeText(this, "Ya tienes un cambio pendiente para Carrera", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cambiar Carrera");
        builder.setMessage("El cambio será enviado al supervisor para aprobación.");

        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, CARRERAS);
        spinner.setAdapter(adapter);
        // Seleccionar la carrera actual
        for (int i = 0; i < CARRERAS.length; i++) {
            if (CARRERAS[i].equals(carrera)) { spinner.setSelection(i); break; }
        }
        spinner.setPadding(40, 20, 40, 20);
        builder.setView(spinner);

        builder.setPositiveButton("Solicitar cambio", (dialog, which) -> {
            String nuevoValor = spinner.getSelectedItem().toString();
            if (nuevoValor.equals(carrera)) {
                Toast.makeText(this, "La carrera es igual a la actual", Toast.LENGTH_SHORT).show();
                return;
            }
            confirmarCambio("Carrera", carrera, nuevoValor);
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void abrirEdicionEscuela() {
        if (tienePendiente("Escuela")) {
            Toast.makeText(this, "Ya tienes un cambio pendiente para Escuela", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cambiar Escuela");
        builder.setMessage("El cambio será enviado al supervisor para aprobación.");

        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, ESCUELAS);
        spinner.setAdapter(adapter);
        for (int i = 0; i < ESCUELAS.length; i++) {
            if (ESCUELAS[i].equals(escuela)) { spinner.setSelection(i); break; }
        }
        spinner.setPadding(40, 20, 40, 20);
        builder.setView(spinner);

        builder.setPositiveButton("Solicitar cambio", (dialog, which) -> {
            String nuevoValor = spinner.getSelectedItem().toString();
            if (nuevoValor.equals(escuela)) {
                Toast.makeText(this, "La escuela es igual a la actual", Toast.LENGTH_SHORT).show();
                return;
            }
            confirmarCambio("Escuela", escuela, nuevoValor);
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void confirmarCambio(String campo, String valorAnterior, String valorNuevo) {
        new AlertDialog.Builder(this)
                .setTitle("¿Solicitar cambio?")
                .setMessage("Campo: " + campo
                        + "\nActual: " + valorAnterior
                        + "\nNuevo: " + valorNuevo
                        + "\n\nEl supervisor deberá aprobar este cambio.")
                .setPositiveButton("Confirmar", (d, w) -> {
                    agregarPendiente(campo, valorAnterior, valorNuevo);
                    enviarNotificacionSupervisor(campo, valorAnterior, valorNuevo);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void enviarNotificacionSupervisor(String campo, String valorAnterior, String valorNuevo) {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Map<String, String> body = new HashMap<>();
        body.put("idUsuario", String.valueOf(ID_SUPERVISOR));
        body.put("titulo", "Solicitud de cambio de perfil — " + campo);
        body.put("mensaje", "El alumno " + nombre + " (boleta: " + boleta + ") "
                + "solicita cambiar su " + campo.toLowerCase() + "."
                + "\n\nValor actual: \"" + valorAnterior + "\""
                + "\nNuevo valor: \"" + valorNuevo + "\""
                + "\n\nRevisa el panel de supervisor para aprobar o rechazar.");

        api.crearNotificacion(body).enqueue(new Callback<NotificacionDTO>() {
            @Override
            public void onResponse(@NonNull Call<NotificacionDTO> call,
                                   @NonNull Response<NotificacionDTO> response) {
                runOnUiThread(() ->
                        Toast.makeText(AlumnoVistaCuenta.this,
                                "Solicitud enviada al supervisor ✓", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onFailure(@NonNull Call<NotificacionDTO> call, @NonNull Throwable t) {
                runOnUiThread(() ->
                        Toast.makeText(AlumnoVistaCuenta.this,
                                "Cambio guardado localmente (sin conexión)", Toast.LENGTH_SHORT).show());
            }
        });
    }

    // ─── SOLICITUDES ──────────────────────────────────────────────────────────
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
                        runOnUiThread(() -> tvPuntosStatus.setText("Error al cargar solicitudes."));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<SolicitudDTO>> call, @NonNull Throwable t) {
                    runOnUiThread(() -> tvPuntosStatus.setText("Error de conexión."));
                }
            });
        } catch (NumberFormatException e) {
            tvPuntosStatus.setText("Boleta inválida.");
        }
    }

    private void mostrarSolicitudes(List<SolicitudDTO> lista) {
        runOnUiThread(() -> {
            if (lista.isEmpty()) {
                tvPuntosStatus.setText("No tienes solicitudes enviadas.");
                return;
            }
            android.text.SpannableStringBuilder sb = new android.text.SpannableStringBuilder();
            for (SolicitudDTO s : lista) {
                String empresa  = s.getNombreEmpresa()  != null ? s.getNombreEmpresa()  : "N/A";
                String proyecto = s.getNombreProyecto() != null ? s.getNombreProyecto() : "N/A";
                String estado   = s.getEstadoSolicitud();

                sb.append("Empresa: ").append(empresa).append("\n");
                sb.append("Proyecto: ").append(proyecto).append("\n");
                sb.append("Estado: ");

                String etiqueta;
                int color;
                if (estado == null) {
                    etiqueta = "Desconocido"; color = android.graphics.Color.GRAY;
                } else if (estado.equalsIgnoreCase("aceptada")) {
                    etiqueta = "Admitido ✅"; color = android.graphics.Color.parseColor("#38a169");
                } else if (estado.toLowerCase().startsWith("rechazada")) {
                    etiqueta = "No seleccionado ❌"; color = android.graphics.Color.parseColor("#e53e3e");
                } else {
                    etiqueta = "En espera ⏳"; color = android.graphics.Color.parseColor("#d69e2e");
                }

                int start = sb.length();
                sb.append(etiqueta);
                sb.setSpan(new android.text.style.ForegroundColorSpan(color),
                        start, sb.length(), android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                sb.append("\n\n");
            }
            tvPuntosStatus.setText(sb);
        });
    }

    // ─── HORARIO ──────────────────────────────────────────────────────────────
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

    @Override
    protected void onResume() {
        super.onResume();
        cargarFotoPerfil();
        cargarSolicitudes();
        cargarHorario();
        mostrarAlertaPendientes();
    }

    // ─── LISTENERS ────────────────────────────────────────────────────────────
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

        btnEditarNombre.setOnClickListener(v  -> abrirEdicionNombre());
        btnEditarCarrera.setOnClickListener(v -> abrirEdicionCarrera());
        btnEditarEscuela.setOnClickListener(v -> abrirEdicionEscuela());

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

    // ─── FOTO ─────────────────────────────────────────────────────────────────
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
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
        Map<String, String> body = new HashMap<>();
        body.put("foto", b64);
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.actualizarFoto(idUsuario, body).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(AlumnoVistaCuenta.this, "Error al guardar foto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "Fallo red al guardar foto: " + t.getMessage());
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
                    byte[] bytes = Base64.decode(response.body().getFoto(), Base64.DEFAULT);
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

    private void eliminarFotoPerfil() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar")
                .setMessage("¿Eliminar foto de perfil?")
                .setPositiveButton("Sí", (d, w) -> {
                    if (idUsuario == null || idUsuario == -1) return;
                    Map<String, String> body = new HashMap<>();
                    body.put("foto", "");
                    ApiService api = ApiClient.getClient().create(ApiService.class);
                    api.actualizarFoto(idUsuario, body).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            runOnUiThread(() -> {
                                imgPerfil.setImageResource(R.drawable.imagen_prederterminada);
                                Toast.makeText(AlumnoVistaCuenta.this, "Foto eliminada", Toast.LENGTH_SHORT).show();
                            });
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            Toast.makeText(AlumnoVistaCuenta.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }

    // ─── SOPORTE ──────────────────────────────────────────────────────────────
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
            if (mensaje.isEmpty()) { Toast.makeText(this, "Escribe un mensaje", Toast.LENGTH_SHORT).show(); return; }
            String cuerpo = "Alumno: " + nombre + "\nBoleta: " + boleta
                    + "\nCarrera: " + carrera + "\nEscuela: " + escuela
                    + "\n\nMensaje:\n" + mensaje;
            String mailtoUri = "mailto:" + CORREO_SOPORTE
                    + "?subject=" + Uri.encode("SoLinX - Soporte Alumno: " + nombre)
                    + "&body=" + Uri.encode(cuerpo);
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse(mailtoUri));
            try { startActivity(Intent.createChooser(emailIntent, "Enviar correo con...")); }
            catch (Exception e) { Toast.makeText(this, "No hay app de correo disponible", Toast.LENGTH_SHORT).show(); }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    // ─── CERRAR SESIÓN ────────────────────────────────────────────────────────
    private void cerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Seguro que quieres salir?")
                .setPositiveButton("Sí", (d, w) -> {
                    try { ThemeUtils.forceLightModeLocal(this); } catch (Exception ignored) {}
                    try {
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } catch (Exception ignored) {}
                    new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                        try {
                            getSharedPreferences("SoLinXPrefs", MODE_PRIVATE).edit().clear().apply();
                            getSharedPreferences("sesion_usuario", MODE_PRIVATE).edit().clear().apply();
                            getSharedPreferences("SoLinXCambiosPendientes", MODE_PRIVATE).edit().clear().apply();
                        } catch (Exception ignored) {}
                    }, 300);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
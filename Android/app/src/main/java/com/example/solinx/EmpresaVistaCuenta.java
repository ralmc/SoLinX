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
import android.text.InputFilter;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.example.solinx.DTO.CambioPerfilDTO;
import com.example.solinx.DTO.EmpresaDTO;
import com.example.solinx.DTO.NotificacionDTO;
import com.example.solinx.DTO.PerfilDTO;
import com.example.solinx.UTIL.ThemeUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmpresaVistaCuenta extends AppCompatActivity {

    private static final String TAG = "EmpresaVistaCuenta";
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String CORREO_SOPORTE = "solinx.soporte@gmail.com";
    private static final int ID_SUPERVISOR = 12;

    private ImageButton btnRegresar;
    private TextView tvNombre, tvCorreo, tvTelefono, tvNombrePersona;
    private TextView btnEditarNombre, btnEditarTelefono;
    private TextView tvAlertaPendiente;
    private TextView btnContactarSoporte, btnCerrarSesion;
    private ImageView imgPerfil;
    private View viewModoClaro, viewModoOscuro;

    private Integer idUsuario;
    private Integer idEmpresa;
    private String nombreEmpresa  = "";
    private String telefonoActual = "";
    private String correoActual   = "";

    private SharedPreferences preferences;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_vista_cuenta);

        preferences = getSharedPreferences("sesion_usuario", MODE_PRIVATE);
        idUsuario   = preferences.getInt("idUsuario",        -1);
        idEmpresa   = preferences.getInt("id_empresa_activa", -1);

        initViews();
        setupImagePicker();
        cargarDatosEmpresa();
        cargarFotoPerfil();
        mostrarNombrePersonaEnHeader();
        setupListeners();
        actualizarIndicadoresTema();
        mostrarAlertaPendientes();
    }

    private void initViews() {
        btnRegresar         = findViewById(R.id.regresar);
        tvNombre            = findViewById(R.id.tvNombre);
        tvCorreo            = findViewById(R.id.tvCorreo);
        tvTelefono          = findViewById(R.id.tvTelefono);
        tvNombrePersona     = findViewById(R.id.tvNombrePersona);
        imgPerfil           = findViewById(R.id.imgPerfil);
        viewModoClaro       = findViewById(R.id.viewModoClaro);
        viewModoOscuro      = findViewById(R.id.viewModoOscuro);
        btnContactarSoporte = findViewById(R.id.btnContactarSoporte);
        btnCerrarSesion     = findViewById(R.id.btnCerrarSesion);
        btnEditarNombre     = findViewById(R.id.btnEditarNombre);
        btnEditarTelefono   = findViewById(R.id.btnEditarTelefono);
        tvAlertaPendiente   = findViewById(R.id.tvAlertaPendiente);
    }

    private void mostrarNombrePersonaEnHeader() {
        if (tvNombrePersona == null) return;
        String nombre = preferences.getString("nombre", "Empresa");
        tvNombrePersona.setText(nombre != null && !nombre.isEmpty() ? nombre : "Empresa");
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatosEmpresa();
        cargarFotoPerfil();
        mostrarNombrePersonaEnHeader();
        mostrarAlertaPendientes();
    }

    // ─── DATOS FRESCOS DEL BACKEND ────────────────────────────────────────────
    private void cargarDatosEmpresa() {
        if (isFinishing() || isDestroyed()) return;
        if (idEmpresa == -1) {
            Toast.makeText(this, "Sesión no válida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.obtenerEmpresaPorId(idEmpresa).enqueue(new Callback<EmpresaDTO>() {
            @Override
            public void onResponse(@NonNull Call<EmpresaDTO> call,
                                   @NonNull Response<EmpresaDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EmpresaDTO e = response.body();
                    nombreEmpresa  = e.getNombreEmpresa() != null ? e.getNombreEmpresa() : "";
                    telefonoActual = e.getTelefono()      != null ? e.getTelefono()      : "";
                    correoActual   = e.getCorreo()        != null ? e.getCorreo()        : "";

                    // Actualizar SharedPreferences con datos frescos
                    preferences.edit()
                            .putString("nombre",   nombreEmpresa)
                            .putString("telefono", telefonoActual)
                            .apply();

                    runOnUiThread(() -> {
                        if (isFinishing() || isDestroyed()) return;
                        tvNombre.setText(!nombreEmpresa.isEmpty() ? nombreEmpresa : "N/A");
                        tvCorreo.setText(!correoActual.isEmpty()   ? correoActual   : "N/A");
                        tvTelefono.setText(!telefonoActual.isEmpty() ? telefonoActual : "N/A");
                        mostrarNombrePersonaEnHeader();
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

    // ─── ALERTA DE CAMBIOS PENDIENTES (BACKEND) ───────────────────────────────
    private void mostrarAlertaPendientes() {
        if (idUsuario == null || idUsuario == -1) return;
        ApiService api = ApiClient.getClient().create(ApiService.class);

        api.getCambiosPorUsuario(idUsuario).enqueue(new Callback<List<CambioPerfilDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<CambioPerfilDTO>> call,
                                   @NonNull Response<List<CambioPerfilDTO>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    runOnUiThread(() -> tvAlertaPendiente.setVisibility(View.GONE));
                    return;
                }
                List<CambioPerfilDTO> pendientes = new java.util.ArrayList<>();
                for (CambioPerfilDTO c : response.body()) {
                    if ("pendiente".equalsIgnoreCase(c.getEstado())) pendientes.add(c);
                }
                runOnUiThread(() -> {
                    if (pendientes.isEmpty()) {
                        tvAlertaPendiente.setVisibility(View.GONE);
                        return;
                    }
                    StringBuilder campos = new StringBuilder();
                    for (int i = 0; i < pendientes.size(); i++) {
                        if (i > 0) campos.append(", ");
                        campos.append(pendientes.get(i).getCampo());
                    }
                    tvAlertaPendiente.setVisibility(View.VISIBLE);
                    tvAlertaPendiente.setText("⏳ Cambios en revisión: " + campos);
                });
            }
            @Override
            public void onFailure(@NonNull Call<List<CambioPerfilDTO>> call, @NonNull Throwable t) {
                runOnUiThread(() -> tvAlertaPendiente.setVisibility(View.GONE));
            }
        });
    }

    private void verificarPendienteYAbrir(String campo, Runnable onNoPendiente) {
        if (idUsuario == null || idUsuario == -1) { onNoPendiente.run(); return; }
        ApiService api = ApiClient.getClient().create(ApiService.class);

        api.getCambiosPorUsuario(idUsuario).enqueue(new Callback<List<CambioPerfilDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<CambioPerfilDTO>> call,
                                   @NonNull Response<List<CambioPerfilDTO>> response) {
                boolean tienePendiente = false;
                if (response.isSuccessful() && response.body() != null) {
                    for (CambioPerfilDTO c : response.body()) {
                        if (campo.equalsIgnoreCase(c.getCampo())
                                && "pendiente".equalsIgnoreCase(c.getEstado())) {
                            tienePendiente = true; break;
                        }
                    }
                }
                boolean finalTiene = tienePendiente;
                runOnUiThread(() -> {
                    if (finalTiene) {
                        Toast.makeText(EmpresaVistaCuenta.this,
                                "Ya tienes un cambio pendiente para " + campo,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        onNoPendiente.run();
                    }
                });
            }
            @Override
            public void onFailure(@NonNull Call<List<CambioPerfilDTO>> call, @NonNull Throwable t) {
                runOnUiThread(onNoPendiente);
            }
        });
    }

    // ─── EDICIÓN DE CAMPOS ────────────────────────────────────────────────────
    private void abrirEdicionNombre() {
        verificarPendienteYAbrir("nombreEmpresa", () -> {
            EditText input = new EditText(this);
            input.setText(nombreEmpresa);
            input.setPadding(40, 20, 40, 20);

            new AlertDialog.Builder(this)
                    .setTitle("Cambiar Nombre de Empresa")
                    .setMessage("El cambio será enviado al supervisor para aprobación.")
                    .setView(input)
                    .setPositiveButton("Solicitar cambio", (d, w) -> {
                        String nuevoValor = input.getText().toString().trim();
                        if (nuevoValor.isEmpty()) {
                            Toast.makeText(this, "Ingresa un valor", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (nuevoValor.equals(nombreEmpresa)) {
                            Toast.makeText(this, "El nombre es igual al actual", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        confirmarCambio("nombreEmpresa", nombreEmpresa, nuevoValor);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    private void abrirEdicionTelefono() {
        verificarPendienteYAbrir("telefono", () -> {
            EditText input = new EditText(this);
            input.setText(telefonoActual);
            input.setInputType(InputType.TYPE_CLASS_PHONE);
            input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
            input.addTextChangedListener(new android.text.TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
                @Override public void afterTextChanged(android.text.Editable s) {}
                @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                    String filtered = s.toString().replaceAll("[^0-9]", "");
                    if (!filtered.equals(s.toString())) {
                        input.setText(filtered);
                        input.setSelection(filtered.length());
                    }
                }
            });
            input.setPadding(40, 20, 40, 20);

            new AlertDialog.Builder(this)
                    .setTitle("Cambiar Teléfono")
                    .setMessage("El cambio será enviado al supervisor. Debe tener exactamente 10 dígitos.")
                    .setView(input)
                    .setPositiveButton("Solicitar cambio", (d, w) -> {
                        String nuevoValor = input.getText().toString().trim();
                        if (nuevoValor.isEmpty()) {
                            Toast.makeText(this, "Ingresa un valor", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!nuevoValor.matches("\\d{10}")) {
                            Toast.makeText(this, "El teléfono debe tener exactamente 10 dígitos", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (nuevoValor.equals(telefonoActual)) {
                            Toast.makeText(this, "El teléfono es igual al actual", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        confirmarCambio("telefono", telefonoActual, nuevoValor);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    private void confirmarCambio(String campo, String valorAnterior, String valorNuevo) {
        String campoLabel = campo.equals("nombreEmpresa") ? "Nombre" : "Teléfono";
        new AlertDialog.Builder(this)
                .setTitle("¿Solicitar cambio?")
                .setMessage("Campo: " + campoLabel
                        + "\nActual: " + valorAnterior
                        + "\nNuevo: " + valorNuevo
                        + "\n\nEl supervisor deberá aprobar este cambio.")
                .setPositiveButton("Confirmar", (d, w) ->
                        enviarCambioAlBackend(campo, valorAnterior, valorNuevo))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // ─── ENVIAR CAMBIO AL BACKEND ─────────────────────────────────────────────
    private void enviarCambioAlBackend(String campo, String valorAnterior, String valorNuevo) {
        if (idUsuario == null || idUsuario == -1) return;
        ApiService api = ApiClient.getClient().create(ApiService.class);

        Map<String, String> body = new HashMap<>();
        body.put("idUsuario",     String.valueOf(idUsuario));
        body.put("rol",           "empresa");
        body.put("campo",         campo);
        body.put("valorAnterior", valorAnterior);
        body.put("valorNuevo",    valorNuevo);

        api.solicitarCambioPerfil(body).enqueue(new Callback<CambioPerfilDTO>() {
            @Override
            public void onResponse(@NonNull Call<CambioPerfilDTO> call,
                                   @NonNull Response<CambioPerfilDTO> response) {
                if (response.isSuccessful()) {
                    enviarNotificacionSupervisor(campo, valorAnterior, valorNuevo);
                    runOnUiThread(() -> {
                        Toast.makeText(EmpresaVistaCuenta.this,
                                "Solicitud enviada al supervisor ✓", Toast.LENGTH_SHORT).show();
                        mostrarAlertaPendientes();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(EmpresaVistaCuenta.this,
                            "Error al enviar solicitud", Toast.LENGTH_SHORT).show());
                }
            }
            @Override
            public void onFailure(@NonNull Call<CambioPerfilDTO> call, @NonNull Throwable t) {
                runOnUiThread(() -> Toast.makeText(EmpresaVistaCuenta.this,
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void enviarNotificacionSupervisor(String campo, String valorAnterior, String valorNuevo) {
        String campoLabel = campo.equals("nombreEmpresa") ? "Nombre de empresa" : "Teléfono";
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Map<String, String> body = new HashMap<>();
        body.put("idUsuario", String.valueOf(ID_SUPERVISOR));
        body.put("titulo",    "Solicitud de cambio de perfil empresa — " + campoLabel);
        body.put("mensaje",   "La empresa " + nombreEmpresa + " solicita cambiar su "
                + campoLabel.toLowerCase() + "."
                + "\n\nValor actual: \"" + valorAnterior + "\""
                + "\nNuevo valor: \"" + valorNuevo + "\""
                + "\n\nRevisa el panel de cambios del supervisor.");

        api.crearNotificacion(body).enqueue(new Callback<NotificacionDTO>() {
            @Override
            public void onResponse(@NonNull Call<NotificacionDTO> call,
                                   @NonNull Response<NotificacionDTO> response) {}
            @Override
            public void onFailure(@NonNull Call<NotificacionDTO> call, @NonNull Throwable t) {
                Log.e(TAG, "Error enviando notificación: " + t.getMessage());
            }
        });
    }

    // ─── FOTO ─────────────────────────────────────────────────────────────────
    private void setupImagePicker() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK
                            && result.getData() != null
                            && result.getData().getData() != null) {
                        procesarImagen(result.getData().getData());
                    }
                });
    }

    private void cargarFotoPerfil() {
        if (idUsuario == null || idUsuario == -1) return;
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
                Log.e(TAG, "Error al cargar foto: " + t.getMessage());
            }
        });
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
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
                if (!response.isSuccessful())
                    Toast.makeText(EmpresaVistaCuenta.this, "Error al guardar foto", Toast.LENGTH_SHORT).show();
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
                    HashMap<String, String> body = new HashMap<>();
                    body.put("foto", "");
                    ApiService api = ApiClient.getClient().create(ApiService.class);
                    api.actualizarFoto(idUsuario, body).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            runOnUiThread(() -> {
                                imgPerfil.setImageResource(R.drawable.imagen_prederterminada);
                                Toast.makeText(EmpresaVistaCuenta.this, "Foto eliminada", Toast.LENGTH_SHORT).show();
                            });
                        }
                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            Toast.makeText(EmpresaVistaCuenta.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("No", null)
                .show();
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
        btnEditarNombre.setOnClickListener(v   -> abrirEdicionNombre());
        btnEditarTelefono.setOnClickListener(v -> abrirEdicionTelefono());

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
            if (mensaje.isEmpty()) { Toast.makeText(this, "Escribe un mensaje", Toast.LENGTH_SHORT).show(); return; }
            String cuerpo = "Empresa: " + nombreEmpresa + "\nCorreo: " + correoActual
                    + "\nTeléfono: " + telefonoActual + "\n\nMensaje:\n" + mensaje;
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:" + CORREO_SOPORTE
                    + "?subject=" + Uri.encode("SoLinX - Soporte Empresa: " + nombreEmpresa)
                    + "&body=" + Uri.encode(cuerpo)));
            try { startActivity(Intent.createChooser(emailIntent, "Enviar correo con...")); dialog.dismiss(); }
            catch (Exception e) { Toast.makeText(this, "No hay app de correo disponible", Toast.LENGTH_SHORT).show(); }
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
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                getSharedPreferences("SoLinXPrefs",    MODE_PRIVATE).edit().clear().apply();
                getSharedPreferences("sesion_usuario", MODE_PRIVATE).edit().clear().apply();
            }, 300);
            finish();
        });

        dialog.show();
        dialog.getWindow().setLayout(
                (int)(getResources().getDisplayMetrics().widthPixels * 0.85),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
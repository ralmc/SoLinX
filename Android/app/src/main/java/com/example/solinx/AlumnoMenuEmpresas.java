package com.example.solinx;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.solinx.API.ApiClient;
import com.example.solinx.API.ApiService;
import com.example.solinx.DTO.SolicitudDTO;
import com.example.solinx.UTIL.ThemeUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlumnoMenuEmpresas extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AlumnoMenuEmpresas";

    ImageView fotoperfil;
    TextView tvBoleta, btnTabEmpresas, btnTabDocumentos;
    View lineaTabEmpresas, lineaTabDocumentos;

    private Integer idUsuario;
    private String nombreUsuario;
    private String correoUsuario;
    private String rolUsuario;
    private boolean alumnoAceptado = false;
    private AlumnoEmpresas empresasFragment;
    private AlumnoDocumentos documentosFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alumno_menu_empresas);

        recibirDatosDelUsuario();

        fotoperfil         = findViewById(R.id.fotoperfil);
        tvBoleta           = findViewById(R.id.tvBoleta);
        btnTabEmpresas     = findViewById(R.id.btnTabEmpresas);
        btnTabDocumentos   = findViewById(R.id.btnTabDocumentos);
        lineaTabEmpresas   = findViewById(R.id.lineaTabEmpresas);
        lineaTabDocumentos = findViewById(R.id.lineaTabDocumentos);

        fotoperfil.setOnClickListener(this);
        btnTabEmpresas.setOnClickListener(this);
        btnTabDocumentos.setOnClickListener(this);

        if (nombreUsuario != null) {
            tvBoleta.setText("Hola, " + nombreUsuario);
        }

        cargarFotoPerfil();
        mostrarTabEmpresas();
        verificarSiAlumnoAceptado();
    }

    // ─── Verificar si el alumno tiene solicitud aceptada ─────────────────────
    private void verificarSiAlumnoAceptado() {
        SharedPreferences prefs = getSharedPreferences("SoLinXPrefs", MODE_PRIVATE);
        String boletaStr = prefs.getString("boleta", null);
        if (boletaStr == null || boletaStr.equals("N/A")) return;

        int boleta = Integer.parseInt(boletaStr);
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.obtenerSolicitudesEstudiante(boleta).enqueue(new Callback<List<SolicitudDTO>>() {
            @Override
            public void onResponse(Call<List<SolicitudDTO>> call,
                                   Response<List<SolicitudDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean aceptado = response.body().stream()
                            .anyMatch(s -> "aceptada".equalsIgnoreCase(s.getEstadoSolicitud()));
                    runOnUiThread(() -> actualizarEstadoTab(aceptado));
                }
            }

            @Override
            public void onFailure(Call<List<SolicitudDTO>> call, Throwable t) {
                Log.e(TAG, "Error al verificar aceptación: " + t.getMessage());
            }
        });
    }

    private void actualizarEstadoTab(boolean aceptado) {
        alumnoAceptado = aceptado;

        if (aceptado) {
            btnTabDocumentos.setAlpha(1.0f);
            btnTabDocumentos.setClickable(true);
            btnTabDocumentos.setFocusable(true);
        } else {
            btnTabDocumentos.setAlpha(0.35f);
            btnTabDocumentos.setClickable(false);
            btnTabDocumentos.setFocusable(false);
        }
    }

    // ─── Tabs ─────────────────────────────────────────────────────────────────
    private void mostrarTabEmpresas() {
        lineaTabEmpresas.setVisibility(View.VISIBLE);
        lineaTabDocumentos.setVisibility(View.INVISIBLE);

        btnTabEmpresas.setTypeface(null, Typeface.BOLD);
        btnTabEmpresas.setTextColor(getResources().getColorStateList(
                android.R.color.tab_indicator_text, getTheme()));
        btnTabDocumentos.setTypeface(null, Typeface.NORMAL);
        btnTabDocumentos.setTextColor(
                getResources().getColor(android.R.color.darker_gray, getTheme()));

        if (empresasFragment == null) empresasFragment = new AlumnoEmpresas();
        cargarFragment(empresasFragment);
    }

    private void mostrarTabDocumentos() {
        if (!alumnoAceptado) {
            Toast.makeText(this,
                    "Debes ser aceptado en un proyecto primero",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        lineaTabEmpresas.setVisibility(View.INVISIBLE);
        lineaTabDocumentos.setVisibility(View.VISIBLE);

        btnTabDocumentos.setTypeface(null, Typeface.BOLD);
        btnTabDocumentos.setTextColor(getResources().getColorStateList(
                android.R.color.tab_indicator_text, getTheme()));
        btnTabEmpresas.setTypeface(null, Typeface.NORMAL);
        btnTabEmpresas.setTextColor(
                getResources().getColor(android.R.color.darker_gray, getTheme()));

        if (documentosFragment == null) documentosFragment = new AlumnoDocumentos();
        cargarFragment(documentosFragment);
    }

    private void cargarFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameAlumno, fragment)
                .commit();
    }

    // ─── Foto de perfil ───────────────────────────────────────────────────────
    private void cargarFotoPerfil() {
        SharedPreferences prefs = getSharedPreferences("SoLinXPrefs", MODE_PRIVATE);
        String boleta = prefs.getString("boleta", "N/A");
        String b64 = getSharedPreferences("SoLinXFotos", MODE_PRIVATE)
                .getString("foto_perfil_" + boleta, null);
        if (b64 != null) {
            byte[] bytes = Base64.decode(b64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            fotoperfil.setImageBitmap(bitmap);
        }
    }

    // ─── Click ────────────────────────────────────────────────────────────────
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == fotoperfil.getId()) {
            navegarAVistaCuenta();
        } else if (id == R.id.btnTabEmpresas) {
            mostrarTabEmpresas();
        } else if (id == R.id.btnTabDocumentos) {
            mostrarTabDocumentos();
        }
    }

    private void navegarAVistaCuenta() {
        Intent intent = new Intent(this, AlumnoVistaCuenta.class);
        SharedPreferences prefs = getSharedPreferences("SoLinXPrefs", MODE_PRIVATE);
        intent.putExtra("nombre", nombreUsuario);
        intent.putExtra("correo", correoUsuario);
        intent.putExtra("boleta", prefs.getString("boleta", "N/A"));
        intent.putExtra("carrera", prefs.getString("carrera", "N/A"));
        intent.putExtra("escuela", prefs.getString("escuela", "N/A"));
        intent.putExtra("telefono", prefs.getString("telefono", "N/A"));
        startActivity(intent);
    }

    // ─── Lifecycle ────────────────────────────────────────────────────────────
    @Override
    protected void onResume() {
        super.onResume();
        cargarFotoPerfil();
        verificarSiAlumnoAceptado();
        if (empresasFragment != null && empresasFragment.isVisible()) {
            empresasFragment.cargarProyectos();
        }
    }

    // ─── Sesión ───────────────────────────────────────────────────────────────
    private void recibirDatosDelUsuario() {
        Intent intent = getIntent();
        if (intent != null) {
            idUsuario     = intent.getIntExtra("idUsuario", -1);
            nombreUsuario = intent.getStringExtra("nombre");
            correoUsuario = intent.getStringExtra("correo");
            rolUsuario    = intent.getStringExtra("rol");
            Log.d(TAG, "Usuario logueado: " + nombreUsuario);
            guardarEnSharedPreferences();
        }
    }

    private void guardarEnSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences("sesion_usuario", MODE_PRIVATE);
        prefs.edit()
                .putInt("idUsuario", idUsuario)
                .putString("nombre", nombreUsuario)
                .putString("correo", correoUsuario)
                .putString("rol", rolUsuario)
                .apply();
    }
}

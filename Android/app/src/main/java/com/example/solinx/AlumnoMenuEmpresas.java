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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.solinx.UTIL.ThemeUtils;

public class AlumnoMenuEmpresas extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AlumnoMenuEmpresas";

    ImageView fotoperfil;
    TextView tvBoleta, btnTabEmpresas, btnTabDocumentos;
    View lineaTabEmpresas, lineaTabDocumentos;

    private Integer idUsuario;
    private String nombreUsuario;
    private String correoUsuario;
    private String rolUsuario;

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
    }

    private void mostrarTabEmpresas() {
        // Líneas
        lineaTabEmpresas.setVisibility(View.VISIBLE);
        lineaTabDocumentos.setVisibility(View.INVISIBLE);

        // Texto tabs
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
        // Líneas
        lineaTabEmpresas.setVisibility(View.INVISIBLE);
        lineaTabDocumentos.setVisibility(View.VISIBLE);

        // Texto tabs
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

    @Override
    protected void onResume() {
        super.onResume();
        cargarFotoPerfil();
        if (empresasFragment != null && empresasFragment.isVisible()) {
            empresasFragment.cargarProyectos();
        }
    }

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
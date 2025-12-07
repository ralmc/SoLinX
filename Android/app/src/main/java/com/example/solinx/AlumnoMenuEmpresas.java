package com.example.solinx;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solinx.ADAPTER.ProyectoAdapter;
import com.example.solinx.API.ApiClient;
import com.example.solinx.API.ApiService;
import com.example.solinx.RESPONSE.ProyectoResponse;
import com.example.solinx.UTIL.ThemeUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlumnoMenuEmpresas extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AlumnoMenuEmpresas";

    ImageView fotoperfil;
    RecyclerView recyclerViewProyectos;
    TextView txtNoProyectos, tvboleta;
    ProgressBar progressBar;

    ProyectoAdapter proyectoAdapter;
    ApiService apiService;

    // Recibir datos del IniciarSesion
    private Integer idUsuario;
    private String nombreUsuario;
    private String correoUsuario;
    private String rolUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alumno_menu_empresas);

        recibirDatosDelUsuario();
        apiService = ApiClient.getClient().create(ApiService.class);

        fotoperfil = findViewById(R.id.fotoperfil);
        recyclerViewProyectos = findViewById(R.id.recyclerViewProyectos);
        txtNoProyectos = findViewById(R.id.txtNoProyectos);
        tvboleta = findViewById(R.id.tvBoleta);
        progressBar = findViewById(R.id.progressBar);

        fotoperfil.setOnClickListener(this);

        if (nombreUsuario != null) {
            tvboleta.setText("Hola, " + nombreUsuario);
        }

        configurarRecyclerView();
        cargarProyectosDesdeAPI();
    }

    private void configurarRecyclerView() {
        recyclerViewProyectos.setLayoutManager(new LinearLayoutManager(this));
        proyectoAdapter = new ProyectoAdapter(this);
        recyclerViewProyectos.setAdapter(proyectoAdapter);
    }

    private void cargarProyectosDesdeAPI() {
        mostrarCargando(true);

        Call<List<ProyectoResponse>> call = apiService.obtenerProyectos();

        call.enqueue(new Callback<List<ProyectoResponse>>() {
            @Override
            public void onResponse(Call<List<ProyectoResponse>> call, Response<List<ProyectoResponse>> response) {
                mostrarCargando(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<ProyectoResponse> proyectos = response.body();
                    Log.d(TAG, "Proyectos recibidos: " + proyectos.size());

                    if (proyectos.isEmpty()) {
                        mostrarMensajeVacio(true);
                    } else {
                        mostrarMensajeVacio(false);
                        proyectoAdapter.setProyectos(proyectos);
                    }
                } else {
                    Log.e(TAG, "Error en respuesta: " + response.code());
                    mostrarError("Error al cargar proyectos: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ProyectoResponse>> call, Throwable t) {
                mostrarCargando(false);
                Log.e(TAG, "Error de conexión: " + t.getMessage(), t);
                mostrarError("Error de conexión: " + t.getMessage());
            }
        });
    }

    private void mostrarCargando(boolean mostrar) {
        if (mostrar) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerViewProyectos.setVisibility(View.GONE);
            txtNoProyectos.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void mostrarMensajeVacio(boolean mostrar) {
        if (mostrar) {
            txtNoProyectos.setText("No hay proyectos disponibles");
            txtNoProyectos.setVisibility(View.VISIBLE);
            recyclerViewProyectos.setVisibility(View.GONE);
        } else {
            txtNoProyectos.setVisibility(View.GONE);
            recyclerViewProyectos.setVisibility(View.VISIBLE);
        }
    }

    private void mostrarError(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
        txtNoProyectos.setText("Error al cargar proyectos.\nVerifica tu conexión.");
        txtNoProyectos.setVisibility(View.VISIBLE);
        recyclerViewProyectos.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == fotoperfil.getId()) {
            // 🆕 NAVEGACIÓN MEJORADA A AlumnoVistaCuenta
            navegarAVistaCuenta();
        }
    }

    /**
     * 🆕 Método mejorado para navegar a la vista de cuenta del alumno
     * Recupera todos los datos de SharedPreferences y los envía al Intent
     */
    private void navegarAVistaCuenta() {
        Intent intent = new Intent(this, AlumnoVistaCuenta.class);

        // Obtener datos de SharedPreferences (guardados durante el login)
        SharedPreferences prefs = getSharedPreferences("SoLinXPrefs", MODE_PRIVATE);

        // Enviar datos básicos del usuario
        intent.putExtra("nombre", nombreUsuario);
        intent.putExtra("correo", correoUsuario);

        // 🆕 Enviar datos específicos del estudiante desde SharedPreferences
        String boleta = prefs.getString("boleta", "N/A");
        String carrera = prefs.getString("carrera", "N/A");
        String escuela = prefs.getString("escuela", "N/A");
        String telefono = prefs.getString("telefono", "N/A");

        intent.putExtra("boleta", boleta);
        intent.putExtra("carrera", carrera);
        intent.putExtra("escuela", escuela);
        intent.putExtra("telefono", telefono);

        // Log para debugging
        Log.d(TAG, "Navegando a AlumnoVistaCuenta con datos:");
        Log.d(TAG, "  Boleta: " + boleta);
        Log.d(TAG, "  Nombre: " + nombreUsuario);
        Log.d(TAG, "  Carrera: " + carrera);
        Log.d(TAG, "  Escuela: " + escuela);
        Log.d(TAG, "  Correo: " + correoUsuario);
        Log.d(TAG, "  Teléfono: " + telefono);

        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarProyectosDesdeAPI();
    }

    private void recibirDatosDelUsuario() {
        Intent intent = getIntent();
        if (intent != null) {
            idUsuario = intent.getIntExtra("idUsuario", -1);
            nombreUsuario = intent.getStringExtra("nombre");
            correoUsuario = intent.getStringExtra("correo");
            rolUsuario = intent.getStringExtra("rol");

            // Log para verificar que llegaron los datos
            Log.d(TAG, "Usuario logueado: " + nombreUsuario + " (" + correoUsuario + ")");

            // Guardar en SharedPreferences para no perderlos
            guardarEnSharedPreferences();
        }
    }

    private void guardarEnSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences("sesion_usuario", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("idUsuario", idUsuario);
        editor.putString("nombre", nombreUsuario);
        editor.putString("correo", correoUsuario);
        editor.putString("rol", rolUsuario);
        editor.apply();
    }
}
package com.example.solinx;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solinx.API.ApiClient;
import com.example.solinx.API.ApiService;
import com.example.solinx.RESPONSE.AprobacionResponse;
import com.example.solinx.RESPONSE.SolicitudesResponse;
import com.example.solinx.ADAPTER.SolicitudesAdapter;
import com.example.solinx.UTIL.ThemeUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AprobarSolicitudesAlumnosActivity extends AppCompatActivity {

    private static final String TAG = "AprobarSolicitudes";

    private RecyclerView recyclerView;
    private SolicitudesAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvNoData;

    private ApiService apiService;
    private int idSupervisor;
    private List<Solicitudes> solicitudesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aprobar_solicitudes_alumnos);

        // Obtener datos del Intent
        idSupervisor = getIntent().getIntExtra("idSupervisor", -1);

        if (idSupervisor == -1) {
            Toast.makeText(this, "Error: ID Supervisor no válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inicializar vistas
        initViews();

        // Inicializar API
        apiService = ApiClient.getClient().create(ApiService.class);

        // Configurar RecyclerView
        setupRecyclerView();

        // Cargar solicitudes
        cargarSolicitudes();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_solicitudes);
        progressBar = findViewById(R.id.progress_bar);
        tvNoData = findViewById(R.id.tv_no_data);
    }

    private void setupRecyclerView() {
        solicitudesList = new ArrayList<>();
        adapter = new SolicitudesAdapter(solicitudesList, new SolicitudesAdapter.OnSolicitudClickListener() {
            @Override
            public void onAprobar(Solicitudes solicitud) {
                mostrarDialogoConfirmacion(solicitud, "aceptar");
            }

            @Override
            public void onRechazar(Solicitudes solicitud) {
                mostrarDialogoConfirmacion(solicitud, "rechazar");
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void cargarSolicitudes() {
        progressBar.setVisibility(View.VISIBLE);
        tvNoData.setVisibility(View.GONE);

        Call<SolicitudesResponse> call = apiService.getSolicitudesEnviadas(idSupervisor);

        call.enqueue(new Callback<SolicitudesResponse>() {
            @Override
            public void onResponse(Call<SolicitudesResponse> call, Response<SolicitudesResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    SolicitudesResponse solicitudesResponse = response.body();

                    if (solicitudesResponse.isSuccess()) {
                        solicitudesList.clear();
                        solicitudesList.addAll(solicitudesResponse.getSolicitudes());
                        adapter.notifyDataSetChanged();

                        if (solicitudesList.isEmpty()) {
                            tvNoData.setVisibility(View.VISIBLE);
                            tvNoData.setText("No hay solicitudes pendientes");
                        }
                    } else {
                        Toast.makeText(AprobarSolicitudesAlumnosActivity.this,
                                solicitudesResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AprobarSolicitudesAlumnosActivity.this,
                            "Error al cargar solicitudes", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Response not successful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<SolicitudesResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Error: " + t.getMessage());
                Toast.makeText(AprobarSolicitudesAlumnosActivity.this,
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarDialogoConfirmacion(Solicitudes solicitud, String accion) {
        String mensaje = accion.equals("aceptar")
                ? "¿Aceptar solicitud de " + solicitud.getNombreEstudiante() + "?"
                : "¿Rechazar solicitud de " + solicitud.getNombreEstudiante() + "?";

        new AlertDialog.Builder(this)
                .setTitle("Confirmación")
                .setMessage(mensaje)
                .setPositiveButton("Sí", (dialog, which) -> procesarSolicitud(solicitud, accion))
                .setNegativeButton("No", null)
                .show();
    }

    private void procesarSolicitud(Solicitudes solicitud, String accion) {
        progressBar.setVisibility(View.VISIBLE);

        String nuevoEstado = accion.equals("aceptar") ? "aceptada" : "rechazada";

        Call<AprobacionResponse> call = apiService.actualizarSolicitud(
                solicitud.getIdSolicitud(),
                nuevoEstado
        );

        call.enqueue(new Callback<AprobacionResponse>() {
            @Override
            public void onResponse(Call<AprobacionResponse> call, Response<AprobacionResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    AprobacionResponse aprobacionResponse = response.body();

                    if (aprobacionResponse.isSuccess()) {
                        Toast.makeText(AprobarSolicitudesAlumnosActivity.this,
                                aprobacionResponse.getMessage(), Toast.LENGTH_SHORT).show();

                        cargarSolicitudes();
                    } else {
                        Toast.makeText(AprobarSolicitudesAlumnosActivity.this,
                                aprobacionResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AprobarSolicitudesAlumnosActivity.this,
                            "Error al procesar solicitud", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Response not successful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AprobacionResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Error: " + t.getMessage());
                Toast.makeText(AprobarSolicitudesAlumnosActivity.this,
                        "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
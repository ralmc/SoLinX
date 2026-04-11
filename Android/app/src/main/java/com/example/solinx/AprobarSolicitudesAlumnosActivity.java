package com.example.solinx;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solinx.ADAPTER.SolicitudesAlumnoAdapter;
import com.example.solinx.API.ApiClient;
import com.example.solinx.API.ApiService;
import com.example.solinx.RESPONSE.AprobacionResponse;
import com.example.solinx.RESPONSE.SolicitudesResponse;
import com.example.solinx.UTIL.ThemeUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AprobarSolicitudesAlumnosActivity extends AppCompatActivity {

    private static final String TAG = "AprobarSolicitudes";

    private RecyclerView recyclerView;
    private SolicitudesAlumnoAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvNoData;

    private ApiService apiService;
    private int idSupervisor;
    private List<Solicitudes> solicitudesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumnos_lista_solicitudes);

        idSupervisor = getIntent().getIntExtra("idSupervisor", -1);

        if (idSupervisor == -1) {
            Toast.makeText(this, "Error: ID Supervisor no válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        apiService = ApiClient.getClient().create(ApiService.class);
        setupRecyclerView();
        cargarSolicitudes();

        // Botón flecha de regreso
        ImageButton btnRegresar = findViewById(R.id.btnRegresarFlecha);
        if (btnRegresar != null) {
            btnRegresar.setOnClickListener(v -> finish());
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_solicitudes);
        progressBar = findViewById(R.id.progress_bar);
        tvNoData = findViewById(R.id.tv_no_data);
    }

    private void setupRecyclerView() {
        solicitudesList = new ArrayList<>();
        adapter = new SolicitudesAlumnoAdapter(solicitudesList, new SolicitudesAlumnoAdapter.OnClickListener() {
            @Override
            public void onAprobar(Solicitudes s) {
                mostrarDialogoConfirmacion(s, "aprobar");
            }

            @Override
            public void onRechazar(Solicitudes s) {
                mostrarDialogoConfirmacion(s, "rechazar");
            }

            @Override
            public void onEnviarCorreo(Solicitudes s) {
                mostrarOpcionesCorreo(s);
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
                    SolicitudesResponse r = response.body();

                    if (r.isSuccess()) {
                        solicitudesList.clear();
                        if (r.getSolicitudes() != null) {
                            solicitudesList.addAll(r.getSolicitudes());
                        }
                        adapter.notifyDataSetChanged();

                        if (solicitudesList.isEmpty()) {
                            tvNoData.setVisibility(View.VISIBLE);
                            tvNoData.setText("No hay solicitudes pendientes");
                        }
                    } else {
                        Toast.makeText(AprobarSolicitudesAlumnosActivity.this,
                                r.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AprobarSolicitudesAlumnosActivity.this,
                            "Error al cargar solicitudes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SolicitudesResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AprobarSolicitudesAlumnosActivity.this,
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // ─── Correo ───────────────────────────────────────────────────────────────

    private void mostrarOpcionesCorreo(Solicitudes solicitud) {
        String[] opciones = {"Enviar correo al Alumno", "Enviar correo a la Empresa"};

        new AlertDialog.Builder(this)
                .setTitle("Enviar correo")
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        mostrarDialogoCorreo(solicitud.getCorreoEstudiante(),
                                solicitud.getNombreEstudiante(), "Alumno", solicitud.getNombreProyecto());
                    } else {
                        mostrarDialogoCorreo(solicitud.getCorreoEmpresa(),
                                solicitud.getNombreEmpresa(), "Empresa", solicitud.getNombreProyecto());
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoCorreo(String correo, String nombre, String tipo, String proyecto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Correo a " + tipo + ": " + (nombre != null ? nombre : ""));

        final EditText input = new EditText(this);
        input.setHint("Escribe tu mensaje aquí...");
        input.setMinLines(3);
        input.setPadding(40, 20, 40, 20);
        builder.setView(input);

        builder.setPositiveButton("Enviar", (dialog, which) -> {
            String mensaje = input.getText().toString().trim();
            if (mensaje.isEmpty()) {
                Toast.makeText(this, "Escribe un mensaje", Toast.LENGTH_SHORT).show();
                return;
            }

            String destinatario = (correo != null && !correo.isEmpty()) ? correo : "";
            String subject = "SoLinX - Supervisor: Sobre proyecto " + (proyecto != null ? proyecto : "");

            String mailtoUri = "mailto:" + destinatario
                    + "?subject=" + Uri.encode(subject)
                    + "&body=" + Uri.encode(mensaje);

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse(mailtoUri));

            try {
                startActivity(Intent.createChooser(emailIntent, "Enviar correo con..."));
            } catch (Exception e) {
                Toast.makeText(this, "No hay app de correo disponible", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    // ─── Aprobación ───────────────────────────────────────────────────────────

    private void mostrarDialogoConfirmacion(Solicitudes solicitud, String accion) {
        String mensaje = accion.equals("aprobar")
                ? "¿Aprobar solicitud de " + solicitud.getNombreEstudiante() + "?"
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

        String nuevoEstado = accion.equals("aprobar") ? "aprobada" : "rechazada";

        Call<AprobacionResponse> call = apiService.actualizarSolicitud(
                solicitud.getIdSolicitud(), nuevoEstado);

        call.enqueue(new Callback<AprobacionResponse>() {
            @Override
            public void onResponse(Call<AprobacionResponse> call, Response<AprobacionResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(AprobarSolicitudesAlumnosActivity.this,
                                response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        cargarSolicitudes();
                    } else {
                        Toast.makeText(AprobarSolicitudesAlumnosActivity.this,
                                response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AprobarSolicitudesAlumnosActivity.this,
                            "Error al procesar solicitud", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AprobacionResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AprobarSolicitudesAlumnosActivity.this,
                        "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

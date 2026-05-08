package com.example.solinx;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solinx.ADAPTER.DocumentoSupervisorAdapter;
import com.example.solinx.API.ApiClient;
import com.example.solinx.API.ApiService;
import com.example.solinx.DTO.DocumentoDTO;
import com.example.solinx.RESPONSE.SolicitudesResponse;
import com.example.solinx.Solicitudes;
import com.example.solinx.UTIL.ThemeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupervisorDocumentos extends AppCompatActivity {

    private static final String TAG = "SupervisorDocumentos";

    private RecyclerView recyclerView;
    private DocumentoSupervisorAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvNoData;
    private ApiService apiService;
    private int idSupervisor;
    private int idEmpresa;

    // Lista plana de items para el RecyclerView
    private final List<DocumentoSupervisorAdapter.DocumentoItem> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_documentos);

        idSupervisor = getIntent().getIntExtra("idSupervisor", -1);
        idEmpresa    = getIntent().getIntExtra("idEmpresa", -1);

        apiService   = ApiClient.getClient().create(ApiService.class);

        recyclerView = findViewById(R.id.recycler_documentos);
        progressBar  = findViewById(R.id.progress_bar);
        tvNoData     = findViewById(R.id.tv_no_data);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new DocumentoSupervisorAdapter(items, new DocumentoSupervisorAdapter.OnClickListener() {
            @Override
            public void onAprobar(DocumentoSupervisorAdapter.DocumentoItem item) {
                mostrarDialogo(item, true);
            }
            @Override
            public void onRechazar(DocumentoSupervisorAdapter.DocumentoItem item) {
                mostrarDialogo(item, false);
            }
        });
        recyclerView.setAdapter(adapter);

        ImageButton btnRegresar = findViewById(R.id.btnRegresarFlecha);
        if (btnRegresar != null) btnRegresar.setOnClickListener(v -> finish());

        cargarDocumentos();
    }

    private void cargarDocumentos() {
        progressBar.setVisibility(View.VISIBLE);
        tvNoData.setVisibility(View.GONE);
        items.clear();

        // 1. Traer todas las solicitudes aceptadas
        apiService.getSolicitudesAceptadas(idEmpresa).enqueue(new Callback<SolicitudesResponse>() {
            @Override
            public void onResponse(Call<SolicitudesResponse> call, Response<SolicitudesResponse> response) {
                if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()) {
                    mostrarError("Error al cargar solicitudes");
                    return;
                }

                List<Solicitudes> aceptados = response.body().getSolicitudes();
                if (aceptados == null || aceptados.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    tvNoData.setVisibility(View.VISIBLE);
                    tvNoData.setText("Sin alumnos aceptados aún");
                    return;
                }

                // 2. Por cada alumno aceptado, traer sus documentos
                final int[] pendientes = {aceptados.size()};
                for (Solicitudes sol : aceptados) {
                    int boleta = sol.getBoleta();
                    apiService.getDocumentos(boleta).enqueue(new Callback<List<DocumentoDTO>>() {
                        @Override
                        public void onResponse(Call<List<DocumentoDTO>> call, Response<List<DocumentoDTO>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                for (DocumentoDTO doc : response.body()) {
                                    items.add(new DocumentoSupervisorAdapter.DocumentoItem(sol, doc));
                                }
                            }
                            pendientes[0]--;
                            if (pendientes[0] == 0) mostrarResultados();
                        }

                        @Override
                        public void onFailure(Call<List<DocumentoDTO>> call, Throwable t) {
                            pendientes[0]--;
                            if (pendientes[0] == 0) mostrarResultados();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<SolicitudesResponse> call, Throwable t) {
                mostrarError("Error de conexión: " + t.getMessage());
            }
        });
    }

    private void mostrarResultados() {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            if (items.isEmpty()) {
                tvNoData.setVisibility(View.VISIBLE);
                tvNoData.setText("Sin documentos subidos aún");
            } else {
                tvNoData.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void mostrarError(String msg) {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });
    }

    private void mostrarDialogo(DocumentoSupervisorAdapter.DocumentoItem item, boolean aprobar) {
        String accion = aprobar ? "aprobar" : "rechazar";
        new AlertDialog.Builder(this)
                .setTitle("Confirmar")
                .setMessage("¿Deseas " + accion + " el Periodo " + item.doc.getPeriodo()
                        + " de " + item.solicitud.getNombreEstudiante() + "?")
                .setPositiveButton("Sí", (d, w) -> procesarDocumento(item, aprobar))
                .setNegativeButton("No", null)
                .show();
    }

    private void procesarDocumento(DocumentoSupervisorAdapter.DocumentoItem item, boolean aprobar) {
        String estado = aprobar ? "aprobado" : "rechazado";
        int boleta    = item.solicitud.getBoleta();
        int periodo   = item.doc.getPeriodo();

        apiService.actualizarEstadoDocumento(boleta, periodo, estado)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(SupervisorDocumentos.this,
                                    "Periodo " + periodo + (aprobar ? " aprobado ✓" : " rechazado"),
                                    Toast.LENGTH_SHORT).show();
                            // Notificar al alumno
                            enviarNotificacion(item, aprobar);
                            cargarDocumentos();
                        } else {
                            Toast.makeText(SupervisorDocumentos.this,
                                    "Error al actualizar estado", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(SupervisorDocumentos.this,
                                "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void enviarNotificacion(DocumentoSupervisorAdapter.DocumentoItem item, boolean aprobar) {
        // Buscar idUsuario del alumno por boleta
        int boleta = item.solicitud.getBoleta();
        int periodo = item.doc.getPeriodo();

        // Usamos la boleta para obtener el idUsuario via endpoint
        apiService.getIdUsuarioPorBoleta(boleta).enqueue(new Callback<Map<String, Integer>>() {
            @Override
            public void onResponse(Call<Map<String, Integer>> call, Response<Map<String, Integer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Integer idUsuario = response.body().get("idUsuario");
                    if (idUsuario == null) return;

                    Map<String, String> body = new HashMap<>();
                    body.put("idUsuario", String.valueOf(idUsuario));
                    body.put("titulo", aprobar
                            ? "Periodo " + periodo + " aprobado ✓"
                            : "Periodo " + periodo + " rechazado");
                    body.put("mensaje", aprobar
                            ? "Tu reporte del Periodo " + periodo + " fue aprobado. ¡Sigue así!"
                            : "Tu reporte del Periodo " + periodo + " fue rechazado. Por favor súbelo nuevamente.");

                    apiService.crearNotificacion(body).enqueue(new Callback<>() {
                        @Override public void onResponse(Call call, Response r) {}
                        @Override public void onFailure(Call call, Throwable t) {}
                    });
                }
            }
            @Override public void onFailure(Call<Map<String, Integer>> call, Throwable t) {}
        });
    }
}
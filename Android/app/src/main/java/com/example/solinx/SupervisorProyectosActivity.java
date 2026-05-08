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

import com.example.solinx.ADAPTER.ProyectoSupervisorAdapter;
import com.example.solinx.API.ApiClient;
import com.example.solinx.API.ApiService;
import com.example.solinx.RESPONSE.ProyectoResponse;
import com.example.solinx.UTIL.ThemeUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupervisorProyectosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProyectoSupervisorAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvNoData;
    private ApiService apiService;
    private final List<ProyectoResponse> lista = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_proyectos);

        apiService   = ApiClient.getClient().create(ApiService.class);
        recyclerView = findViewById(R.id.recycler_proyectos);
        progressBar  = findViewById(R.id.progress_bar);
        tvNoData     = findViewById(R.id.tv_no_data);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProyectoSupervisorAdapter(lista, new ProyectoSupervisorAdapter.OnClickListener() {
            @Override public void onAprobar(ProyectoResponse p)  { mostrarDialogo(p, true);  }
            @Override public void onRechazar(ProyectoResponse p) { mostrarDialogo(p, false); }
        });
        recyclerView.setAdapter(adapter);

        ImageButton btnRegresar = findViewById(R.id.btnRegresarFlecha);
        if (btnRegresar != null) btnRegresar.setOnClickListener(v -> finish());

        cargarProyectos();
    }

    private void cargarProyectos() {
        progressBar.setVisibility(View.VISIBLE);
        tvNoData.setVisibility(View.GONE);

        // GET /proyecto — trae todos (sin filtro soloAprobados)
        apiService.obtenerProyectos().enqueue(new Callback<List<ProyectoResponse>>() {
            @Override
            public void onResponse(Call<List<ProyectoResponse>> call, Response<List<ProyectoResponse>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    lista.clear();
                    lista.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    if (lista.isEmpty()) {
                        tvNoData.setVisibility(View.VISIBLE);
                        tvNoData.setText("Sin proyectos registrados");
                    }
                } else {
                    Toast.makeText(SupervisorProyectosActivity.this,
                            "Error al cargar proyectos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ProyectoResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SupervisorProyectosActivity.this,
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogo(ProyectoResponse proyecto, boolean aprobar) {
        String accion = aprobar ? "aprobar" : "rechazar";
        new AlertDialog.Builder(this)
                .setTitle("Confirmar")
                .setMessage("¿Deseas " + accion + " el proyecto \"" + proyecto.getNombreProyecto() + "\"?")
                .setPositiveButton("Sí", (d, w) -> procesarProyecto(proyecto, aprobar))
                .setNegativeButton("No", null)
                .show();
    }

    private void procesarProyecto(ProyectoResponse proyecto, boolean aprobar) {
        String estado = aprobar ? "aprobado" : "rechazado";
        apiService.actualizarEstadoProyecto(proyecto.getIdProyecto(), estado)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(SupervisorProyectosActivity.this,
                                    aprobar ? "Proyecto aprobado ✓" : "Proyecto rechazado",
                                    Toast.LENGTH_SHORT).show();
                            cargarProyectos();
                        } else {
                            Toast.makeText(SupervisorProyectosActivity.this,
                                    "Error al actualizar", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(SupervisorProyectosActivity.this,
                                "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
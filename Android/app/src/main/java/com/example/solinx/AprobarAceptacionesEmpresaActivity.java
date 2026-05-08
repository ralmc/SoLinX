package com.example.solinx;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solinx.ADAPTER.AceptacionesEmpresaAdapter;
import com.example.solinx.API.ApiClient;
import com.example.solinx.API.ApiService;
import com.example.solinx.RESPONSE.SolicitudesResponse;
import com.example.solinx.UTIL.ThemeUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AprobarAceptacionesEmpresaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AceptacionesEmpresaAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvNoData;
    private ApiService apiService;
    private int idEmpresa;
    private final List<Solicitudes> lista = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_aprobar_aceptaciones_empresa);

        idEmpresa = getIntent().getIntExtra("idEmpresa", -1);
        if (idEmpresa == -1) {
            Toast.makeText(this, "Error: ID Empresa no válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiService   = ApiClient.getClient().create(ApiService.class);
        recyclerView = findViewById(R.id.recycler_aceptaciones);
        progressBar  = findViewById(R.id.progress_bar);
        tvNoData     = findViewById(R.id.tv_no_data);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AceptacionesEmpresaAdapter(this, lista);
        recyclerView.setAdapter(adapter);

        ImageButton btnRegresar = findViewById(R.id.btnRegresarFlecha);
        if (btnRegresar != null) btnRegresar.setOnClickListener(v -> finish());

        cargarAceptaciones();
    }

    private void cargarAceptaciones() {
        progressBar.setVisibility(View.VISIBLE);
        tvNoData.setVisibility(View.GONE);

        apiService.getSolicitudesAceptadas(idEmpresa).enqueue(new Callback<SolicitudesResponse>() {
            @Override
            public void onResponse(Call<SolicitudesResponse> call, Response<SolicitudesResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    lista.clear();
                    if (response.body().getSolicitudes() != null)
                        lista.addAll(response.body().getSolicitudes());
                    adapter.notifyDataSetChanged();
                    if (lista.isEmpty()) {
                        tvNoData.setVisibility(View.VISIBLE);
                        tvNoData.setText("Sin aceptaciones aún");
                    }
                } else {
                    Toast.makeText(AprobarAceptacionesEmpresaActivity.this,
                            "Error al cargar aceptaciones", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SolicitudesResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AprobarAceptacionesEmpresaActivity.this,
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
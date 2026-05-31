package com.example.solinx;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solinx.API.ApiClient;
import com.example.solinx.API.ApiService;
import com.example.solinx.DTO.CambioPerfilDTO;
import com.example.solinx.DTO.NotificacionDTO;
import com.example.solinx.UTIL.ThemeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupervisorCambiosPerfil extends AppCompatActivity {

    private RecyclerView recyclerCambios;
    private ProgressBar progressBar;
    private TextView tvNoData;
    private ApiService apiService;
    private final List<CambioPerfilDTO> lista = new ArrayList<>();
    private CambiosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_cambios_perfil);

        apiService     = ApiClient.getClient().create(ApiService.class);
        recyclerCambios = findViewById(R.id.recyclerCambios);
        progressBar    = findViewById(R.id.progressBar);
        tvNoData       = findViewById(R.id.tvNoData);

        recyclerCambios.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CambiosAdapter();
        recyclerCambios.setAdapter(adapter);

        ImageButton btnRegresar = findViewById(R.id.btnRegresarFlecha);
        if (btnRegresar != null) btnRegresar.setOnClickListener(v -> finish());

        cargarCambios();
    }

    // ─── Cargar cambios pendientes ────────────────────────────────────────────
    private void cargarCambios() {
        progressBar.setVisibility(View.VISIBLE);
        tvNoData.setVisibility(View.GONE);

        apiService.getCambiosPendientes().enqueue(new Callback<List<CambioPerfilDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<CambioPerfilDTO>> call,
                                   @NonNull Response<List<CambioPerfilDTO>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    lista.clear();
                    for (CambioPerfilDTO c : response.body()) {
                        if ("pendiente".equalsIgnoreCase(c.getEstado())) lista.add(c);
                    }
                    adapter.notifyDataSetChanged();
                    if (lista.isEmpty()) tvNoData.setVisibility(View.VISIBLE);
                } else if (response.code() == 204) {
                    lista.clear();
                    adapter.notifyDataSetChanged();
                    tvNoData.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(SupervisorCambiosPerfil.this,
                            "Error al cargar cambios", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<CambioPerfilDTO>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SupervisorCambiosPerfil.this,
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ─── Resolver cambio ──────────────────────────────────────────────────────
    private void mostrarDialogo(CambioPerfilDTO cambio, boolean aprobar) {
        String campoLabel = obtenerLabelCampo(cambio.getCampo());
        String accion = aprobar ? "aprobar" : "rechazar";

        new AlertDialog.Builder(this)
                .setTitle("Confirmar")
                .setMessage("¿Deseas " + accion + " el cambio de " + campoLabel
                        + " de " + cambio.getNombreUsuario() + "?")
                .setPositiveButton("Sí", (d, w) -> resolverCambio(cambio, aprobar))
                .setNegativeButton("No", null)
                .show();
    }

    private void resolverCambio(CambioPerfilDTO cambio, boolean aprobar) {
        progressBar.setVisibility(View.VISIBLE);

        Call<CambioPerfilDTO> call = aprobar
                ? apiService.aprobarCambio(cambio.getIdCambio())
                : apiService.rechazarCambio(cambio.getIdCambio());

        call.enqueue(new Callback<CambioPerfilDTO>() {
            @Override
            public void onResponse(@NonNull Call<CambioPerfilDTO> call,
                                   @NonNull Response<CambioPerfilDTO> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    String campoLabel = obtenerLabelCampo(cambio.getCampo());
                    Toast.makeText(SupervisorCambiosPerfil.this,
                            aprobar ? "Cambio aprobado ✓" : "Cambio rechazado",
                            Toast.LENGTH_SHORT).show();
                    // Notificar al usuario
                    enviarNotificacion(cambio, aprobar, campoLabel);
                    cargarCambios();
                } else {
                    Toast.makeText(SupervisorCambiosPerfil.this,
                            "Error al procesar", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<CambioPerfilDTO> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SupervisorCambiosPerfil.this,
                        "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enviarNotificacion(CambioPerfilDTO cambio, boolean aprobado, String campoLabel) {
        if (cambio.getIdUsuario() == null) return;
        Map<String, String> body = new HashMap<>();
        body.put("idUsuario", String.valueOf(cambio.getIdUsuario()));
        body.put("titulo",    aprobado
                ? "Cambio de " + campoLabel + " aprobado ✓"
                : "Cambio de " + campoLabel + " rechazado");
        body.put("mensaje",   aprobado
                ? "Tu solicitud de cambio de " + campoLabel + " fue aprobada y ya está aplicada en tu perfil."
                : "Tu solicitud de cambio de " + campoLabel + " fue rechazada por el supervisor.");

        apiService.crearNotificacion(body).enqueue(new Callback<NotificacionDTO>() {
            @Override public void onResponse(@NonNull Call<NotificacionDTO> call,
                                             @NonNull Response<NotificacionDTO> response) {}
            @Override public void onFailure(@NonNull Call<NotificacionDTO> call,
                                            @NonNull Throwable t) {}
        });
    }

    private String obtenerLabelCampo(String campo) {
        if (campo == null) return "dato";
        switch (campo) {
            case "nombre":       return "Nombre";
            case "carrera":      return "Carrera";
            case "escuela":      return "Escuela";
            case "nombreEmpresa": return "Nombre empresa";
            case "telefono":     return "Teléfono";
            default:             return campo;
        }
    }

    // ─── Adapter ──────────────────────────────────────────────────────────────
    private class CambiosAdapter extends RecyclerView.Adapter<CambiosAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_cambio_perfil, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder h, int position) {
            CambioPerfilDTO c = lista.get(position);

            h.tvNombreUsuario.setText(c.getNombreUsuario() != null ? c.getNombreUsuario() : "N/A");
            h.tvRol.setText("empresa".equalsIgnoreCase(c.getRol()) ? "Empresa" : "Alumno");
            h.tvCampo.setText(obtenerLabelCampo(c.getCampo()));
            h.tvValorAnterior.setText(c.getValorAnterior() != null ? c.getValorAnterior() : "—");
            h.tvValorNuevo.setText(c.getValorNuevo() != null ? c.getValorNuevo() : "—");

            h.btnAprobar.setOnClickListener(v -> mostrarDialogo(c, true));
            h.btnRechazar.setOnClickListener(v -> mostrarDialogo(c, false));
        }

        @Override
        public int getItemCount() { return lista.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvNombreUsuario, tvRol, tvCampo, tvValorAnterior, tvValorNuevo;
            TextView btnAprobar, btnRechazar;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNombreUsuario = itemView.findViewById(R.id.tvNombreUsuario);
                tvRol           = itemView.findViewById(R.id.tvRol);
                tvCampo         = itemView.findViewById(R.id.tvCampo);
                tvValorAnterior = itemView.findViewById(R.id.tvValorAnterior);
                tvValorNuevo    = itemView.findViewById(R.id.tvValorNuevo);
                btnAprobar      = itemView.findViewById(R.id.btnAprobar);
                btnRechazar     = itemView.findViewById(R.id.btnRechazar);
            }
        }
    }
}
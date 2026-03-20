package com.example.solinx;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solinx.ADAPTER.ProyectoAdapter;
import com.example.solinx.API.ApiClient;
import com.example.solinx.API.ApiService;
import com.example.solinx.RESPONSE.ProyectoResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlumnoEmpresas extends Fragment {

    private static final String TAG = "AlumnoEmpresas";

    private RecyclerView recyclerViewProyectos;
    private TextView txtNoProyectos;
    private ProgressBar progressBar;
    private ProyectoAdapter proyectoAdapter;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alumno_empresas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewProyectos = view.findViewById(R.id.recyclerViewProyectos);
        txtNoProyectos        = view.findViewById(R.id.txtNoProyectos);
        progressBar           = view.findViewById(R.id.progressBar);

        apiService = ApiClient.getClient().create(ApiService.class);

        recyclerViewProyectos.setLayoutManager(new LinearLayoutManager(requireContext()));
        proyectoAdapter = new ProyectoAdapter(requireContext());
        recyclerViewProyectos.setAdapter(proyectoAdapter);

        cargarProyectos();
    }

    public void cargarProyectos() {
        mostrarCargando(true);

        apiService.obtenerProyectos().enqueue(new Callback<List<ProyectoResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProyectoResponse>> call,
                                   @NonNull Response<List<ProyectoResponse>> response) {
                mostrarCargando(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<ProyectoResponse> proyectos = response.body();
                    if (proyectos.isEmpty()) {
                        mostrarMensajeVacio(true);
                    } else {
                        mostrarMensajeVacio(false);
                        proyectoAdapter.setProyectos(proyectos);
                    }
                } else {
                    mostrarError("Error al cargar proyectos: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProyectoResponse>> call, @NonNull Throwable t) {
                mostrarCargando(false);
                mostrarError("Error de conexión: " + t.getMessage());
            }
        });
    }

    private void mostrarCargando(boolean mostrar) {
        progressBar.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        if (mostrar) {
            recyclerViewProyectos.setVisibility(View.GONE);
            txtNoProyectos.setVisibility(View.GONE);
        }
    }

    private void mostrarMensajeVacio(boolean mostrar) {
        txtNoProyectos.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        recyclerViewProyectos.setVisibility(mostrar ? View.GONE : View.VISIBLE);
    }

    private void mostrarError(String mensaje) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show();
        txtNoProyectos.setText("Error al cargar proyectos.\nVerifica tu conexión.");
        txtNoProyectos.setVisibility(View.VISIBLE);
        recyclerViewProyectos.setVisibility(View.GONE);
    }
}
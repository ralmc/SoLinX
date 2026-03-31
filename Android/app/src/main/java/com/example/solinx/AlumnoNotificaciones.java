package com.example.solinx;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.example.solinx.ADAPTER.NotificacionAdapter;
import com.example.solinx.API.ApiClient;
import com.example.solinx.API.ApiService;
import com.example.solinx.DTO.NotificacionDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlumnoNotificaciones extends Fragment {

    private RecyclerView recyclerNotificaciones;
    private TextView txtSinNotificaciones, btnMarcarTodas;
    private ProgressBar progressBar;
    private NotificacionAdapter adapter;
    private ApiService apiService;
    private int idUsuario;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alumno_notificaciones, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerNotificaciones  = view.findViewById(R.id.recyclerNotificaciones);
        txtSinNotificaciones    = view.findViewById(R.id.txtSinNotificaciones);
        progressBar             = view.findViewById(R.id.progressBar);
        btnMarcarTodas          = view.findViewById(R.id.btnMarcarTodas);

        apiService = ApiClient.getClient().create(ApiService.class);

        SharedPreferences prefs = requireContext().getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        idUsuario = prefs.getInt("idUsuario", -1);

        recyclerNotificaciones.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new NotificacionAdapter(requireContext(), notificacion -> {
            if (Boolean.FALSE.equals(notificacion.getLeida())) {
                marcarComoLeida(notificacion.getIdNotificacion());
            }
        });
        recyclerNotificaciones.setAdapter(adapter);

        btnMarcarTodas.setOnClickListener(v -> marcarTodasComoLeidas());

        cargarNotificaciones();
    }

    private void cargarNotificaciones() {
        mostrarCargando(true);
        apiService.obtenerNotificaciones(idUsuario).enqueue(new Callback<List<NotificacionDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<NotificacionDTO>> call,
                                   @NonNull Response<List<NotificacionDTO>> response) {
                mostrarCargando(false);
                // 204 = sin notificaciones, no es error
                if (response.code() == 204 ||
                        (response.isSuccessful() && (response.body() == null || response.body().isEmpty()))) {
                    txtSinNotificaciones.setVisibility(View.VISIBLE);
                    recyclerNotificaciones.setVisibility(View.GONE);
                } else if (response.isSuccessful() && response.body() != null) {
                    txtSinNotificaciones.setVisibility(View.GONE);
                    recyclerNotificaciones.setVisibility(View.VISIBLE);
                    adapter.setNotificaciones(response.body());
                } else {
                    mostrarError("Error al cargar notificaciones: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<NotificacionDTO>> call, @NonNull Throwable t) {
                mostrarCargando(false);
                mostrarError("Error de conexión: " + t.getMessage());
            }
        });
    }

    private void marcarComoLeida(int idNotificacion) {
        apiService.marcarComoLeida(idNotificacion).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) cargarNotificaciones();
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Error al marcar notificación", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void marcarTodasComoLeidas() {
        apiService.marcarTodasComoLeidas(idUsuario).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) cargarNotificaciones();
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Error al actualizar notificaciones", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarCargando(boolean mostrar) {
        progressBar.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        if (mostrar) {
            recyclerNotificaciones.setVisibility(View.GONE);
            txtSinNotificaciones.setVisibility(View.GONE);
        }
    }

    private void mostrarError(String mensaje) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show();
        txtSinNotificaciones.setText("Error al cargar notificaciones.\nVerifica tu conexión.");
        txtSinNotificaciones.setVisibility(View.VISIBLE);
        recyclerNotificaciones.setVisibility(View.GONE);
    }
}

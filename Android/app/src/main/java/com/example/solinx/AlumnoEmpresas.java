package com.example.solinx;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.solinx.DTO.SolicitudDTO;
import com.example.solinx.RESPONSE.ProyectoAlumnoResponse;
import com.example.solinx.RESPONSE.ProyectoResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlumnoEmpresas extends Fragment {

    private static final String TAG = "AlumnoEmpresas";

    private LinearLayout layoutListaProyectos;
    private RecyclerView recyclerViewProyectos;
    private TextView txtNoProyectos;
    private ProgressBar progressBar;
    private ProyectoAdapter proyectoAdapter;

    private FrameLayout containerProyectoAsignado;
    private ImageView imgProyectoAsignado;
    private TextView tvNombreProyectoAsignado, tvEmpresaAsignado, tvCarreraAsignado,
            tvUbicacionAsignado, tvFechaInicioAsignado, tvFechaFinAsignado,
            tvTelefonoAsignado, tvObjetivoAsignado;
    private Button btnContactarEmpresa;

    private ApiService apiService;
    private String correoEmpresaAsignada;
    private String nombreProyectoAsignadoStr;

    private Map<Integer, String> estadosSolicitudes = new HashMap<>();
    private boolean alumnoAceptado = false;
    private Integer idProyectoAceptado = null;

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

        layoutListaProyectos  = view.findViewById(R.id.layoutListaProyectos);
        recyclerViewProyectos = view.findViewById(R.id.recyclerViewProyectos);
        txtNoProyectos        = view.findViewById(R.id.txtNoProyectos);
        progressBar           = view.findViewById(R.id.progressBar);

        containerProyectoAsignado = view.findViewById(R.id.containerProyectoAsignado);
        imgProyectoAsignado       = view.findViewById(R.id.imgProyectoAsignado);
        tvNombreProyectoAsignado  = view.findViewById(R.id.tvNombreProyectoAsignado);
        tvEmpresaAsignado         = view.findViewById(R.id.tvEmpresaAsignado);
        tvCarreraAsignado         = view.findViewById(R.id.tvCarreraAsignado);
        tvUbicacionAsignado       = view.findViewById(R.id.tvUbicacionAsignado);
        tvFechaInicioAsignado     = view.findViewById(R.id.tvFechaInicioAsignado);
        tvFechaFinAsignado        = view.findViewById(R.id.tvFechaFinAsignado);
        tvTelefonoAsignado        = view.findViewById(R.id.tvTelefonoAsignado);
        tvObjetivoAsignado        = view.findViewById(R.id.tvObjetivoAsignado);
        btnContactarEmpresa       = view.findViewById(R.id.btnContactarEmpresa);

        apiService = ApiClient.getClient().create(ApiService.class);

        recyclerViewProyectos.setLayoutManager(new LinearLayoutManager(requireContext()));
        proyectoAdapter = new ProyectoAdapter(requireContext());
        recyclerViewProyectos.setAdapter(proyectoAdapter);

        btnContactarEmpresa.setOnClickListener(v -> contactarEmpresa());

        cargarSolicitudesYProyectos();
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarSolicitudesYProyectos();
    }

    private void cargarSolicitudesYProyectos() {
        int boleta = obtenerBoletaDelAlumno();
        if (boleta == -1) {
            mostrarError("No se pudo obtener la boleta del alumno");
            return;
        }

        mostrarCargando(true);

        apiService.obtenerSolicitudesEstudiante(boleta).enqueue(new Callback<List<SolicitudDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<SolicitudDTO>> call,
                                   @NonNull Response<List<SolicitudDTO>> response) {

                estadosSolicitudes.clear();
                alumnoAceptado = false;

                if (response.isSuccessful() && response.body() != null) {
                    for (SolicitudDTO s : response.body()) {
                        if (s.getIdProyecto() != null && s.getEstadoSolicitud() != null) {
                            estadosSolicitudes.put(s.getIdProyecto(), s.getEstadoSolicitud());
                        }
                        if ("aceptada".equalsIgnoreCase(s.getEstadoSolicitud())) {
                            alumnoAceptado = true;
                            idProyectoAceptado = s.getIdProyecto();
                        }
                    }
                }
                cargarProyectos(boleta);
            }

            @Override
            public void onFailure(@NonNull Call<List<SolicitudDTO>> call, @NonNull Throwable t) {
                cargarProyectos(boleta);
            }
        });
    }

    public void cargarProyectos(int boleta) {
        apiService.obtenerProyectosParaAlumno(boleta).enqueue(new Callback<ProyectoAlumnoResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProyectoAlumnoResponse> call,
                                   @NonNull Response<ProyectoAlumnoResponse> response) {
                mostrarCargando(false);

                if (response.isSuccessful() && response.body() != null) {
                    ProyectoAlumnoResponse r = response.body();

                    // LOG TEMPORAL
                    android.util.Log.d("PROYECTOS", "Total proyectos recibidos: " +
                            (r.getProyectos() != null ? r.getProyectos().size() : "null"));
                    if (r.getProyectos() != null) {
                        for (ProyectoResponse p : r.getProyectos()) {
                            android.util.Log.d("PROYECTOS", "  → id: " + p.getIdProyecto() + " | " + p.getNombreProyecto());
                        }
                    }

                    mostrarVistaLista(r.getProyectos());
                } else {
                    mostrarError("Error al cargar proyectos: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProyectoAlumnoResponse> call, @NonNull Throwable t) {
                mostrarCargando(false);
                mostrarError("Error de conexión: " + t.getMessage());
            }
        });
    }

    public void cargarProyectos() {
        int boleta = obtenerBoletaDelAlumno();
        if (boleta != -1) cargarProyectos(boleta);
    }

    private int obtenerBoletaDelAlumno() {
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("SoLinXPrefs", android.content.Context.MODE_PRIVATE);
        String boletaStr = prefs.getString("boleta", null);
        if (boletaStr == null) return -1;
        try { return Integer.parseInt(boletaStr); }
        catch (NumberFormatException e) { return -1; }
    }

    private void mostrarVistaLista(List<ProyectoResponse> proyectos) {
        containerProyectoAsignado.setVisibility(View.GONE);
        layoutListaProyectos.setVisibility(View.VISIBLE);

        if (proyectos == null || proyectos.isEmpty()) {
            txtNoProyectos.setVisibility(View.VISIBLE);
            recyclerViewProyectos.setVisibility(View.GONE);
        } else {
            txtNoProyectos.setVisibility(View.GONE);
            recyclerViewProyectos.setVisibility(View.VISIBLE);
            proyectoAdapter.setEstadosSolicitudes(estadosSolicitudes, alumnoAceptado, idProyectoAceptado);
            proyectoAdapter.setProyectos(proyectos);
        }
    }

    private void mostrarVistaProyectoAsignado(ProyectoResponse p, String correoEmpresa) {
        layoutListaProyectos.setVisibility(View.GONE);
        containerProyectoAsignado.setVisibility(View.VISIBLE);

        this.correoEmpresaAsignada = correoEmpresa;
        this.nombreProyectoAsignadoStr = p.getNombreProyecto();

        tvNombreProyectoAsignado.setText(p.getNombreProyecto() != null ? p.getNombreProyecto() : "Sin nombre");
        tvEmpresaAsignado.setText("Empresa: " + (p.getNombreEmpresa() != null ? p.getNombreEmpresa() : "N/A"));
        tvCarreraAsignado.setText("Carrera enfocada: " + (p.getCarreraEnfocada() != null ? p.getCarreraEnfocada() : "N/A"));
        tvUbicacionAsignado.setText("Ubicación: " + (p.getUbicacion() != null ? p.getUbicacion() : "N/A"));

        String fechaI = (p.getFechaInicio() != null && p.getFechaInicio().length() >= 10)
                ? p.getFechaInicio().substring(0, 10) : "N/A";
        String fechaF = (p.getFechaTermino() != null && p.getFechaTermino().length() >= 10)
                ? p.getFechaTermino().substring(0, 10) : "Sin fecha";

        tvFechaInicioAsignado.setText("Fecha de inicio: " + fechaI);
        tvFechaFinAsignado.setText("Fecha de término: " + fechaF);
        tvTelefonoAsignado.setText("Teléfono: " + (p.getTelefonoEmpresa() != null ? p.getTelefonoEmpresa() : "No disponible"));
        tvObjetivoAsignado.setText(p.getObjetivo() != null ? p.getObjetivo() : "Sin descripción");

        if (p.getImagenProyecto() != null && !p.getImagenProyecto().isEmpty()) {
            try {
                byte[] bytes = Base64.decode(p.getImagenProyecto(), Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (bmp != null) imgProyectoAsignado.setImageBitmap(bmp);
                else imgProyectoAsignado.setImageResource(R.drawable.img_default_proyecto);
            } catch (Exception e) {
                imgProyectoAsignado.setImageResource(R.drawable.img_default_proyecto);
            }
        } else {
            imgProyectoAsignado.setImageResource(R.drawable.img_default_proyecto);
        }
    }

    private void contactarEmpresa() {
        if (correoEmpresaAsignada == null || correoEmpresaAsignada.isEmpty()) {
            Toast.makeText(requireContext(), "Correo de la empresa no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Contactar Empresa");

        final EditText input = new EditText(requireContext());
        input.setHint("Escribe tu mensaje...");
        input.setMinLines(3);
        input.setPadding(40, 20, 40, 20);
        builder.setView(input);

        builder.setPositiveButton("Enviar", (dialog, which) -> {
            String mensaje = input.getText().toString().trim();
            if (mensaje.isEmpty()) {
                Toast.makeText(requireContext(), "Escribe un mensaje", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = requireActivity()
                    .getSharedPreferences("SoLinXPrefs", android.content.Context.MODE_PRIVATE);
            String nombreAlumno  = prefs.getString("nombre", "N/A");
            String boletaAlumno  = prefs.getString("boleta", "N/A");
            String carreraAlumno = prefs.getString("carrera", "N/A");
            String escuelaAlumno = prefs.getString("escuela", "N/A");

            String cuerpo = "Alumno: " + nombreAlumno + "\n"
                    + "Boleta: " + boletaAlumno + "\n"
                    + "Carrera: " + carreraAlumno + "\n"
                    + "Escuela: " + escuelaAlumno + "\n"
                    + "Proyecto: " + (nombreProyectoAsignadoStr != null ? nombreProyectoAsignadoStr : "") + "\n\n"
                    + "Mensaje:\n" + mensaje;

            String subject = "SoLinX - Alumno: Consulta sobre proyecto " +
                    (nombreProyectoAsignadoStr != null ? nombreProyectoAsignadoStr : "");

            String mailtoUri = "mailto:" + correoEmpresaAsignada
                    + "?subject=" + Uri.encode(subject)
                    + "&body=" + Uri.encode(cuerpo);

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse(mailtoUri));

            try {
                startActivity(Intent.createChooser(emailIntent, "Enviar correo con..."));
            } catch (Exception e) {
                Toast.makeText(requireContext(), "No hay app de correo disponible", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void mostrarCargando(boolean mostrar) {
        if (progressBar != null)
            progressBar.setVisibility(mostrar ? View.VISIBLE : View.GONE);
    }

    private void mostrarError(String mensaje) {
        if (getContext() != null)
            Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show();
    }
}
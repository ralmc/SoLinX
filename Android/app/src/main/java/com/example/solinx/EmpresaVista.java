package com.example.solinx;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.solinx.API.ApiClient;
import com.example.solinx.API.ApiService;
import com.example.solinx.DTO.PerfilDTO;
import com.example.solinx.RESPONSE.ProyectoResponse;
import com.example.solinx.UTIL.ThemeUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmpresaVista extends AppCompatActivity implements View.OnClickListener {

    LinearLayout contenedorProyectos;
    TextView btnAñadir, tvMensajeVacio, btnotificaciones;
    ImageView logoEmpresa, imgPerfilEmpresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_vista_menu);

        gestionarSesion();
        inicializarVistas();
        cargarDatosDelBackend();
        cargarFotoPerfil();
    }

    private void gestionarSesion() {
        SharedPreferences prefs = getSharedPreferences("sesion_usuario", MODE_PRIVATE);

        if (getIntent().hasExtra("ID_EMPRESA_ACTUAL")) {
            int idRecibido = getIntent().getIntExtra("ID_EMPRESA_ACTUAL", -1);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("id_empresa_activa", idRecibido);
            editor.commit();
        }
    }

    private int obtenerIdEmpresaActual() {
        SharedPreferences prefs = getSharedPreferences("sesion_usuario", MODE_PRIVATE);
        return prefs.getInt("id_empresa_activa", -1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatosDelBackend();
        cargarFotoPerfil();
    }

    private void inicializarVistas() {
        contenedorProyectos = findViewById(R.id.contenedorProyectos);
        tvMensajeVacio = findViewById(R.id.tvMensajeVacio);
        btnAñadir = findViewById(R.id.btnAñadir);
        logoEmpresa = findViewById(R.id.logoEmpresa);
        btnotificaciones = findViewById(R.id.notificaciones);
        imgPerfilEmpresa = findViewById(R.id.imgPerfilEmpresa);

        btnAñadir.setOnClickListener(this);
        logoEmpresa.setOnClickListener(this);
        btnotificaciones.setOnClickListener(this);
        if (imgPerfilEmpresa != null) {
            imgPerfilEmpresa.setOnClickListener(this);
        }
    }

    private void cargarFotoPerfil() {
        if (imgPerfilEmpresa == null) return;

        int idUsuario = getSharedPreferences("sesion_usuario", MODE_PRIVATE)
                .getInt("idUsuario", -1);
        if (idUsuario == -1) return;

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.obtenerPerfil(idUsuario).enqueue(new Callback<PerfilDTO>() {
            @Override
            public void onResponse(Call<PerfilDTO> call, Response<PerfilDTO> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getFoto() != null
                        && !response.body().getFoto().isEmpty()) {
                    String b64 = response.body().getFoto();
                    byte[] bytes = Base64.decode(b64, Base64.DEFAULT);
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    runOnUiThread(() -> imgPerfilEmpresa.setImageBitmap(bmp));
                }
            }

            @Override
            public void onFailure(Call<PerfilDTO> call, Throwable t) {
                // dejar el icono default
            }
        });
    }

    private void cargarDatosDelBackend() {
        int idActual = obtenerIdEmpresaActual();

        if (idActual == -1) {
            Toast.makeText(this, "Error de sesión. Vuelve a iniciar sesión.", Toast.LENGTH_LONG).show();
            tvMensajeVacio.setVisibility(View.VISIBLE);
            tvMensajeVacio.setText("Error: No se pudo cargar la sesión");
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.obtenerProyectosPorEmpresa(idActual).enqueue(new Callback<List<ProyectoResponse>>() {
            @Override
            public void onResponse(Call<List<ProyectoResponse>> call, Response<List<ProyectoResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    renderizarProyectos(response.body());
                } else {
                    renderizarProyectos(java.util.Collections.emptyList());
                }
            }

            @Override
            public void onFailure(Call<List<ProyectoResponse>> call, Throwable t) {
                Toast.makeText(EmpresaVista.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                tvMensajeVacio.setVisibility(View.VISIBLE);
                tvMensajeVacio.setText("Error de conexión. Intenta de nuevo.");
            }
        });
    }

    private void renderizarProyectos(List<ProyectoResponse> lista) {
        contenedorProyectos.removeAllViews();

        if (lista.isEmpty()) {
            tvMensajeVacio.setVisibility(View.VISIBLE);
            return;
        }
        tvMensajeVacio.setVisibility(View.GONE);

        int contadorVisual = 1;

        for (ProyectoResponse proyecto : lista) {

            View tarjeta = LayoutInflater.from(this).inflate(R.layout.activity_item_proyecto_card, contenedorProyectos, false);

            TextView tvId = tarjeta.findViewById(R.id.tvIdProyecto);
            TextView tvCarrera = tarjeta.findViewById(R.id.tvCarreraEnfocada);
            TextView tvNombre = tarjeta.findViewById(R.id.tvNombreProyecto);
            TextView tvEmpresa = tarjeta.findViewById(R.id.tvNombreEmpresa);
            TextView tvObjetivo = tarjeta.findViewById(R.id.tvObjetivo);
            TextView tvVacantes = tarjeta.findViewById(R.id.tvVacantes);
            TextView tvUbicacion = tarjeta.findViewById(R.id.tvUbicacion);
            TextView tvInicio = tarjeta.findViewById(R.id.tvFechaInicio);
            TextView tvFin = tarjeta.findViewById(R.id.tvFechaFin);
            ImageView img = tarjeta.findViewById(R.id.imgProyecto);
            ImageView btnEdit = tarjeta.findViewById(R.id.btnEditar);
            TextView btnEliminar = tarjeta.findViewById(R.id.tvEliminar);

            tvId.setText("# " + contadorVisual);
            contadorVisual++;

            tvCarrera.setText("Carrera: " + proyecto.getCarreraEnfocada());
            tvNombre.setText("Proyecto: " + proyecto.getNombreProyecto());
            tvEmpresa.setText("Empresa: " + (proyecto.getNombreEmpresa() != null ? proyecto.getNombreEmpresa() : "TechNova"));
            tvObjetivo.setText("Obj: " + proyecto.getObjetivo());
            tvVacantes.setText("Vacantes: " + proyecto.getVacantes());
            tvUbicacion.setText("Ubic: " + proyecto.getUbicacion());

            String fechaI = (proyecto.getFechaInicio() != null && proyecto.getFechaInicio().length()>=10)
                    ? proyecto.getFechaInicio().substring(0,10) : "N/A";
            tvInicio.setText("Inicio: " + fechaI);

            String fechaF = (proyecto.getFechaTermino() != null && proyecto.getFechaTermino().length()>=10)
                    ? proyecto.getFechaTermino().substring(0,10) : "---";
            tvFin.setText("Fin: " + fechaF);

            // Pintar imagen Base64 si existe, si no usar drawable local
            if (proyecto.getImagenProyecto() != null && !proyecto.getImagenProyecto().isEmpty()) {
                try {
                    byte[] bytes = Base64.decode(proyecto.getImagenProyecto(), Base64.DEFAULT);
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    if (bmp != null) {
                        img.setImageBitmap(bmp);
                    } else {
                        img.setImageResource(obtenerIdImagen(this, proyecto.getImagenRef()));
                    }
                } catch (Exception e) {
                    img.setImageResource(obtenerIdImagen(this, proyecto.getImagenRef()));
                }
            } else {
                img.setImageResource(obtenerIdImagen(this, proyecto.getImagenRef()));
            }

            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(EmpresaVista.this, GestionProyectoActivity.class);
                intent.putExtra("idProyecto", proyecto.getIdProyecto());
                intent.putExtra("idEmpresa", proyecto.getIdEmpresa());
                intent.putExtra("carrera", proyecto.getCarreraEnfocada());
                intent.putExtra("nombre", proyecto.getNombreProyecto());
                intent.putExtra("objetivo", proyecto.getObjetivo());
                intent.putExtra("vacantes", proyecto.getVacantes());
                intent.putExtra("ubicacion", proyecto.getUbicacion());
                intent.putExtra("imagen", proyecto.getImagenRef());
                intent.putExtra("imagenProyecto", proyecto.getImagenProyecto());
                startActivity(intent);
            });

            btnEliminar.setOnClickListener(v -> eliminarProyecto(proyecto.getIdProyecto()));
            contenedorProyectos.addView(tarjeta);
        }
    }

    private void eliminarProyecto(int idProyecto) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.eliminarProyecto(idProyecto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EmpresaVista.this, "Proyecto eliminado", Toast.LENGTH_SHORT).show();
                    cargarDatosDelBackend();
                } else {
                    Toast.makeText(EmpresaVista.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EmpresaVista.this, "Fallo de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int obtenerIdImagen(Context context, String nombreImagen) {
        if (nombreImagen == null || nombreImagen.isEmpty()) return R.drawable.img_default_proyecto;
        int resId = context.getResources().getIdentifier(nombreImagen, "drawable", context.getPackageName());
        return (resId != 0) ? resId : R.drawable.img_default_proyecto;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnAñadir) {
            startActivity(new Intent(this, GestionProyectoActivity.class));

        } else if (id == R.id.logoEmpresa) {
            Toast.makeText(this, "Actualizando...", Toast.LENGTH_SHORT).show();
            cargarDatosDelBackend();

        } else if (id == R.id.notificaciones) {
            startActivity(new Intent(this, EmpresaNotificaciones.class));

        } else if (id == R.id.imgPerfilEmpresa) {
            startActivity(new Intent(this, EmpresaVistaCuenta.class));
        }
    }
}

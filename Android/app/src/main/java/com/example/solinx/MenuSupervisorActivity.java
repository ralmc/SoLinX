package com.example.solinx;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.solinx.API.ApiClient;
import com.example.solinx.API.ApiService;
import com.example.solinx.DTO.PerfilDTO;
import com.example.solinx.RESPONSE.SupervisorResponse;
import com.example.solinx.UTIL.ThemeUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuSupervisorActivity extends AppCompatActivity {

    private static final String TAG = "MenuSupervisor";

    private TextView tvNombreSupervisor;
    private ImageView ivProfileMenu;
    private CardView cardAprobarSolicitudAlumno;
    private CardView cardAprobarAceptacionEmpresa;
    private CardView cardDocumentos;
    private CardView cardProyectosEmpresas;
    private CardView cardCambiosPerfil;

    private Supervisor supervisor;
    private ApiService apiService;
    private int idUsuarioActual = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_menu);

        apiService = ApiClient.getClient().create(ApiService.class);

        initViews();
        cargarDatosSupervisor();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarFotoPerfil();
    }

    private void initViews() {
        tvNombreSupervisor           = findViewById(R.id.tv_nombre_supervisor);
        ivProfileMenu                = findViewById(R.id.iv_profile_menu);
        cardAprobarSolicitudAlumno   = findViewById(R.id.card_aprobar_solicitud_alumno);
        cardAprobarAceptacionEmpresa = findViewById(R.id.card_aprobar_aceptacion_empresa);
        cardDocumentos               = findViewById(R.id.card_documentos);
        cardProyectosEmpresas        = findViewById(R.id.card_proyectos_empresas);
        cardCambiosPerfil            = findViewById(R.id.card_cambios_perfil);
    }

    private void cargarDatosSupervisor() {
        idUsuarioActual = getIntent().getIntExtra("idUsuario", -1);
        if (idUsuarioActual == -1) {
            idUsuarioActual = getSharedPreferences("sesion_usuario", MODE_PRIVATE)
                    .getInt("idUsuario", -1);
        }
        if (idUsuarioActual == -1) {
            Toast.makeText(this, "Error: No se recibió ID de usuario", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        getSharedPreferences("sesion_usuario", MODE_PRIVATE)
                .edit().putInt("idUsuario", idUsuarioActual).apply();

        apiService.getSupervisorData(idUsuarioActual).enqueue(new Callback<SupervisorResponse>() {
            @Override
            public void onResponse(Call<SupervisorResponse> call, Response<SupervisorResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    supervisor = response.body().getSupervisor();
                    runOnUiThread(() -> {
                        tvNombreSupervisor.setText(supervisor.getNombre());

                        getSharedPreferences("sesion_usuario", MODE_PRIVATE)
                                .edit().putString("nombre", supervisor.getNombre()).apply();
                    });
                    cargarFotoPerfil();
                } else {
                    Toast.makeText(MenuSupervisorActivity.this,
                            "Error al cargar datos", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            @Override
            public void onFailure(Call<SupervisorResponse> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
                Toast.makeText(MenuSupervisorActivity.this,
                        "Error de conexión", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void cargarFotoPerfil() {
        if (idUsuarioActual == -1 || ivProfileMenu == null) return;
        apiService.obtenerPerfil(idUsuarioActual).enqueue(new Callback<PerfilDTO>() {
            @Override
            public void onResponse(Call<PerfilDTO> call, Response<PerfilDTO> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getFoto() != null
                        && !response.body().getFoto().isEmpty()) {
                    byte[] bytes = Base64.decode(response.body().getFoto(), Base64.DEFAULT);
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    runOnUiThread(() -> ivProfileMenu.setImageBitmap(bmp));
                }
            }
            @Override
            public void onFailure(Call<PerfilDTO> call, Throwable t) {
                Log.e(TAG, "Error foto: " + t.getMessage());
            }
        });
    }

    private void setupListeners() {
        cardAprobarSolicitudAlumno.setOnClickListener(v -> {
            if (supervisor != null) {
                Intent intent = new Intent(this, AprobarSolicitudesAlumnosActivity.class);
                intent.putExtra("idSupervisor", supervisor.getIdSupervisor());
                intent.putExtra("idEmpresa",    supervisor.getIdEmpresa());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Espere a que carguen los datos", Toast.LENGTH_SHORT).show();
            }
        });

        cardProyectosEmpresas.setOnClickListener(v -> {
            if (supervisor != null) {
                Intent intent = new Intent(this, SupervisorProyectosActivity.class);
                intent.putExtra("idSupervisor", supervisor.getIdSupervisor());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Espere a que carguen los datos", Toast.LENGTH_SHORT).show();
            }
        });

        cardAprobarAceptacionEmpresa.setOnClickListener(v -> {
            if (supervisor != null) {
                Intent intent = new Intent(this, AprobarAceptacionesEmpresaActivity.class);
                intent.putExtra("idEmpresa", supervisor.getIdEmpresa());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Espere a que carguen los datos", Toast.LENGTH_SHORT).show();
            }
        });

        cardDocumentos.setOnClickListener(v -> {
            if (supervisor != null) {
                Intent intent = new Intent(this, SupervisorDocumentos.class);
                intent.putExtra("idSupervisor", supervisor.getIdSupervisor());
                intent.putExtra("idEmpresa",    supervisor.getIdEmpresa());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Espere a que carguen los datos", Toast.LENGTH_SHORT).show();
            }
        });

        ivProfileMenu.setOnClickListener(v ->
                startActivity(new Intent(this, SupervisorVistaCuenta.class)));

        cardCambiosPerfil.setOnClickListener(v ->
                startActivity(new Intent(this, SupervisorCambiosPerfil.class)));
    }
}
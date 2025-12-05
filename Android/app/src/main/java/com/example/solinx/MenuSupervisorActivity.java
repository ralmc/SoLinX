package com.example.solinx;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.solinx.API.ApiClient;
import com.example.solinx.API.ApiService;
import com.example.solinx.RESPONSE.SupervisorResponse;
import com.example.solinx.Supervisor;
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

    private Supervisor supervisor;
    private ApiService apiService;

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

    private void initViews() {
        tvNombreSupervisor = findViewById(R.id.tv_nombre_supervisor);
        ivProfileMenu = findViewById(R.id.iv_profile_menu);
        cardAprobarSolicitudAlumno = findViewById(R.id.card_aprobar_solicitud_alumno);
        cardAprobarAceptacionEmpresa = findViewById(R.id.card_aprobar_aceptacion_empresa);
    }

    private void cargarDatosSupervisor() {
        int idUsuario = getIntent().getIntExtra("idUsuario", -1);

        if (idUsuario == -1) {
            Toast.makeText(this, "Error: No se recibió ID de usuario", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Call<SupervisorResponse> call = apiService.getSupervisorData(idUsuario);

        call.enqueue(new Callback<SupervisorResponse>() {
            @Override
            public void onResponse(Call<SupervisorResponse> call, Response<SupervisorResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SupervisorResponse supervisorResponse = response.body();

                    if (supervisorResponse.isSuccess()) {
                        supervisor = supervisorResponse.getSupervisor();
                        tvNombreSupervisor.setText(supervisor.getNombre());

                        Toast.makeText(MenuSupervisorActivity.this,
                                "Bienvenido, " + supervisor.getNombre(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MenuSupervisorActivity.this,
                                supervisorResponse.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(MenuSupervisorActivity.this,
                            "Error al cargar datos",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<SupervisorResponse> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
                Toast.makeText(MenuSupervisorActivity.this,
                        "Error de conexión",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void abrirActivity(Class<?> targetActivity) {
        Intent intent = new Intent(MenuSupervisorActivity.this, targetActivity);
        startActivity(intent);
    }

    private void setupListeners() {
        cardAprobarSolicitudAlumno.setOnClickListener(v -> {
            if (supervisor != null) {
                Intent intent = new Intent(this, AprobarSolicitudesAlumnosActivity.class);
                intent.putExtra("idSupervisor", supervisor.getIdSupervisor());
                intent.putExtra("idEmpresa", supervisor.getIdEmpresa());
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

        ivProfileMenu.setOnClickListener(v -> {
            if (supervisor != null) {
                Toast.makeText(this,
                        "Perfil: " + supervisor.getNombre(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}

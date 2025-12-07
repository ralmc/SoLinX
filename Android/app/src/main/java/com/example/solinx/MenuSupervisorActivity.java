package com.example.solinx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

//VELAZQUEZ REYNOSO ADRIAN
public class MenuSupervisorActivity extends AppCompatActivity {

    private TextView optionSolicitudesAlumnos;
    private TextView optionAceptacionesEmpresas;
    private ImageView ivProfileMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_menu);

        optionSolicitudesAlumnos = findViewById(R.id.option_aprobar_solicitud_alumno);
        optionAceptacionesEmpresas = findViewById(R.id.option_aprobar_aceptacion_empresa);
        ivProfileMenu = findViewById(R.id.iv_profile_menu);


        optionSolicitudesAlumnos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirActivity(SupervisorSolicitudesAlumnoActivity.class);
            }
        });
        optionAceptacionesEmpresas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirActivity(SupervisorEmpresaAceptarActivity.class);
            }
        });
        ivProfileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MenuSupervisorActivity.this, "Abriendo Configuración de Perfil...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Método genérico para abrir cualquier Activity.
     * @param targetActivity La clase de la Activity de destino
     */
    private void abrirActivity(Class<?> targetActivity) {
        Intent intent = new Intent(MenuSupervisorActivity.this, targetActivity);
        startActivity(intent);
    }
}
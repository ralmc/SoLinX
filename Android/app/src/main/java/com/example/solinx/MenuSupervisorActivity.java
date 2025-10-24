package com.example.solinx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// IMPORTANTE: Asegúrate de que estas clases existan y tengan el paquete correcto
// Si están en otro paquete (ej. com.example.solinx), ajusta los imports.
// import com.example.solinx.SupervisorAdminActivity;
// import com.example.solinx.SupervisorTareasActivity;

public class MenuSupervisorActivity extends AppCompatActivity {

    private TextView optionAdministradores;
    private TextView optionTareas;
    private ImageView ivProfileMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_supervisor);

        // 1. Obtener referencias de las vistas
        optionAdministradores = findViewById(R.id.option_administradores);
        optionTareas = findViewById(R.id.option_tareas);
        ivProfileMenu = findViewById(R.id.iv_profile_menu);

        // Opcional: Establecer el nombre de la empresa (si es estático o se carga de un Intent)
        // TextView tvNombreEmpresa = findViewById(R.id.tv_nombre_empresa);
        // tvNombreEmpresa.setText("Mi Gran Empresa S.A.");

        // 2. Establecer Listeners

        // Opción 1: Administradores
        optionAdministradores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirActivity(SupervisorAdminActivity.class);
            }
        });

        // Opción 2: Tareas
        optionTareas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirActivity(SupervisorTareasActivity.class);
            }
        });

        // Icono de perfil (imagen_prederterminada.png)
        ivProfileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MenuSupervisorActivity.this, "Abriendo Configuración de Perfil...", Toast.LENGTH_SHORT).show();
                // Aquí podrías agregar la lógica para abrir una Activity de perfil/configuración
            }
        });
    }

    /**
     * Método genérico para abrir cualquier Activity.
     * @param targetActivity La clase de la Activity de destino (ej: SupervisorAdminActivity.class)
     */
    private void abrirActivity(Class<?> targetActivity) {
        Intent intent = new Intent(MenuSupervisorActivity.this, targetActivity);
        startActivity(intent);
    }
}
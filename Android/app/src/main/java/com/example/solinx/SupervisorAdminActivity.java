package com.example.solinx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton; // Necesario para btn_edit_admin
import android.widget.TextView;
import android.widget.Toast; // Necesario para los mensajes de confirmación
import androidx.appcompat.app.AppCompatActivity;

public class SupervisorAdminActivity extends AppCompatActivity {

    // Declaraciones de vistas (ya tenías tabTareas, ahora agregamos las demás)
    private TextView tabTareas;
    private ImageButton btnEditAdmin;
    private TextView btnDelete;
    private TextView btnAddAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Carga el layout de administradores
        setContentView(R.layout.activity_supervisor_admin);

        // 1. Obtener la referencia de las vistas
        tabTareas = findViewById(R.id.tab_tareas);

        // Referencias de los nuevos botones/acciones
        btnEditAdmin = findViewById(R.id.btn_edit_admin);
        btnDelete = findViewById(R.id.btn_delete);
        btnAddAdmin = findViewById(R.id.btn_add_admin);


        // 2. Establecer el listener para la navegación de pestaña
        tabTareas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar a la Activity de Tareas
                abrirSupervisorTareasActivity();
            }
        });

        // 3. Establecer Listeners para las acciones (con Toast)

        // Acción: Editar Administrador
        btnEditAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SupervisorAdminActivity.this, "Abriendo formulario para Editar Administrador", Toast.LENGTH_SHORT).show();
                // Aquí iría la lógica para pasar los datos de este administrador
            }
        });

        // Acción: Eliminar Administrador
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SupervisorAdminActivity.this, "Administrador marcado para Eliminación", Toast.LENGTH_SHORT).show();
                // Aquí se puede añadir un cuadro de diálogo de confirmación
            }
        });

        // Acción: Añadir Administrador
        btnAddAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SupervisorAdminActivity.this, "Abriendo formulario para Añadir nuevo Administrador", Toast.LENGTH_SHORT).show();
                // Aquí iría la navegación a una nueva Activity/Fragment para el formulario de alta
            }
        });
    }

    /**
     * Función para abrir la Activity SupervisorTareasActivity.
     */
    private void abrirSupervisorTareasActivity() {
        // Crea un Intent para ir a SupervisorTareasActivity
        // NOTA: Asegúrate de que SupervisorTareasActivity exista en el paquete com.example.solinx
        Intent intent = new Intent(SupervisorAdminActivity.this, SupervisorTareasActivity.class);

        // Inicia la nueva Activity
        startActivity(intent);

        // Reemplazar la Activity actual con la nueva para simular el cambio de pestaña
        // y que el botón de retroceso no regrese a esta pantalla.
        finish();
    }
}
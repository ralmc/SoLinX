package com.example.solinx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SupervisorTareasActivity extends AppCompatActivity {

    private TextView tabAdministradores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_tareas);

        // 1. Referencia de la pestaña de navegación
        tabAdministradores = findViewById(R.id.tab_administradores);
        tabAdministradores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirSupervisorAdminActivity();
            }
        });

        // 2. Referencias a las vistas contenedoras de las tarjetas de tarea (las inclusiones)
        View cardTarea1 = findViewById(R.id.card_tarea_001);
        View cardTarea2 = findViewById(R.id.card_tarea_002);
        View cardTarea3 = findViewById(R.id.card_tarea_003);

        // 3. Configurar listeners para cada tarjeta
        // Usamos un método común para establecer el listener y manejar el Toast.
        configurarBotonAsignar(cardTarea1, "Tarea 001");
        configurarBotonAsignar(cardTarea2, "Tarea 002");
        configurarBotonAsignar(cardTarea3, "Tarea 003");
    }

    /**
     * Configura el OnClickListener para el botón btn_asignar dentro de una tarjeta específica.
     * @param cardView La vista principal (el ID de la inclusión)
     * @param tareaNombre El nombre de la tarea a mostrar en el Toast
     */
    private void configurarBotonAsignar(View cardView, final String tareaNombre) {
        if (cardView != null) {
            // Buscar el TextView 'btn_asignar' dentro de la vista de la tarjeta
            TextView btnAsignar = cardView.findViewById(R.id.btn_asignar);

            // Buscar el TextView 'tv_tarea_id' para un mensaje más específico
            TextView tvTareaId = cardView.findViewById(R.id.tv_tarea_id);

            final String idToast = tvTareaId != null ? tvTareaId.getText().toString() : tareaNombre;

            if (btnAsignar != null) {
                btnAsignar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Mensaje de confirmación según lo solicitado
                        Toast.makeText(SupervisorTareasActivity.this, idToast + " ha sido asignada", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    /**
     * Función para abrir la Activity SupervisorAdminActivity y cerrar la actual.
     */
    private void abrirSupervisorAdminActivity() {
        Intent intent = new Intent(SupervisorTareasActivity.this, SupervisorAdminActivity.class);
        startActivity(intent);
        finish();
    }
}
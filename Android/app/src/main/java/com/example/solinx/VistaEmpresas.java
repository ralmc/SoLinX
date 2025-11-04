package com.example.solinx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class VistaEmpresas extends AppCompatActivity {

    // Referencias a los elementos del layout
    private ImageView btnEditar;
    private TextView btnAñadir;
    private TextView tvEliminar;
    private ImageView logoEmpresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_menu_empresas); // Asegúrate de que coincida con tu XML

        // 🔹 Inicializar vistas
        btnEditar = findViewById(R.id.btnEditar);
        btnAñadir = findViewById(R.id.btnAñadir);
        tvEliminar = findViewById(R.id.tvEliminar);
        logoEmpresa = findViewById(R.id.logoEmpresa);

        // ==========================
        // LISTENERS
        // ==========================

        // 🟦 Botón "Editar"
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VistaEmpresas.this, "Editar proyecto", Toast.LENGTH_SHORT).show();
                // Ejemplo: abrir una Activity para editar
                // Intent intent = new Intent(VistaEmpresas.this, EditarProyectoActivity.class);
                // startActivity(intent);
            }
        });

        // 🟩 Texto "Añadir"
        btnAñadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VistaEmpresas.this, "Añadir nuevo proyecto", Toast.LENGTH_SHORT).show();
                // Ejemplo: abrir una Activity para crear un nuevo proyecto
                // Intent intent = new Intent(VistaEmpresas.this, CrearProyectoEmpresa.class);
                // startActivity(intent);
            }
        });

        // 🟥 Texto "Eliminar"
        tvEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VistaEmpresas.this, "Proyecto eliminado", Toast.LENGTH_SHORT).show();
                // Aquí podrías agregar la lógica para eliminar el proyecto
            }
        });

        // 🟨 Logo (opcional): por ejemplo, regresar al inicio o mostrar info
        logoEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VistaEmpresas.this, "Logo presionado", Toast.LENGTH_SHORT).show();
                // Ejemplo: volver al MainActivity
                // Intent intent = new Intent(VistaEmpresas.this, MainActivity.class);
                // startActivity(intent);
            }
        });
    }
}

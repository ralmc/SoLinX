package com.example.solinx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Inicio extends Activity {

    // 🔹 Elementos de la interfaz
    private TextView solinx;
    private ImageView imgLogoAve;
    private TextView tvIniciarSesion;
    private TextView tvCrearCuenta;
    private TextView tvAlumno;
    private TextView tvEmpresa;
    private LinearLayout layoutInferior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio); // 🔹 Asegúrate de que el XML se llame así

        // ================================
        // 📌 Referencias a los elementos del layout
        // ================================
        solinx = findViewById(R.id.solinx);
        imgLogoAve = findViewById(R.id.imgLogoAve);
        tvIniciarSesion = findViewById(R.id.tvIniciarSesion);
        tvCrearCuenta = findViewById(R.id.tvCrearCuenta);
        tvAlumno = findViewById(R.id.tvAlumno);
        tvEmpresa = findViewById(R.id.tvEmpresa);
        layoutInferior = findViewById(R.id.layoutInferior);

        // ================================
        // ⚙️ Configurar listeners
        // ================================

        // 🔹 Opción "Iniciar Sesión"
        tvIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí puedes abrir la Activity de inicio de sesión
                Toast.makeText(Inicio.this, "Ir a Iniciar Sesión", Toast.LENGTH_SHORT).show();

                // Ejemplo si tienes una actividad llamada IniciarSesion.java
                // Intent intent = new Intent(Inicio.this, IniciarSesion.class);
                // startActivity(intent);
            }
        });

        // 🔹 Opción "Crear Cuenta"
        tvCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Inicio.this, "Ir a Crear Cuenta", Toast.LENGTH_SHORT).show();

                // Ejemplo:
                // Intent intent = new Intent(Inicio.this, CrearCuenta.class);
                // startActivity(intent);
            }
        });

        // 🔹 Opción "Alumno"
        tvAlumno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Inicio.this, "Modo Alumno", Toast.LENGTH_SHORT).show();

                // Ejemplo:
                // Intent intent = new Intent(Inicio.this, LoginAlumno.class);
                // startActivity(intent);
            }
        });

        // 🔹 Opción "Empresa"
        tvEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Inicio.this, "Modo Empresa", Toast.LENGTH_SHORT).show();

                // Ejemplo:
                // Intent intent = new Intent(Inicio.this, LoginEmpresa.class);
                // startActivity(intent);
            }
        });
    }
}
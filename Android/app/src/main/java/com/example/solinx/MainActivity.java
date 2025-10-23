package com.example.solinx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {

    // 🔹 Elementos de la interfaz
    private TextView solinx;
    private ImageView imgLogoAve;
    Button btnIniciarSesion;
    private TextView tvCrearCuenta;
    private TextView tvAlumno;
    private TextView tvEmpresa;
    private LinearLayout layoutInferior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // 🔹 Asegúrate de que el XML se llame así

        // ================================
        // 📌 Referencias a los elementos del layout
        // ================================
        solinx = findViewById(R.id.solinx);
        imgLogoAve = findViewById(R.id.imgLogoAve);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        tvCrearCuenta = findViewById(R.id.tvCrearCuenta);
        tvAlumno = findViewById(R.id.tvAlumno);
        tvEmpresa = findViewById(R.id.tvEmpresa);
        layoutInferior = findViewById(R.id.layoutInferior);

        btnIniciarSesion.setOnClickListener(this);
        // ================================
        // ⚙️ Configurar listeners
        // ================================


        // 🔹 Opción "Crear Cuenta"
        tvCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Ir a Crear Cuenta", Toast.LENGTH_SHORT).show();

                // Ejemplo:
                // Intent intent = new Intent(Inicio.this, CrearCuenta.class);
                // startActivity(intent);
            }
        });

        // 🔹 Opción "Alumno"
        tvAlumno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Modo Alumno", Toast.LENGTH_SHORT).show();

                // Ejemplo:
                // Intent intent = new Intent(Inicio.this, LoginAlumno.class);
                // startActivity(intent);
            }
        });

        // 🔹 Opción "Empresa"
        tvEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Modo Empresa", Toast.LENGTH_SHORT).show();

                // Ejemplo:
                // Intent intent = new Intent(Inicio.this, LoginEmpresa.class);
                // startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent intento = new Intent(this,Vcalumno.class);
        startActivity(intento);
    }
}
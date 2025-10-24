package com.example.solinx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {

    // 🔹 Elementos de la interfaz
    private TextView solinx;
    private ImageView imgLogoAve;
    Button btnIniciarSesion, btnCrearCuenta;
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
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        tvAlumno = findViewById(R.id.tvAlumno);
        tvEmpresa = findViewById(R.id.tvEmpresa);
        layoutInferior = findViewById(R.id.layoutInferior);

        btnIniciarSesion.setOnClickListener(this);
        btnCrearCuenta.setOnClickListener(this);
        // ================================
        // ⚙️ Configurar listeners
        // ================================

        //Spinner para el modo alumno

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
    public void onClick(View v) {
        String cadena = ((Button)v).getText().toString();
        if (cadena.equals("Iniciar Sesión")) {
            Intent intento = new Intent(this, iniSesion.class);
            startActivity(intento);
        } if (cadena.equals("Crear Cuenta")) {
            Intent intento = new Intent(this, creSesion.class);
            startActivity(intento);
        }
    }
}
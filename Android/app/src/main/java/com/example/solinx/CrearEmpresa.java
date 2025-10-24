package com.example.solinx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CrearEmpresa extends AppCompatActivity {

    private Button btnRegister;
    private TextView tvLoginLink; // Texto: ¿Ya tienes cuenta? Inicia sesión aquí.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cre_empresa); // Asegúrate de que coincida con tu XML

        // 1️⃣ Referencias de los elementos
        btnRegister = findViewById(R.id.btn_register);
        tvLoginLink = findViewById(R.id.tv_login_link);

        // 2️⃣ Cuando el usuario presiona "Enviar"
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirVistaEmpresas();
            }
        });

        // 3️⃣ Cuando el usuario presiona "¿Ya tienes cuenta? Inicia sesión aquí."
        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirVistaEmpresas();
            }
        });
    }

    /**
     * 🔹 Abre la pantalla VistaEmpresas
     */
    private void abrirVistaEmpresas() {
        Intent intent = new Intent(CrearEmpresa.this, VistaEmpresas.class);
        startActivity(intent);
        // finish(); // Opcional: cerrar esta pantalla si no quieres que pueda volver atrás
    }
}

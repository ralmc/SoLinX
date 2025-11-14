package com.example.solinx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CrearEmpresa extends AppCompatActivity {

    private Button btnRegister;
    private TextView tvLoginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cre_empresa);


        btnRegister = findViewById(R.id.btn_register);
        tvLoginLink = findViewById(R.id.tv_login_link);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirVistaEmpresas();
            }
        });


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
        Intent intent = new Intent(CrearEmpresa.this, iniSesion.class);
        startActivity(intent);
    }
}

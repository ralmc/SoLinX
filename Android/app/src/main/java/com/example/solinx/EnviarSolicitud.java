package com.example.solinx;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EnviarSolicitud extends AppCompatActivity implements View.OnClickListener {
    ImageButton regresar;
    Button enviar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_enviar_solicitud);

        regresar = findViewById(R.id.regresar);
        regresar.setOnClickListener(this);
        enviar = findViewById(R.id.enviar);
        enviar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.regresar) {
            Intent intento = new Intent(this, MenuEmpresas.class);
            startActivity(intento);
        }
        if (id == R.id.enviar) {
            Toast.makeText(this, "Solicitud Enviada...", Toast.LENGTH_SHORT).show();
        }
    }
}
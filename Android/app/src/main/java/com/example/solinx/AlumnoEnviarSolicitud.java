package com.example.solinx;
// LANDA CABALLERO ANGEL
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class AlumnoEnviarSolicitud extends AppCompatActivity implements View.OnClickListener {
    ImageView btnRegresar;
    TextView btnEnviar, presta, fechaini, fechafin, repre, vacantes, ubi, obj, just, apoyos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alumno_enviar_solicitud);

        btnRegresar = findViewById(R.id.btnRegresar);
        btnEnviar = findViewById(R.id.btnEnviar);
        presta = findViewById(R.id.prestatario);
        fechaini = findViewById(R.id.fechini);
        fechafin = findViewById(R.id.fechfin);
        repre = findViewById(R.id.representante);
        vacantes = findViewById(R.id.vacantes);
        ubi = findViewById(R.id.ubicacion);
        obj = findViewById(R.id.objetivo);
        just = findViewById(R.id.justificacion);
        apoyos = findViewById(R.id.apoyos);

        //Declarar los setOnClickListener
        btnEnviar.setOnClickListener(this);
        btnRegresar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnRegresar) {
            Intent intento = new Intent(this, AlumnoMenuEmpresas.class);
            startActivity(intento);
        }  if (id == R.id.btnEnviar) {
            Toast.makeText(this, "Solicitud Enviada...", Toast.LENGTH_SHORT).show();
        }
    }
}
package com.example.solinx;
// LANDA CABALLERO ANGEL
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class AlumnoEnviarSolicitud extends AppCompatActivity implements View.OnClickListener {
    ImageButton regresar;
    TextView presta, fechaini, fechafin, repre, vacantes, ubi, obj, just, apoyos;
    Button enviar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alumno_enviar_solicitud);

        regresar = findViewById(R.id.regresar);
        regresar.setOnClickListener(this);
        enviar = findViewById(R.id.enviar);
        enviar.setOnClickListener(this);

        presta = findViewById(R.id.prestatario);
        fechaini = findViewById(R.id.fechini);
        fechafin = findViewById(R.id.fechfin);
        repre = findViewById(R.id.representante);
        vacantes = findViewById(R.id.vacantes);
        ubi = findViewById(R.id.ubicacion);
        obj = findViewById(R.id.objetivo);
        just = findViewById(R.id.justificacion);
        apoyos = findViewById(R.id.apoyos);

        presta.append(" SoLinX");
        fechaini.append(" 18/10/2025");
        fechafin.append(" 18/10/2026");
        repre.append(" Velázquez Reynoso Adrian");
        vacantes.append(" 9");
        ubi.append(" Mar Mediterráneo 227, Nextitla, Miguel Hidalgo, 11420 Ciudad de México, CDMX");
        obj.append(" Crear una aplicación que ayude a los estudiantes a realizar su servicio social y que al mismo" +
                "tiempo tengan experiencia laboral.");
        just.append(" Ayudar a los alumnos con su servicio social creando una aplicación que falicite el proceso.");
        apoyos.append(" 1. Apoyo Monetario de $100 al mes\n\t\t\t\t\t\t\t2. Cursos de adaptación");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.regresar) {
            Intent intento = new Intent(this, AlumnoMenuEmpresas.class);
            startActivity(intento);
        }
        if (id == R.id.enviar) {
            Toast.makeText(this, "Solicitud Enviada...", Toast.LENGTH_SHORT).show();
        }
    }
}
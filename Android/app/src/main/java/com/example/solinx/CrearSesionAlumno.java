package com.example.solinx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class CrearSesionAlumno extends AppCompatActivity implements View.OnClickListener {
    Spinner spEscuela, spCarrera;
    Button btnEnviar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alumno_crear_sesion);

        spEscuela = findViewById(R.id.spEscuela);
        spCarrera = findViewById(R.id.spCarrera);
        btnEnviar = findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(this);

        String[] escuelas = {"Selecciona", "Cecyt 9"};
        String[] carreras = {"Selecciona", "Programación"};

        ArrayAdapter<String> adapterEscuela = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                escuelas
        );
        adapterEscuela.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEscuela.setAdapter(adapterEscuela);

        ArrayAdapter<String> adapterCarrera = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                carreras
        );
        adapterCarrera.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCarrera.setAdapter(adapterCarrera);
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(this, "Enviado...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
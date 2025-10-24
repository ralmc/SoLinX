package com.example.solinx;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class CrearCuenta extends AppCompatActivity {

    private Spinner spEscuela, spCarrera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_cuenta);

        spEscuela = findViewById(R.id.spEscuela);
        spCarrera = findViewById(R.id.spCarrera);


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
}


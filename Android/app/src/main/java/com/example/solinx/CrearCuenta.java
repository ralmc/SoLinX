package com.example.solinx; // cambia por tu paquete real

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

import com.example.solinx.R;

public class CrearCuenta extends AppCompatActivity {

    private Spinner spEscuela, spCarrera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 👇 Aquí pones el XML que tú estás usando, no el main
        setContentView(R.layout.pantalla_crear_cuenta);

        // Spinners
        spEscuela = findViewById(R.id.spEscuela);
        spCarrera = findViewById(R.id.spCarrera);

        // Datos para los spinners (puedes cambiarlos después)
        String[] escuelas = {"ESCOM", "UPIICSA", "ESIME", "CET", "CECyT"};
        String[] carreras = {"Ingeniería en Sistemas", "Informática", "Telecomunicaciones", "Administración", "Contabilidad"};

        // Adaptadores
        ArrayAdapter<String> adapterEscuela = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, escuelas);
        ArrayAdapter<String> adapterCarrera = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, carreras);

        // Asignarlos a los spinners
        spEscuela.setAdapter(adapterEscuela);
        spCarrera.setAdapter(adapterCarrera);
    }
}

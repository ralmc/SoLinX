package com.example.solinx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MenuEmpresas extends AppCompatActivity implements View.OnClickListener {
    Button masinfo;
    ImageButton imagen;
    TextView prestam, fecha, representante, vacantes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_empresas);

        masinfo = findViewById(R.id.masinfo);
        masinfo.setOnClickListener(this);
        imagen = findViewById(R.id.fotoperfil);
        imagen.setOnClickListener(this);
        prestam = findViewById(R.id.prestatario);
        fecha = findViewById(R.id.fechas);
        representante = findViewById(R.id.representante);
        vacantes = findViewById(R.id.vacantes);

        prestam.append(" SoLinX");
        fecha.append(" 18/10/2025");
        representante.append(" Velázquez Reynoso Adrian");
        vacantes.append(" 7");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (masinfo.getId() == id) {
            Intent intento = new Intent(this, EnviarSolicitud.class);
            startActivity(intento);
        } if (imagen.getId() == id) {
            Intent intento = new Intent(this, Vcalumno.class);
            startActivity(intento);
        }
    }
}

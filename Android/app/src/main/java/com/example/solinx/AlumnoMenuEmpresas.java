package com.example.solinx;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class AlumnoMenuEmpresas extends AppCompatActivity implements View.OnClickListener {
    ImageView fotoperfil;
    TextView masinfo, prestam, fecha, representante, vacantes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alumno_menu_empresas);

        masinfo = findViewById(R.id.masinfo);
        masinfo.setOnClickListener(this);
        fotoperfil = findViewById(R.id.fotoperfil);
        fotoperfil.setOnClickListener(this);
        prestam = findViewById(R.id.prestatario);
        fecha = findViewById(R.id.fechas);
        representante = findViewById(R.id.representante);
        vacantes = findViewById(R.id.vacantes);

        Intent intent = getIntent();
        Integer idUsuario = intent.getIntExtra("idUsuario", 0);
        String nombre = intent.getStringExtra("nombre");
        String correo = intent.getStringExtra("correo");
        String rol = intent.getStringExtra("rol");

        Toast.makeText(this, "Hola " + nombre + " (" + rol + ")", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == masinfo.getId() ) {
            Intent intento = new Intent(this, AlumnoEnviarSolicitud.class);
            startActivity(intento);
        } else if (id == fotoperfil.getId()) {
            Intent intento = new Intent(this, AlumnoVistaCuenta.class);
            startActivity(intento);
        }
    }
}

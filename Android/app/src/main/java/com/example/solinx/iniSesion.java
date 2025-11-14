package com.example.solinx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class iniSesion extends AppCompatActivity implements View.OnClickListener {
    EditText nombre, contraseña;
    Button enviar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ini_sesion);

        nombre = findViewById(R.id.etUsuario);
        contraseña = findViewById(R.id.etContrasena);
        enviar = findViewById(R.id.btnEnviar);
        enviar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, AlumnoMenuEmpresas.class);
        startActivity(intent);
    }
}
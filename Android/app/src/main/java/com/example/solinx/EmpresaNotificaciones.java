package com.example.solinx;
// LANDA CABALLERO ANGEL
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class EmpresaNotificaciones extends AppCompatActivity implements View.OnClickListener {
    Button menu, notificaciones, ad1, ad2, ad3, ne1, ne2, ne3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_empresa_notificaciones);

        menu = findViewById(R.id.btnMenu);
        menu.setOnClickListener(this);
        notificaciones = findViewById(R.id.btnNotificaciones);
        notificaciones.setOnClickListener(this);
        ad1 = findViewById(R.id.admitir);
        ad1.setOnClickListener(this);
        ad2 = findViewById(R.id.admitir2);
        ad2.setOnClickListener(this);
        ad3 = findViewById(R.id.admitir3);
        ad3.setOnClickListener(this);
        ne1 = findViewById(R.id.rechazar);
        ne1.setOnClickListener(this);
        ne2 = findViewById(R.id.rechazar2);
        ne2.setOnClickListener(this);
        ne3 = findViewById(R.id.rechazar3);
        ne3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String cadena = ((Button)v).getText().toString();
        if (cadena.equals("Menú")) {
            Intent intent = new Intent(this, VistaEmpresas.class);
            startActivity(intent);
        } if (cadena.equals("Notificaciones")) {
            Toast.makeText(this, "Estas en Notificaciones...", Toast.LENGTH_SHORT).show();
        } if (cadena.equals("Admitir")) {
            Toast.makeText(this, "Usuario Admitido...", Toast.LENGTH_SHORT).show();
        } if (cadena.equals("Rechazar")) {
            Toast.makeText(this, "Usuario Rechazado...", Toast.LENGTH_SHORT).show();
        }
    }
}
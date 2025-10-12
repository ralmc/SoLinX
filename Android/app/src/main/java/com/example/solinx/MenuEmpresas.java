package com.example.solinx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MenuEmpresas extends AppCompatActivity implements View.OnClickListener {
    Button masinfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_empresas);

        masinfo = findViewById(R.id.masinfo);
        masinfo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String cadena = ((Button)v).getText().toString();
        if (cadena.equals("Mas información...")) {
            Intent intento = new Intent(this, EnviarSolicitud.class);
            startActivity(intento);
        }
    }
}
package com.example.solinx;

import static android.app.ProgressDialog.show;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EmpresaVista extends AppCompatActivity implements View.OnClickListener{

    // Referencias a los elementos del layout
    ImageView btnEditar, logoEmpresa;
    TextView btnAñadir, tvEliminar, btnotificaciones, btnaceptado, btnpendiente, btnrechazado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_vista_menu);
        //Definir TextView y ImageView
        btnEditar = findViewById(R.id.btnEditar);
        btnAñadir = findViewById(R.id.btnAñadir);
        tvEliminar = findViewById(R.id.tvEliminar);
        logoEmpresa = findViewById(R.id.logoEmpresa);
        btnotificaciones = findViewById(R.id.notificaciones);

        btnotificaciones.setOnClickListener(this);
        btnEditar.setOnClickListener(this);
        btnAñadir.setOnClickListener(this);
        tvEliminar.setOnClickListener(this);
        logoEmpresa.setOnClickListener(this);
        btnpendiente.setOnClickListener(this);
        btnrechazado.setOnClickListener(this);
        btnaceptado.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.notificaciones){
            Intent intento = new Intent(this, EmpresaNotificaciones.class);
            startActivity(intento);
        } else if (id == R.id.btnAñadir) {
            Toast.makeText(this, "Añadido", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.tvEliminar) {
            Toast.makeText(this, "Eliminado", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.btnEditar){
            Toast.makeText(this, "Editado", Toast.LENGTH_SHORT).show();
        }
    }
}

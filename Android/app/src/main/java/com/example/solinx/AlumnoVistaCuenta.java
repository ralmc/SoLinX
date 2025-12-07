package com.example.solinx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AlumnoVistaCuenta extends AppCompatActivity implements View.OnClickListener {

    Button btnCerrarSesion;
    ImageButton regresar;
    private TextView tvBoleta;
    private ImageView imgLogoAve;

    // Modo
    private View viewModoClaro, viewModoOscuro;
    private TextView tvModo;

    // Foto de perfil
    private TextView tvCambiarFoto;
    private ImageView imgPerfil;

    // Información y status
    private TextView tvInfoAlumnado, tvPuntosInfo, tvStatus, tvPuntosStatus;

    // Empresas
    private TextView tvEmpresa1Title, tvEmpresa2Title;

    // Cerrar sesión
    private TextView tvCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumno_vista_cuenta); // tu XML

        // --- Header ---
        tvBoleta = findViewById(R.id.tvBoleta);
        imgLogoAve = findViewById(R.id.imgLogoAve);
        regresar = findViewById(R.id.regresar);
        regresar.setOnClickListener(this);

        // --- Modo ---
        tvModo = findViewById(R.id.tvModo);
        viewModoClaro = findViewById(R.id.viewModoClaro);
        viewModoOscuro = findViewById(R.id.viewModoOscuro);

        viewModoClaro.setOnClickListener(v -> seleccionarModo(true));
        viewModoOscuro.setOnClickListener(v -> seleccionarModo(false));

        // --- Foto de perfil ---
        tvCambiarFoto = findViewById(R.id.tvCambiarFoto);
        imgPerfil = findViewById(R.id.imgPerfil);

        tvCambiarFoto.setOnClickListener(v -> cambiarFotoPerfil());

        // --- Información y status ---
        tvInfoAlumnado = findViewById(R.id.tvInfoAlumnado);
        tvPuntosInfo = findViewById(R.id.tvPuntosInfo);
        tvStatus = findViewById(R.id.tvStatus);
        tvPuntosStatus = findViewById(R.id.tvPuntosStatus);

        // --- Empresas ---
        tvEmpresa1Title = findViewById(R.id.tvEmpresa1Title);
        tvEmpresa2Title = findViewById(R.id.tvEmpresa2Title);

        // --- Cerrar sesión ---
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(this);
    }

    // Método para seleccionar modo (claro u oscuro)
    private void seleccionarModo(boolean modoClaro) {
        if (modoClaro) {
            // aquí pones lógica para modo claro
            tvModo.setText("Modo Claro");
        } else {
            // aquí pones lógica para modo oscuro
            tvModo.setText("Modo Oscuro");
        }
    }

    // Método para cambiar foto de perfil
    private void cambiarFotoPerfil() {
        // ejemplo simple: abrir galería o cambiar imagen por defecto
        // por ahora solo puedes mostrar un mensaje
        // Toast.makeText(this, "Cambiar foto de perfil", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (btnCerrarSesion.getId() == id) {
            Intent intento = new Intent(this, MainActivity.class);
            startActivity(intento);
        } if (regresar.getId() == id) {
            Intent intento = new Intent(this, AlumnoMenuEmpresas.class);
            startActivity(intento);
        }
    }
}
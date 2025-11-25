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

    private View viewModoClaro, viewModoOscuro;
    private TextView tvModo;

    private TextView tvCambiarFoto;
    private ImageView imgPerfil;

    private TextView tvInfoAlumnado, tvPuntosInfo, tvStatus, tvPuntosStatus;

    private TextView tvEmpresa1Title, tvEmpresa2Title;

    private TextView tvCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumno_vista_cuenta);

        tvBoleta = findViewById(R.id.tvBoleta);
        imgLogoAve = findViewById(R.id.imgLogoAve);
        regresar = findViewById(R.id.regresar);
        regresar.setOnClickListener(this);

        tvModo = findViewById(R.id.tvModo);
        viewModoClaro = findViewById(R.id.viewModoClaro);
        viewModoOscuro = findViewById(R.id.viewModoOscuro);

        viewModoClaro.setOnClickListener(v -> seleccionarModo(true));
        viewModoOscuro.setOnClickListener(v -> seleccionarModo(false));

        tvCambiarFoto = findViewById(R.id.tvCambiarFoto);
        imgPerfil = findViewById(R.id.imgPerfil);

        tvCambiarFoto.setOnClickListener(v -> cambiarFotoPerfil());

        tvInfoAlumnado = findViewById(R.id.tvInfoAlumnado);
        tvPuntosInfo = findViewById(R.id.tvPuntosInfo);
        tvStatus = findViewById(R.id.tvStatus);
        tvPuntosStatus = findViewById(R.id.tvPuntosStatus);

        tvEmpresa1Title = findViewById(R.id.tvEmpresa1Title);
        tvEmpresa2Title = findViewById(R.id.tvEmpresa2Title);

        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(this);
    }

    private void seleccionarModo(boolean modoClaro) {
        if (modoClaro) {
            tvModo.setText("Modo Claro");
        } else {
            tvModo.setText("Modo Oscuro");
        }
    }

    private void cambiarFotoPerfil() {
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
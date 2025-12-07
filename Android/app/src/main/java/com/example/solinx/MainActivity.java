package com.example.solinx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {

    private TextView solinx;
    private ImageView imgLogoAve;
    Button btnIniciarSesion, btnCrearCuenta;
    private TextView tvAlumno, tvEmpresa, tvSupervisor;
    private LinearLayout layoutInferior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        solinx = findViewById(R.id.solinx);
        imgLogoAve = findViewById(R.id.imgLogoAve);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        tvAlumno = findViewById(R.id.tvAlumno);
        tvEmpresa = findViewById(R.id.tvEmpresa);
        tvSupervisor = findViewById(R.id.tvSupervisor);
        layoutInferior = findViewById(R.id.layoutInferior);

        // Listeners generales
        btnIniciarSesion.setOnClickListener(this);
        btnCrearCuenta.setOnClickListener(this);
        tvSupervisor.setOnClickListener(this);
        tvEmpresa.setOnClickListener(this);
        tvAlumno.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnIniciarSesion) {
            Intent intento = new Intent(this, IniciarSesion.class);
            startActivity(intento);
        } else if (v.getId() == R.id.btnCrearCuenta) {
            Intent intento = new Intent(this, AlumnoCrearCuenta.class);
            startActivity(intento);
        } else if (v.getId() == R.id.tvSupervisor) {
            Intent intent = new Intent(this, MenuSupervisorActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.tvAlumno) {
        } else if (v.getId() == R.id.tvEmpresa) {
            Intent intent = new Intent(this, EmpresaCrear.class);
            startActivity(intent);
        }
    }
}

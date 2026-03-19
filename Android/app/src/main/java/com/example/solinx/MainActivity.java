package com.example.solinx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.solinx.CONEXION.InicioHelper;
import com.example.solinx.UTIL.ThemeUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView solinx;
    private ImageView imgLogoAve;
    Button btnIniciarSesion, btnCrearCuenta;
    private TextView tvAlumno, tvEmpresa, tvSupervisor;
    private LinearLayout layoutInferior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);

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

        btnIniciarSesion.setOnClickListener(this);
        btnCrearCuenta.setOnClickListener(this);
        tvSupervisor.setOnClickListener(this);
        tvEmpresa.setOnClickListener(this);
        tvAlumno.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnIniciarSesion) {
            iniciarSesion();
        } else if (v.getId() == R.id.btnCrearCuenta) {
            Intent intent = new Intent(this, InicioHelper.class);
            intent.putExtra("CREAR","CREAR");
            startActivity(intent);
        } else if (v.getId() == R.id.tvSupervisor) {
            iniciarSesion();
        } else if (v.getId() == R.id.tvEmpresa) {
            Intent intent = new Intent(this, InicioHelper.class);
            intent.putExtra("CREARe","CREARe");
            startActivity(intent);
        }
    }

    public void iniciarSesion() {
        Intent intent = new Intent(this, InicioHelper.class);
        intent.putExtra("INICIAR","INICIAR");
        startActivity(intent);
    }
}

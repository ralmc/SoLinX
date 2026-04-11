package com.example.solinx;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.solinx.CONEXION.InicioHelper;
import com.example.solinx.UTIL.ThemeUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        Button btnCrearCuenta   = findViewById(R.id.btnCrearCuenta);
        TextView tvEmpresa      = findViewById(R.id.tvEmpresa);
        TextView tvSupervisor   = findViewById(R.id.tvSupervisor);

        btnIniciarSesion.setOnClickListener(v -> irA(InicioHelper.EXTRA_INICIAR));
        tvSupervisor.setOnClickListener(v    -> irA(InicioHelper.EXTRA_INICIAR));
        btnCrearCuenta.setOnClickListener(v  -> irA(InicioHelper.EXTRA_CREAR));
        tvEmpresa.setOnClickListener(v       -> irA(InicioHelper.EXTRA_CREAR_E));
    }

    private void irA(String accion) {
        Intent intent = new Intent(this, InicioHelper.class);
        intent.putExtra("accion", accion);
        startActivity(intent);
    }
}
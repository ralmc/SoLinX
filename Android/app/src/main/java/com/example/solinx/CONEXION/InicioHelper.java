package com.example.solinx.CONEXION;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.solinx.AlumnoCrearCuenta;
import com.example.solinx.AlumnoHorario;
import com.example.solinx.EmpresaCrear;
import com.example.solinx.IniciarSesion;
import com.example.solinx.R;
import com.example.solinx.UTIL.ThemeUtils;

public class InicioHelper extends AppCompatActivity {
    private static final String TAG = "InicioHelper";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_helper);
        String iniciar = "", crear = "", crearE = "";
        int boleta;

        try {
            boleta = getIntent().getIntExtra("boleta", -1);
            Log.w(TAG, "Status boleta: OK, " + boleta);
            crear = getIntent().getStringExtra("CREAR");
            Log.w(TAG, "Status crear: OK, " + crear);
            crearE = getIntent().getStringExtra("CREARe");
            Log.w(TAG, "Status crearE: OK, " + crear);
            iniciar = getIntent().getStringExtra("INICIAR");
            Log.w(TAG, "Status iniciar: OK, " + iniciar);

            if ("CREAR".equals(crear)) {
                Crear();
            } else if ("CREARe".equals(crearE)) {
                CrearE();
            } else if ("INICIAR".equals(iniciar)) {
                Iniciar();
            } else if (boleta != -1) {
                Horario(boleta);
            } else {
                Iniciar();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error: boleta");
            throw new RuntimeException(e);
        }
    }
    public void Crear() {
        AlumnoCrearCuenta fragment = new AlumnoCrearCuenta();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
    public void CrearE() {
        EmpresaCrear fragment = new EmpresaCrear();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
    public void Iniciar() {
        IniciarSesion fragment = new IniciarSesion();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
    public void Horario(int boleta) {
        Bundle bundle = new Bundle();
        bundle.putInt("boleta", boleta);

        AlumnoHorario fragment = new AlumnoHorario();
        fragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
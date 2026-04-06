package com.example.solinx.CONEXION;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.solinx.AlumnoCrearCuenta;
import com.example.solinx.AlumnoHorario;
import com.example.solinx.EmpresaCrear;
import com.example.solinx.IniciarSesion;
import com.example.solinx.R;
import com.example.solinx.UTIL.ThemeUtils;

public class InicioHelper extends AppCompatActivity {

    private static final String TAG = "InicioHelper";

    public static final String EXTRA_INICIAR = "INICIAR";
    public static final String EXTRA_CREAR   = "CREAR";
    public static final String EXTRA_CREAR_E = "CREARe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_helper);

        if (savedInstanceState != null) return;

        String accion = getIntent().getStringExtra("accion");
        int    boleta = getIntent().getIntExtra("boleta", -1);

        if (EXTRA_CREAR.equals(accion)) {
            mostrarCrearCuenta();
        } else if (EXTRA_CREAR_E.equals(accion)) {
            mostrarCrearEmpresa();
        } else if (boleta != -1) {
            mostrarHorario(boleta);
        } else {
            mostrarIniciarSesion();
        }
    }

    public void mostrarCrearCuenta()    { reemplazarFragment(new AlumnoCrearCuenta()); }
    public void mostrarCrearEmpresa()   { reemplazarFragment(new EmpresaCrear()); }
    public void mostrarIniciarSesion()  { reemplazarFragment(new IniciarSesion()); }

    public void mostrarHorario(int boleta) {
        AlumnoHorario fragment = new AlumnoHorario();
        Bundle args = new Bundle();
        args.putInt("boleta", boleta);
        fragment.setArguments(args);
        reemplazarFragment(fragment);
    }

    public void mostrarHorarioConDatos(Bundle datos) {
        AlumnoHorario fragment = new AlumnoHorario();
        fragment.setArguments(datos);
        reemplazarFragment(fragment);
    }

    private void reemplazarFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
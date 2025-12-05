package com.example.solinx;
// LANDA CABALLERO ANGEL
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.solinx.UTIL.ThemeUtils;

public class AlumnoEnviarSolicitud extends AppCompatActivity implements View.OnClickListener {
    ImageView btnRegresar, imgLogoProyecto;
    TextView btnEnviar, txtNombreEmpresa, txtNombreProyecto, fechaini, fechafin;
    TextView txtCarreraEnfocada, telefono, vacantes, ubi, obj;

    private Integer proyectoId;
    private Integer idEmpresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alumno_enviar_solicitud);

        btnRegresar = findViewById(R.id.btnRegresar);
        imgLogoProyecto = findViewById(R.id.imgLogoProyecto);
        btnEnviar = findViewById(R.id.btnEnviar);
        txtNombreEmpresa = findViewById(R.id.txtNombreEmpresa);
        txtNombreProyecto = findViewById(R.id.txtNombreProyecto);
        fechaini = findViewById(R.id.fechini);
        fechafin = findViewById(R.id.fechfin);
        txtCarreraEnfocada = findViewById(R.id.txtCarreraEnfocada);
        telefono = findViewById(R.id.telefono);
        vacantes = findViewById(R.id.vacantes);
        ubi = findViewById(R.id.ubicacion);
        obj = findViewById(R.id.objetivo);

        btnEnviar.setOnClickListener(this);
        btnRegresar.setOnClickListener(this);

        recibirDatosDelProyecto();
    }

    private void recibirDatosDelProyecto() {
        Intent intent = getIntent();
        if (intent != null) {
            proyectoId = intent.getIntExtra("proyectoId", 0);
            idEmpresa = intent.getIntExtra("idEmpresa", 0);

            String nombreEmpresa = intent.getStringExtra("nombreEmpresa");
            String nombreProyecto = intent.getStringExtra("nombreProyecto");
            String fechaInicio = intent.getStringExtra("fechaInicio");
            String fechaFin = intent.getStringExtra("fechaFin");
            String carreraEnfocada = intent.getStringExtra("carreraEnfocada");
            String telefonoEmpresa = intent.getStringExtra("telefono");  // NUEVO
            int vacantesDisponibles = intent.getIntExtra("vacantes", 0);
            String ubicacion = intent.getStringExtra("ubicacion");
            String objetivo = intent.getStringExtra("objetivo");
            String imagenRef = intent.getStringExtra("imagenRef");

            if (nombreEmpresa != null && !nombreEmpresa.isEmpty()) {
                txtNombreEmpresa.setText("Empresa: " + nombreEmpresa);
            } else {
                txtNombreEmpresa.setVisibility(View.GONE);
            }

            if (nombreProyecto != null && !nombreProyecto.isEmpty()) {
                txtNombreProyecto.setText("Proyecto: " + nombreProyecto);
            } else {
                txtNombreProyecto.setText("Proyecto sin nombre");
            }

            if (fechaInicio != null && !fechaInicio.isEmpty()) {
                fechaini.setText("Fecha Inicio: " + fechaInicio);
            } else {
                fechaini.setText("Fecha Inicio: No disponible");
            }

            if (fechaFin != null && !fechaFin.isEmpty()) {
                fechafin.setText("Fecha Termino: " + fechaFin);
            } else {
                fechafin.setText("Fecha Termino: No disponible");
            }

            if (carreraEnfocada != null && !carreraEnfocada.isEmpty()) {
                txtCarreraEnfocada.setText("Carrera Enfocada: " + carreraEnfocada);
            } else {
                txtCarreraEnfocada.setText("Carrera Enfocada: No especificada");
            }

            if (telefonoEmpresa != null && !telefonoEmpresa.isEmpty()) {
                telefono.setText("Teléfono: " + telefonoEmpresa);
            } else {
                telefono.setText("Teléfono: No disponible");
            }

            vacantes.setText("Vacantes: " + vacantesDisponibles);

            if (ubicacion != null && !ubicacion.isEmpty()) {
                ubi.setText("Ubicación: " + ubicacion);
            } else {
                ubi.setText("Ubicación: No especificada");
            }

            if (objetivo != null && !objetivo.isEmpty()) {
                obj.setText("Objetivo: " + objetivo);
            } else {
                obj.setText("Objetivo: No especificado");
            }
            imgLogoProyecto.setImageResource(R.drawable.solinx_logo);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnRegresar) {
            finish();
        } else if (id == R.id.btnEnviar) {
            enviarSolicitud();
        }
    }

    private void enviarSolicitud() {
        Toast.makeText(this, "Solicitud Enviada...", Toast.LENGTH_SHORT).show();
        finish();
    }
}
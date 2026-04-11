package com.example.solinx;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.solinx.API.ApiClient;
import com.example.solinx.API.ApiService;
import com.example.solinx.DTO.EmpresaDTO;
import com.example.solinx.DTO.PerfilDTO;
import com.example.solinx.DTO.SolicitudDTO;
import com.example.solinx.UTIL.ThemeUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlumnoEnviarSolicitud extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AlumnoEnviarSolicitud";

    ImageView btnRegresar, imgLogoProyecto, fotoPerfilHeader;
    TextView btnEnviar, txtNombreEmpresa, txtNombreProyecto, fechaini, fechafin;
    TextView txtCarreraEnfocada, telefono, vacantes, ubi, obj;
    TextView btnboleta, btnEnviarCorreo;

    private Integer proyectoId;
    private Integer idEmpresa;
    private Integer boletaAlumno;
    private String correoEmpresa = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alumno_enviar_solicitud);

        btnRegresar       = findViewById(R.id.btnRegresar);
        imgLogoProyecto   = findViewById(R.id.imgLogoProyecto);
        btnEnviar         = findViewById(R.id.btnEnviar);
        txtNombreEmpresa  = findViewById(R.id.txtNombreEmpresa);
        txtNombreProyecto = findViewById(R.id.txtNombreProyecto);
        fechaini          = findViewById(R.id.fechini);
        fechafin          = findViewById(R.id.fechfin);
        txtCarreraEnfocada= findViewById(R.id.txtCarreraEnfocada);
        telefono          = findViewById(R.id.telefono);
        vacantes          = findViewById(R.id.vacantes);
        ubi               = findViewById(R.id.ubicacion);
        obj               = findViewById(R.id.objetivo);
        btnboleta         = findViewById(R.id.btnboleta);
        fotoPerfilHeader  = findViewById(R.id.fotoPerfilHeader);
        btnEnviarCorreo   = findViewById(R.id.btnEnviarCorreo);

        btnEnviar.setOnClickListener(this);
        btnRegresar.setOnClickListener(this);
        if (btnEnviarCorreo != null) btnEnviarCorreo.setOnClickListener(this);

        obtenerBoletaEstudiante();
        cargarFotoYBoleta();
        recibirDatosDelProyecto();
        buscarCorreoEmpresa();
    }

    private void obtenerBoletaEstudiante() {
        SharedPreferences prefs = getSharedPreferences("SoLinXPrefs", MODE_PRIVATE);
        String boletaStr = prefs.getString("boleta", null);

        if (boletaStr != null && !boletaStr.equals("N/A")) {
            try {
                boletaAlumno = Integer.parseInt(boletaStr);
            } catch (NumberFormatException e) {
                boletaAlumno = null;
            }
        }
    }

    private void cargarFotoYBoleta() {
        SharedPreferences prefs = getSharedPreferences("SoLinXPrefs", MODE_PRIVATE);
        String boleta = prefs.getString("boleta", "N/A");

        if (btnboleta != null) {
            btnboleta.setText(boleta);
        }

        if (fotoPerfilHeader != null) {
            int idUsuario = getSharedPreferences("sesion_usuario", MODE_PRIVATE)
                    .getInt("idUsuario", -1);
            if (idUsuario == -1) return;

            ApiService api = ApiClient.getClient().create(ApiService.class);
            api.obtenerPerfil(idUsuario).enqueue(new Callback<PerfilDTO>() {
                @Override
                public void onResponse(Call<PerfilDTO> call, Response<PerfilDTO> response) {
                    if (response.isSuccessful() && response.body() != null
                            && response.body().getFoto() != null
                            && !response.body().getFoto().isEmpty()) {
                        String b64 = response.body().getFoto();
                        byte[] bytes = Base64.decode(b64, Base64.DEFAULT);
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        runOnUiThread(() -> fotoPerfilHeader.setImageBitmap(bmp));
                    }
                }

                @Override
                public void onFailure(Call<PerfilDTO> call, Throwable t) {
                    Log.e(TAG, "Error al cargar foto: " + t.getMessage());
                }
            });
        }
    }

    private void recibirDatosDelProyecto() {
        Intent intent = getIntent();
        if (intent != null) {
            proyectoId = intent.getIntExtra("proyectoId", 0);
            idEmpresa  = intent.getIntExtra("idEmpresa", 0);

            String nombreEmpresa       = intent.getStringExtra("nombreEmpresa");
            String nombreProyecto      = intent.getStringExtra("nombreProyecto");
            String fechaInicio         = intent.getStringExtra("fechaInicio");
            String fechaFin            = intent.getStringExtra("fechaFin");
            String carreraEnfocada     = intent.getStringExtra("carreraEnfocada");
            String telefonoEmpresa     = intent.getStringExtra("telefono");
            int    vacantesDisponibles = intent.getIntExtra("vacantes", 0);
            String ubicacion           = intent.getStringExtra("ubicacion");
            String objetivo            = intent.getStringExtra("objetivo");
            String imagenProyecto      = intent.getStringExtra("imagenProyecto");

            txtNombreEmpresa.setText(nombreEmpresa != null && !nombreEmpresa.isEmpty()
                    ? "Empresa: " + nombreEmpresa : "");
            txtNombreProyecto.setText(nombreProyecto != null && !nombreProyecto.isEmpty()
                    ? "Proyecto: " + nombreProyecto : "Proyecto sin nombre");
            fechaini.setText("Fecha Inicio: " + (fechaInicio != null && !fechaInicio.isEmpty()
                    ? fechaInicio : "No disponible"));
            fechafin.setText("Fecha Termino: " + (fechaFin != null && !fechaFin.isEmpty()
                    ? fechaFin : "No disponible"));
            txtCarreraEnfocada.setText("Carrera Enfocada: " + (carreraEnfocada != null && !carreraEnfocada.isEmpty()
                    ? carreraEnfocada : "No especificada"));
            telefono.setText("Teléfono: " + (telefonoEmpresa != null && !telefonoEmpresa.isEmpty()
                    ? telefonoEmpresa : "No disponible"));
            vacantes.setText("Vacantes: " + vacantesDisponibles);
            ubi.setText("Ubicación: " + (ubicacion != null && !ubicacion.isEmpty()
                    ? ubicacion : "No especificada"));
            obj.setText("Objetivo: " + (objetivo != null && !objetivo.isEmpty()
                    ? objetivo : "No especificado"));

            // Pintar imagen Base64 del proyecto si existe
            if (imagenProyecto != null && !imagenProyecto.isEmpty()) {
                try {
                    byte[] bytes = Base64.decode(imagenProyecto, Base64.DEFAULT);
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    if (bmp != null) {
                        imgLogoProyecto.setImageBitmap(bmp);
                    } else {
                        imgLogoProyecto.setImageResource(R.drawable.solinx_logo);
                    }
                } catch (Exception e) {
                    imgLogoProyecto.setImageResource(R.drawable.solinx_logo);
                }
            } else {
                imgLogoProyecto.setImageResource(R.drawable.solinx_logo);
            }
        }
    }

    /**
     * Busca el correo de la empresa usando el endpoint /empresa/{id}
     * que ahora devuelve el correo asociado al usuario de la empresa.
     */
    private void buscarCorreoEmpresa() {
        if (idEmpresa == null || idEmpresa == 0) return;

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.obtenerEmpresaPorId(idEmpresa).enqueue(new Callback<EmpresaDTO>() {
            @Override
            public void onResponse(Call<EmpresaDTO> call, Response<EmpresaDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    correoEmpresa = response.body().getCorreo();
                    Log.d(TAG, "Correo empresa obtenido: " + correoEmpresa);
                }
            }

            @Override
            public void onFailure(Call<EmpresaDTO> call, Throwable t) {
                Log.e(TAG, "No se pudo obtener correo empresa: " + t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnRegresar) {
            finish();
        } else if (id == R.id.btnEnviar) {
            enviarSolicitud();
        } else if (id == R.id.btnEnviarCorreo) {
            abrirCorreoEmpresa();
        }
    }

    private void abrirCorreoEmpresa() {
        String nombreEmpresa = getIntent().getStringExtra("nombreEmpresa");
        String nombreProyecto = getIntent().getStringExtra("nombreProyecto");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enviar correo a " + (nombreEmpresa != null ? nombreEmpresa : "Empresa"));

        final EditText input = new EditText(this);
        input.setHint("Escribe tu mensaje aquí...");
        input.setMinLines(3);
        input.setPadding(40, 20, 40, 20);
        builder.setView(input);

        builder.setPositiveButton("Enviar", (dialog, which) -> {
            String mensaje = input.getText().toString().trim();
            if (mensaje.isEmpty()) {
                Toast.makeText(this, "Escribe un mensaje", Toast.LENGTH_SHORT).show();
                return;
            }

            String destinatario = (correoEmpresa != null && !correoEmpresa.isEmpty()) ? correoEmpresa : "";

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:" + destinatario));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT,
                    "SoLinX - Consulta sobre proyecto: " + (nombreProyecto != null ? nombreProyecto : ""));
            emailIntent.putExtra(Intent.EXTRA_TEXT, mensaje);
            try {
                startActivity(Intent.createChooser(emailIntent, "Enviar correo con..."));
            } catch (Exception e) {
                Toast.makeText(this, "No hay app de correo disponible", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void enviarSolicitud() {
        if (boletaAlumno == null) {
            Toast.makeText(this, "Error: No se pudo obtener tu boleta", Toast.LENGTH_SHORT).show();
            return;
        }
        if (proyectoId == null || proyectoId == 0) {
            Toast.makeText(this, "Error: Proyecto no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        btnEnviar.setEnabled(false);
        btnEnviar.setText("ENVIANDO...");

        SolicitudDTO solicitudDTO = new SolicitudDTO();
        solicitudDTO.setBoletaAlumno(boletaAlumno);
        solicitudDTO.setIdProyecto(proyectoId);
        solicitudDTO.setEstadoSolicitud("enviada");
        solicitudDTO.setFechaSolicitud(null);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.enviarSolicitud(solicitudDTO).enqueue(new Callback<SolicitudDTO>() {
            @Override
            public void onResponse(Call<SolicitudDTO> call, Response<SolicitudDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AlumnoEnviarSolicitud.this,
                            "¡Solicitud enviada con éxito!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AlumnoEnviarSolicitud.this,
                            "Error al enviar la solicitud (Código: " + response.code() + ")",
                            Toast.LENGTH_LONG).show();
                    btnEnviar.setEnabled(true);
                    btnEnviar.setText("ENVIAR SOLICITUD");
                }
            }

            @Override
            public void onFailure(Call<SolicitudDTO> call, Throwable t) {
                Toast.makeText(AlumnoEnviarSolicitud.this,
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                btnEnviar.setEnabled(true);
                btnEnviar.setText("ENVIAR SOLICITUD");
            }
        });
    }
}

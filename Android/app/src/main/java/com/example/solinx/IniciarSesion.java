package com.example.solinx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class IniciarSesion extends AppCompatActivity implements View.OnClickListener {
    EditText nombre, contraseña;
    Button enviar;

    // URL para emulador - usa 10.0.2.2 para localhost
    private static final String BASE_URL = "http://192.168.21.36:8080/SoLinX/api" ;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_iniciar_sesion);

        nombre = findViewById(R.id.etUsuario);
        contraseña = findViewById(R.id.etContrasena);
        enviar = findViewById(R.id.btnEnviar);
        enviar.setOnClickListener(this);

        requestQueue = Volley.newRequestQueue(this);
    }

    @Override
    public void onClick(View view) {
        String usuario = nombre.getText().toString().trim();
        String contrasena = contraseña.getText().toString().trim();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos...", Toast.LENGTH_SHORT).show();
            return;
        }
        loginUser(usuario, contrasena);
    }

    private void loginUser(String usuario, String contrasena) {
        String url = BASE_URL + "/login";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("correo", usuario);
            jsonBody.put("userPassword", contrasena);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creando la solicitud", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Long idUsuario = response.getLong("idUsuario");
                            String nombre = response.getString("nombre");
                            String correo = response.getString("correo");
                            String rol = response.getString("rol");

                            Toast.makeText(IniciarSesion.this,
                                    "Bienvenido: " + nombre, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(IniciarSesion.this, AlumnoMenuEmpresas.class);

                            intent.putExtra("idUsuario", idUsuario);
                            intent.putExtra("nombre", nombre);
                            intent.putExtra("correo", correo);
                            intent.putExtra("rol", rol);

                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(IniciarSesion.this,
                                    "Error procesando respuesta del servidor", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            int statusCode = error.networkResponse.statusCode;
                            if (statusCode == 401) {
                                Toast.makeText(IniciarSesion.this,
                                        "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(IniciarSesion.this,
                                        "Error del servidor: " + statusCode, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(IniciarSesion.this,
                                    "Error de conexión: Verifica tu internet", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        requestQueue.add(request);
    }
}
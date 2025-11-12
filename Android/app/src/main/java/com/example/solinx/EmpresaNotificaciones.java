package com.example.solinx;
// LANDA CABALLERO ANGEL
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class EmpresaNotificaciones extends AppCompatActivity implements View.OnClickListener {
    TextView btnMenu, ad1, ad2, ad3, ne1, ne2, ne3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_empresa_notificaciones);

        //Declarar los textViews
        btnMenu = findViewById(R.id.btnMenu);

        //Declarar los setOnClickListener
        btnMenu.setOnClickListener(this);

        ad1 = findViewById(R.id.admitir);
        ad1.setOnClickListener(this);
        ad2 = findViewById(R.id.admitir2);
        ad2.setOnClickListener(this);
        ad3 = findViewById(R.id.admitir3);
        ad3.setOnClickListener(this);
        ne1 = findViewById(R.id.rechazar);
        ne1.setOnClickListener(this);
        ne2 = findViewById(R.id.rechazar2);
        ne2.setOnClickListener(this);
        ne3 = findViewById(R.id.rechazar3);
        ne3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnMenu) {
            Intent intent = new Intent(this, VistaEmpresas.class);
            startActivity(intent);
        } else if (id == R.id.admitir || id == R.id.admitir2 || id == R.id.admitir3) {
            String numeroSolicitud = "";
            if (id == R.id.admitir) numeroSolicitud = "#001";
            else if (id == R.id.admitir2) numeroSolicitud = "#002";
            else if (id == R.id.admitir3) numeroSolicitud = "#003";

            Toast.makeText(this, "Solicitud " + numeroSolicitud + " - Usuario Admitido ✅", Toast.LENGTH_SHORT).show();

            ((TextView)v).setTextColor(0xFF4CAF50);
            deshabilitarBotonesDeTarjeta(v);
        } else if (id == R.id.rechazar || id == R.id.rechazar2 || id == R.id.rechazar3) {
            String numeroSolicitud = "";
            if (id == R.id.rechazar) numeroSolicitud = "#001";
            else if (id == R.id.rechazar2) numeroSolicitud = "#002";
            else if (id == R.id.rechazar3) numeroSolicitud = "#003";

            Toast.makeText(this, "Solicitud " + numeroSolicitud + " - Usuario Rechazado ❌", Toast.LENGTH_SHORT).show();

            ((TextView)v).setTextColor(0xFFD32F2F);
            deshabilitarBotonesDeTarjeta(v);
        }
    }

    // Método opcional para deshabilitar los botones de una tarjeta después de tomar una decisión
    private void deshabilitarBotonesDeTarjeta(View botonPresionado) {
        View parent = (View) botonPresionado.getParent();

        if (parent != null && parent instanceof View) {
            // Deshabilitar todos los botones en el mismo LinearLayout
            for (int i = 0; i < ((android.view.ViewGroup) parent).getChildCount(); i++) {
                View child = ((android.view.ViewGroup) parent).getChildAt(i);
                if (child instanceof TextView) {
                    child.setClickable(false);
                    child.setFocusable(false);
                    ((TextView) child).setTextColor(0xFF9E9E9E);
                }
            }
        }
    }
}

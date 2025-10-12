package com.example.solinx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button MenuEmpresas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        MenuEmpresas = findViewById(R.id.MenuEmpresas);
        MenuEmpresas.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intento = new Intent(this, MenuEmpresas.class);
        startActivity(intento);
    }
}
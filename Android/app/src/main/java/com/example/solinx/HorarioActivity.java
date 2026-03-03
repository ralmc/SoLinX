package com.example.solinx;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class HorarioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horario_activity);

        int boleta = getIntent().getIntExtra("boleta", -1);

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
package com.example.solinx;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

//VELAZQUEZ REYNOSO ADRIAN
public class SupervisorSolicitudesAlumnoActivity extends AppCompatActivity {

    RecyclerView recyclerViewSolicitudes;
    SolicitudesAlumnoAdapter adapter;
    List<Solicitud> miListaDeSolicitudes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lista_solicitudes_alumnos);

        recyclerViewSolicitudes = findViewById(R.id.recycler_view_solicitudes);
        miListaDeSolicitudes = new ArrayList<>();
        miListaDeSolicitudes.add(new Solicitud("001", "Adrian Velazquez", "2024001", "Solinx", "Desarrollo Android", "Computación", "Matutino"));
        miListaDeSolicitudes.add(new Solicitud("002", "Ana García", "2024002", "Google", "Testing QA", "Informática", "Vespertino"));
        miListaDeSolicitudes.add(new Solicitud("003", "Juan Pérez", "2024003", "Microsoft", "Diseño BD", "Computación", "Mixto"));

        adapter = new SolicitudesAlumnoAdapter(this, miListaDeSolicitudes);
        recyclerViewSolicitudes.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSolicitudes.setAdapter(adapter);
    }
}
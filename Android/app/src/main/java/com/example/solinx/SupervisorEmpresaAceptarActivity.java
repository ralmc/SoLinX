package com.example.solinx;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

//VELAZQUEZ REYNOSO ADRIAN
public class SupervisorEmpresaAceptarActivity extends AppCompatActivity {

    RecyclerView recyclerViewAceptaciones;
    AceptacionesEmpresaAdapter adapter;
    List<Solicitud> miListaDeAceptaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_empresas_lista_aceptaciones);

        recyclerViewAceptaciones = findViewById(R.id.recycler_view_aceptaciones);

        miListaDeAceptaciones = new ArrayList<>();
        miListaDeAceptaciones.add(new Solicitud("001", "Adrian Velazquez", "2024001", "Solinx", "Desarrollo Android", "Computación", "13/11/2025"));
        miListaDeAceptaciones.add(new Solicitud("002", "Ana García", "2024002", "Google", "Testing QA", "Informática", "14/11/2025"));

        // 3. Configura el nuevo adapter
        adapter = new AceptacionesEmpresaAdapter(this, miListaDeAceptaciones);
        recyclerViewAceptaciones.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAceptaciones.setAdapter(adapter);
    }
}
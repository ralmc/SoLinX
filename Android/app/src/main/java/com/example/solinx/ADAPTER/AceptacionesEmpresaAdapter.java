package com.example.solinx.ADAPTER;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solinx.R;
import com.example.solinx.Solicitudes;

import java.util.List;

public class AceptacionesEmpresaAdapter extends RecyclerView.Adapter<AceptacionesEmpresaAdapter.ViewHolder> {

    private final List<Solicitudes> lista;
    private final Context context;

    public AceptacionesEmpresaAdapter(Context context, List<Solicitudes> lista) {
        this.context = context;
        this.lista   = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.activity_card_aceptacion_empresa, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Solicitudes s = lista.get(position);

        holder.tvNombreAlumno.setText(s.getNombreEstudiante() != null ? s.getNombreEstudiante() : "N/A");
        holder.tvCorreoAlumno.setText(s.getCorreoEstudiante() != null ? s.getCorreoEstudiante() : "N/A");
        holder.tvCarreraAlumno.setText("Carrera: " + (s.getCarrera() != null ? s.getCarrera() : "N/A"));

        // Detalles expandibles
        holder.tvNombreEmpresa.setText("Empresa: " + (s.getNombreEmpresa() != null ? s.getNombreEmpresa() : "N/A"));
        holder.tvNombreProyecto.setText("Proyecto: " + (s.getNombreProyecto() != null ? s.getNombreProyecto() : "N/A"));
        holder.tvFechaAceptacion.setText("Aceptado: " + (s.getFechaAceptacion() != null && !s.getFechaAceptacion().isEmpty() ? s.getFechaAceptacion() : "N/A"));

        holder.btnExpandir.setOnClickListener(v -> {
            boolean visible = holder.layoutExpandible.getVisibility() == View.VISIBLE;
            holder.layoutExpandible.setVisibility(visible ? View.GONE : View.VISIBLE);
            holder.btnExpandir.setRotation(visible ? 0f : 180f);
        });
    }

    @Override
    public int getItemCount() { return lista.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView     tvNombreAlumno, tvCorreoAlumno, tvCarreraAlumno;
        TextView     tvNombreEmpresa, tvNombreProyecto, tvFechaAceptacion;
        ImageButton  btnExpandir;
        LinearLayout layoutExpandible;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreAlumno    = itemView.findViewById(R.id.tv_nombre_alumno);
            tvCorreoAlumno    = itemView.findViewById(R.id.tv_correo_alumno);
            tvCarreraAlumno   = itemView.findViewById(R.id.tv_carrera_alumno);
            tvNombreEmpresa   = itemView.findViewById(R.id.tv_nombre_empresa);
            tvNombreProyecto  = itemView.findViewById(R.id.tv_nombre_proyecto);
            tvFechaAceptacion = itemView.findViewById(R.id.tv_fecha_aceptacion);
            btnExpandir       = itemView.findViewById(R.id.btn_expandir_detalles);
            layoutExpandible  = itemView.findViewById(R.id.layout_detalles_expandibles);
        }
    }
}
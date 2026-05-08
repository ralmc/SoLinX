package com.example.solinx.ADAPTER;

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

public class SolicitudesAlumnoAdapter extends RecyclerView.Adapter<SolicitudesAlumnoAdapter.ViewHolder> {

    private final List<Solicitudes> solicitudes;
    private final OnClickListener listener;

    public interface OnClickListener {
        void onAprobar(Solicitudes s);
        void onRechazar(Solicitudes s);
        void onEnviarCorreo(Solicitudes s);
    }

    public SolicitudesAlumnoAdapter(List<Solicitudes> solicitudes, OnClickListener listener) {
        this.solicitudes = solicitudes;
        this.listener    = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_card_solicitud_alumno, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Solicitudes s = solicitudes.get(position);

        // ─── Info siempre visible ──────────────────────────────
        holder.tvNombreAlumno.setText(s.getNombreEstudiante() != null ? s.getNombreEstudiante() : "N/A");
        holder.tvCorreoAlumno.setText(s.getCorreoEstudiante() != null ? s.getCorreoEstudiante() : "N/A");
        holder.tvCarreraAlumno.setText("Carrera: " + (s.getCarrera() != null ? s.getCarrera() : "N/A"));

        // ─── Info expandible ───────────────────────────────────
        holder.tvCarreraEnfocada.setText("Carrera enfocada: " + (s.getCarreraEnfocada() != null ? s.getCarreraEnfocada() : "N/A"));
        holder.tvNombreProyecto.setText("Proyecto: " + (s.getNombreProyecto() != null ? s.getNombreProyecto() : "N/A"));
        holder.tvNombreEmpresa.setText("Empresa: " + (s.getNombreEmpresa() != null ? s.getNombreEmpresa() : "N/A"));

        // ─── Toggle expandir ───────────────────────────────────
        holder.btnExpandir.setOnClickListener(v -> {
            boolean visible = holder.layoutDetallesExpandibles.getVisibility() == View.VISIBLE;
            holder.layoutDetallesExpandibles.setVisibility(visible ? View.GONE : View.VISIBLE);
            holder.btnExpandir.setRotation(visible ? 0f : 180f);
        });

        holder.btnAprobar.setOnClickListener(v -> listener.onAprobar(s));
        holder.btnRechazar.setOnClickListener(v -> listener.onRechazar(s));
        holder.btnEnviarCorreo.setOnClickListener(v -> listener.onEnviarCorreo(s));
    }

    @Override
    public int getItemCount() { return solicitudes.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView     tvNombreAlumno, tvCorreoAlumno, tvCarreraAlumno;
        TextView     tvCarreraEnfocada, tvNombreProyecto, tvNombreEmpresa;
        TextView     btnAprobar, btnRechazar, btnEnviarCorreo;
        ImageButton  btnExpandir;
        LinearLayout layoutDetallesExpandibles;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreAlumno          = itemView.findViewById(R.id.tv_nombre_alumno);
            tvCorreoAlumno          = itemView.findViewById(R.id.tv_correo_alumno);
            tvCarreraAlumno         = itemView.findViewById(R.id.tv_carrera_alumno);
            tvCarreraEnfocada       = itemView.findViewById(R.id.tv_carrera_enfocada);
            tvNombreProyecto        = itemView.findViewById(R.id.tv_nombre_proyecto);
            tvNombreEmpresa         = itemView.findViewById(R.id.tv_nombre_empresa);
            btnAprobar              = itemView.findViewById(R.id.btn_aprobar);
            btnRechazar             = itemView.findViewById(R.id.btn_rechazar);
            btnEnviarCorreo         = itemView.findViewById(R.id.btn_enviar_correo);
            btnExpandir             = itemView.findViewById(R.id.btn_expandir_detalles);
            layoutDetallesExpandibles = itemView.findViewById(R.id.layout_detalles_expandibles);
        }
    }
}
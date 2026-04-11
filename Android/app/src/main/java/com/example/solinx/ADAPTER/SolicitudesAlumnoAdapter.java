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
        this.listener = listener;
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

        holder.tvSolicitudId.setText("Solicitud #" + s.getIdSolicitud());
        holder.tvNombreAlumno.setText("Alumno: " + (s.getNombreEstudiante() != null ? s.getNombreEstudiante() : "N/A"));
        holder.tvBoletaAlumno.setText("Boleta: " + s.getBoleta());
        holder.tvNombreEmpresa.setText("Empresa: " + (s.getNombreEmpresa() != null ? s.getNombreEmpresa() : "N/A"));

        // Detalles expandibles
        holder.tvDetalleProyecto.setText("Proyecto: " + (s.getNombreProyecto() != null ? s.getNombreProyecto() : "N/A"));
        holder.tvDetalleObjetivos.setText("Carrera: " + (s.getCarrera() != null ? s.getCarrera() : "N/A"));
        holder.tvDetalleHoras.setText("Escuela: " + (s.getEscuela() != null ? s.getEscuela() : "N/A"));

        // Toggle expandir/contraer
        holder.btnExpandir.setOnClickListener(v -> {
            boolean visible = holder.layoutDetallesExpandibles.getVisibility() == View.VISIBLE;
            holder.layoutDetallesExpandibles.setVisibility(visible ? View.GONE : View.VISIBLE);
        });

        holder.btnAprobar.setOnClickListener(v -> listener.onAprobar(s));
        holder.btnRechazar.setOnClickListener(v -> listener.onRechazar(s));
        holder.btnEnviarCorreo.setOnClickListener(v -> listener.onEnviarCorreo(s));
    }

    @Override
    public int getItemCount() {
        return solicitudes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSolicitudId, tvNombreAlumno, tvBoletaAlumno, tvNombreEmpresa;
        TextView tvDetalleProyecto, tvDetalleObjetivos, tvDetalleHoras;
        TextView btnAprobar, btnRechazar, btnEnviarCorreo;
        ImageButton btnExpandir;
        LinearLayout layoutDetallesExpandibles;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSolicitudId       = itemView.findViewById(R.id.tv_solicitud_id);
            tvNombreAlumno      = itemView.findViewById(R.id.tv_nombre_alumno);
            tvBoletaAlumno      = itemView.findViewById(R.id.tv_boleta_alumno);
            tvNombreEmpresa     = itemView.findViewById(R.id.tv_nombre_empresa);
            tvDetalleProyecto   = itemView.findViewById(R.id.tv_detalle_proyecto);
            tvDetalleObjetivos  = itemView.findViewById(R.id.tv_detalle_objetivos);
            tvDetalleHoras      = itemView.findViewById(R.id.tv_detalle_horas);
            btnAprobar          = itemView.findViewById(R.id.btn_aprobar);
            btnRechazar         = itemView.findViewById(R.id.btn_rechazar);
            btnEnviarCorreo     = itemView.findViewById(R.id.btn_enviar_correo);
            btnExpandir         = itemView.findViewById(R.id.btn_expandir_detalles);
            layoutDetallesExpandibles = itemView.findViewById(R.id.layout_detalles_expandibles);
        }
    }
}

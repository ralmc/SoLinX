package com.example.solinx.ADAPTER;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solinx.R;
import com.example.solinx.Solicitudes;  // ← CAMBIO AQUÍ

import java.util.List;

public class SolicitudesAdapter extends RecyclerView.Adapter<SolicitudesAdapter.SolicitudViewHolder> {

    private List<Solicitudes> solicitudes;  // ← CAMBIO AQUÍ
    private OnSolicitudClickListener listener;

    public interface OnSolicitudClickListener {
        void onAprobar(Solicitudes solicitud);  // ← CAMBIO AQUÍ
        void onRechazar(Solicitudes solicitud);  // ← CAMBIO AQUÍ
    }

    public SolicitudesAdapter(List<Solicitudes> solicitudes, OnSolicitudClickListener listener) {  // ← CAMBIO AQUÍ
        this.solicitudes = solicitudes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SolicitudViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_solicitud, parent, false);
        return new SolicitudViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SolicitudViewHolder holder, int position) {
        Solicitudes solicitud = solicitudes.get(position);  // ← CAMBIO AQUÍ

        holder.tvNombreEstudiante.setText(solicitud.getNombreEstudiante());
        holder.tvBoleta.setText("Boleta: " + solicitud.getBoleta());
        holder.tvCarrera.setText(solicitud.getCarrera());
        holder.tvProyecto.setText("Proyecto: " + solicitud.getNombreProyecto());

        holder.btnAprobar.setOnClickListener(v -> listener.onAprobar(solicitud));
        holder.btnRechazar.setOnClickListener(v -> listener.onRechazar(solicitud));
    }

    @Override
    public int getItemCount() {
        return solicitudes.size();
    }

    static class SolicitudViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreEstudiante, tvBoleta, tvCarrera, tvProyecto;
        Button btnAprobar, btnRechazar;

        public SolicitudViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreEstudiante = itemView.findViewById(R.id.tv_nombre_estudiante);
            tvBoleta = itemView.findViewById(R.id.tv_boleta);
            tvCarrera = itemView.findViewById(R.id.tv_carrera);
            tvProyecto = itemView.findViewById(R.id.tv_proyecto);
            btnAprobar = itemView.findViewById(R.id.btn_aprobar);
            btnRechazar = itemView.findViewById(R.id.btn_rechazar);
        }
    }
}
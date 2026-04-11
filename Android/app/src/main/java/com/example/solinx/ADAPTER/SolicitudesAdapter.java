package com.example.solinx.ADAPTER;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solinx.R;
import com.example.solinx.Solicitudes;

import java.util.List;

public class SolicitudesAdapter extends RecyclerView.Adapter<SolicitudesAdapter.SolicitudViewHolder> {

    private final List<Solicitudes> solicitudes;
    private final OnSolicitudClickListener listener;

    public interface OnSolicitudClickListener {
        void onAprobar(Solicitudes solicitud);
        void onRechazar(Solicitudes solicitud);
        default void onEnviarCorreo(Solicitudes solicitud) {}
    }

    public SolicitudesAdapter(List<Solicitudes> solicitudes, OnSolicitudClickListener listener) {
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
        Solicitudes solicitud = solicitudes.get(position);

        holder.tvNombreEstudiante.setText(solicitud.getNombreEstudiante());
        holder.tvBoleta.setText("Boleta: " + solicitud.getBoleta());
        holder.tvCarrera.setText(solicitud.getCarrera());
        holder.tvProyecto.setText("Proyecto: " + solicitud.getNombreProyecto());

        // ─── Traducir estado a texto amigable para la empresa ───
        String estado = solicitud.getEstadoSolicitud();
        String estadoAmigable;
        boolean mostrarBotones;

        if (estado == null) {
            estadoAmigable = "Estado: Pendiente";
            mostrarBotones = true;
        } else {
            switch (estado.toLowerCase()) {
                case "aprobada_supervisor":
                    // Empresa puede decidir: mostrar botones
                    estadoAmigable = "Estado: Pendiente de tu revisión";
                    mostrarBotones = true;
                    break;
                case "aceptada":
                    // Ya la aceptó la empresa, espera visto bueno del supervisor
                    estadoAmigable = "Estado: Aprobada por ti";
                    mostrarBotones = false;
                    break;
                case "rechazada_empresa":
                    estadoAmigable = "Estado: Rechazada por ti";
                    mostrarBotones = false;
                    break;
                case "aprobada":
                    estadoAmigable = "Estado: Confirmada oficialmente";
                    mostrarBotones = false;
                    break;
                case "rechazada":
                case "rechazada_supervisor":
                    estadoAmigable = "Estado: Rechazada";
                    mostrarBotones = false;
                    break;
                default:
                    estadoAmigable = "Estado: " + estado;
                    mostrarBotones = true;
                    break;
            }
        }

        // Pintar el estado amigable en el TextView del proyecto (añadido al final)
        holder.tvProyecto.setText("Proyecto: " + solicitud.getNombreProyecto() + "\n" + estadoAmigable);

        // Mostrar/ocultar botones de acción
        if (mostrarBotones) {
            holder.btnAprobar.setVisibility(View.VISIBLE);
            holder.btnRechazar.setVisibility(View.VISIBLE);
            holder.btnAprobar.setOnClickListener(v -> listener.onAprobar(solicitud));
            holder.btnRechazar.setOnClickListener(v -> listener.onRechazar(solicitud));
        } else {
            holder.btnAprobar.setVisibility(View.GONE);
            holder.btnRechazar.setVisibility(View.GONE);
        }

        // Botón de correo siempre visible
        if (holder.btnEnviarCorreo != null) {
            holder.btnEnviarCorreo.setVisibility(View.VISIBLE);
            holder.btnEnviarCorreo.setOnClickListener(v -> listener.onEnviarCorreo(solicitud));
        }
    }

    @Override
    public int getItemCount() {
        return solicitudes.size();
    }

    static class SolicitudViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreEstudiante, tvBoleta, tvCarrera, tvProyecto;
        Button btnAprobar, btnRechazar;
        TextView btnEnviarCorreo;

        public SolicitudViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreEstudiante = itemView.findViewById(R.id.tv_nombre_estudiante);
            tvBoleta = itemView.findViewById(R.id.tv_boleta);
            tvCarrera = itemView.findViewById(R.id.tv_carrera);
            tvProyecto = itemView.findViewById(R.id.tv_proyecto);
            btnAprobar = itemView.findViewById(R.id.btn_aprobar);
            btnRechazar = itemView.findViewById(R.id.btn_rechazar);
            btnEnviarCorreo = itemView.findViewById(R.id.btn_enviar_correo);
        }
    }
}

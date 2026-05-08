package com.example.solinx.ADAPTER;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solinx.R;
import com.example.solinx.RESPONSE.ProyectoResponse;

import java.util.List;

public class ProyectoSupervisorAdapter extends RecyclerView.Adapter<ProyectoSupervisorAdapter.ViewHolder> {

    public interface OnClickListener {
        void onAprobar(ProyectoResponse p);
        void onRechazar(ProyectoResponse p);
    }

    private final List<ProyectoResponse> lista;
    private final OnClickListener listener;

    public ProyectoSupervisorAdapter(List<ProyectoResponse> lista, OnClickListener listener) {
        this.lista    = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_proyecto_supervisor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProyectoResponse p = lista.get(position);

        holder.tvNombreProyecto.setText(p.getNombreProyecto() != null ? p.getNombreProyecto() : "N/A");
        holder.tvNombreEmpresa.setText("Empresa: " + (p.getNombreEmpresa() != null ? p.getNombreEmpresa() : "N/A"));
        holder.tvCarrera.setText("Carrera: " + (p.getCarreraEnfocada() != null ? p.getCarreraEnfocada() : "N/A"));
        holder.tvVacantes.setText("Vacantes: " + (p.getVacantes() != null ? p.getVacantes() : "N/A"));
        holder.tvUbicacion.setText("Ubicación: " + (p.getUbicacion() != null ? p.getUbicacion() : "N/A"));

        // ─── Toggle expandir ───────────────────────────────────
        holder.btnExpandir.setOnClickListener(v -> {
            boolean visible = holder.layoutExpandible.getVisibility() == View.VISIBLE;
            holder.layoutExpandible.setVisibility(visible ? View.GONE : View.VISIBLE);
            holder.btnExpandir.setRotation(visible ? 0f : 180f);
        });

        // ─── Badge estado ──────────────────────────────────────
        String estado = p.getEstadoProyecto() != null ? p.getEstadoProyecto() : "pendiente";
        switch (estado) {
            case "aprobado":
                holder.tvEstadoBadge.setText("✓ Aprobado");
                holder.tvEstadoBadge.setBackgroundTintList(ColorStateList.valueOf(0xFF38A169));
                holder.btnAprobar.setVisibility(View.GONE);
                holder.btnRechazar.setVisibility(View.GONE);
                break;
            case "rechazado":
                holder.tvEstadoBadge.setText("✗ Rechazado");
                holder.tvEstadoBadge.setBackgroundTintList(ColorStateList.valueOf(0xFFE53E3E));
                holder.btnAprobar.setVisibility(View.VISIBLE);
                holder.btnRechazar.setVisibility(View.GONE);
                break;
            default:
                holder.tvEstadoBadge.setText("⏳ Pendiente");
                holder.tvEstadoBadge.setBackgroundTintList(ColorStateList.valueOf(0xFFD69E2E));
                holder.btnAprobar.setVisibility(View.VISIBLE);
                holder.btnRechazar.setVisibility(View.VISIBLE);
                break;
        }

        holder.btnAprobar.setOnClickListener(v -> listener.onAprobar(p));
        holder.btnRechazar.setOnClickListener(v -> listener.onRechazar(p));
    }

    @Override
    public int getItemCount() { return lista.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView     tvNombreProyecto, tvNombreEmpresa, tvCarrera;
        TextView     tvVacantes, tvUbicacion, tvEstadoBadge;
        TextView     btnAprobar, btnRechazar;
        ImageButton  btnExpandir;
        LinearLayout layoutExpandible;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreProyecto = itemView.findViewById(R.id.tv_nombre_proyecto);
            tvNombreEmpresa  = itemView.findViewById(R.id.tv_nombre_empresa);
            tvCarrera        = itemView.findViewById(R.id.tv_carrera);
            tvVacantes       = itemView.findViewById(R.id.tv_vacantes);
            tvUbicacion      = itemView.findViewById(R.id.tv_ubicacion);
            tvEstadoBadge    = itemView.findViewById(R.id.tv_estado_badge);
            btnAprobar       = itemView.findViewById(R.id.btn_aprobar);
            btnRechazar      = itemView.findViewById(R.id.btn_rechazar);
            btnExpandir      = itemView.findViewById(R.id.btn_expandir_detalles);
            layoutExpandible = itemView.findViewById(R.id.layout_detalles_expandibles);
        }
    }
}
package com.example.solinx.ADAPTER;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solinx.DTO.DocumentoDTO;
import com.example.solinx.R;
import com.example.solinx.Solicitudes;

import java.util.List;

public class DocumentoSupervisorAdapter extends RecyclerView.Adapter<DocumentoSupervisorAdapter.ViewHolder> {

    public static class DocumentoItem {
        public final Solicitudes solicitud;
        public final DocumentoDTO doc;

        public DocumentoItem(Solicitudes solicitud, DocumentoDTO doc) {
            this.solicitud = solicitud;
            this.doc       = doc;
        }
    }

    public interface OnClickListener {
        void onAprobar(DocumentoItem item);
        void onRechazar(DocumentoItem item);
    }

    private final List<DocumentoItem> items;
    private final OnClickListener listener;

    public DocumentoSupervisorAdapter(List<DocumentoItem> items, OnClickListener listener) {
        this.items    = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_documento_supervisor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentoItem item = items.get(position);
        Solicitudes sol    = item.solicitud;
        DocumentoDTO doc   = item.doc;

        holder.tvNombreAlumno.setText(sol.getNombreEstudiante() != null ? sol.getNombreEstudiante() : "N/A");
        holder.tvCorreoAlumno.setText(sol.getCorreoEstudiante() != null ? sol.getCorreoEstudiante() : "N/A");
        holder.tvNombreProyecto.setText("Proyecto: " + (sol.getNombreProyecto() != null ? sol.getNombreProyecto() : "N/A"));
        holder.tvPeriodo.setText("Periodo " + doc.getPeriodo());
        holder.tvNombreArchivo.setText(doc.getNombreArchivo() != null ? doc.getNombreArchivo() : "documento.pdf");
        holder.tvFecha.setText(doc.getFechaSubida() != null ? doc.getFechaSubida() : "");

        // Estado badge
        String estado = doc.getEstadoDocumento() != null ? doc.getEstadoDocumento() : "pendiente";
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
                holder.tvEstadoBadge.setText("⏳ En revisión");
                holder.tvEstadoBadge.setBackgroundTintList(ColorStateList.valueOf(0xFFD69E2E));
                holder.btnAprobar.setVisibility(View.VISIBLE);
                holder.btnRechazar.setVisibility(View.VISIBLE);
                break;
        }

        holder.btnAprobar.setOnClickListener(v -> listener.onAprobar(item));
        holder.btnRechazar.setOnClickListener(v -> listener.onRechazar(item));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreAlumno, tvCorreoAlumno, tvNombreProyecto;
        TextView tvPeriodo, tvNombreArchivo, tvFecha, tvEstadoBadge;
        TextView btnAprobar, btnRechazar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreAlumno  = itemView.findViewById(R.id.tv_nombre_alumno);
            tvCorreoAlumno  = itemView.findViewById(R.id.tv_correo_alumno);
            tvNombreProyecto= itemView.findViewById(R.id.tv_nombre_proyecto);
            tvPeriodo       = itemView.findViewById(R.id.tv_periodo);
            tvNombreArchivo = itemView.findViewById(R.id.tv_nombre_archivo);
            tvFecha         = itemView.findViewById(R.id.tv_fecha);
            tvEstadoBadge   = itemView.findViewById(R.id.tv_estado_badge);
            btnAprobar      = itemView.findViewById(R.id.btn_aprobar);
            btnRechazar     = itemView.findViewById(R.id.btn_rechazar);
        }
    }
}
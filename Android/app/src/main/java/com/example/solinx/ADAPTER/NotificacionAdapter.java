package com.example.solinx.ADAPTER;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solinx.DTO.NotificacionDTO;
import com.example.solinx.R;

import java.util.ArrayList;
import java.util.List;

public class NotificacionAdapter extends RecyclerView.Adapter<NotificacionAdapter.ViewHolder> {

    private final Context context;
    private List<NotificacionDTO> notificaciones = new ArrayList<>();
    private OnNotificacionClickListener listener;

    public interface OnNotificacionClickListener {
        void onNotificacionClick(NotificacionDTO notificacion);
    }

    public NotificacionAdapter(Context context, OnNotificacionClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setNotificaciones(List<NotificacionDTO> lista) {
        this.notificaciones = lista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.card_notificacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificacionDTO n = notificaciones.get(position);

        holder.tvTitulo.setText(n.getTitulo());
        holder.tvMensaje.setText(n.getMensaje());
        holder.tvFecha.setText(n.getFechaCreacion() != null ? n.getFechaCreacion() : "");

        // Indicador visual de no leída
        holder.indicadorNoLeida.setVisibility(
                Boolean.FALSE.equals(n.getLeida()) ? View.VISIBLE : View.INVISIBLE
        );

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onNotificacionClick(n);
        });
    }

    @Override
    public int getItemCount() { return notificaciones.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvMensaje, tvFecha;
        View indicadorNoLeida;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo        = itemView.findViewById(R.id.tvTitulo);
            tvMensaje       = itemView.findViewById(R.id.tvMensaje);
            tvFecha         = itemView.findViewById(R.id.tvFecha);
            indicadorNoLeida = itemView.findViewById(R.id.indicadorNoLeida);
        }
    }
}

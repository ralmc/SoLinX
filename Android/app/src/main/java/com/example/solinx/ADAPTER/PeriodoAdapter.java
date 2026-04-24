package com.example.solinx.ADAPTER;

import android.content.res.ColorStateList;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solinx.DTO.DocumentoDTO;
import com.example.solinx.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PeriodoAdapter extends RecyclerView.Adapter<PeriodoAdapter.PeriodoViewHolder> {

    public interface OnSubirClickListener {
        void onSubir(int periodo);
    }

    public interface OnConfirmarClickListener {
        void onConfirmar(int periodo, Uri uri);
    }

    private final List<DocumentoDTO> periodos;
    private final OnSubirClickListener listenerSubir;
    private OnConfirmarClickListener listenerConfirmar;
    private final int periodoDesbloqueado;

    private final Map<Integer, Uri> urisTemp = new HashMap<>();
    private final Map<Integer, String> nombresTemp = new HashMap<>();

    public PeriodoAdapter(List<DocumentoDTO> periodos,
                          int periodoDesbloqueado,
                          OnSubirClickListener listenerSubir) {
        this.periodos            = periodos;
        this.periodoDesbloqueado = periodoDesbloqueado;
        this.listenerSubir       = listenerSubir;
    }

    public void setOnConfirmarClickListener(OnConfirmarClickListener listener) {
        this.listenerConfirmar = listener;
    }

    public void mostrarPrevia(int periodo, Uri uri, String nombreArchivo) {
        urisTemp.put(periodo, uri);
        nombresTemp.put(periodo, nombreArchivo);
        notifyDataSetChanged();
    }

    public void limpiarPrevia(int periodo) {
        urisTemp.remove(periodo);
        nombresTemp.remove(periodo);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PeriodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_periodo, parent, false);
        return new PeriodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PeriodoViewHolder holder, int position) {
        int numeroPeriodo = position + 1;
        DocumentoDTO doc  = periodos.get(position);
        Uri uriTemp       = urisTemp.get(numeroPeriodo);
        String nombreTemp = nombresTemp.get(numeroPeriodo);

        holder.tvPeriodo.setText("Periodo " + numeroPeriodo);

        // ─── Ya subido ─────────────────────────────────────
        if (doc != null) {
            holder.tvEstado.setText(doc.getNombreArchivo());
            holder.tvEstado.setVisibility(View.VISIBLE);
            holder.btnSubir.setVisibility(View.GONE);
            holder.layoutPrevia.setVisibility(View.GONE);
            setCardEstado(holder, true);

            // ─── Tiene previa pendiente de confirmar ───────────
        } else if (uriTemp != null) {
            holder.tvEstado.setVisibility(View.GONE);
            holder.btnSubir.setVisibility(View.GONE);
            holder.layoutPrevia.setVisibility(View.VISIBLE);
            holder.tvNombreArchivo.setText("📄 " + (nombreTemp != null ? nombreTemp : "archivo.pdf"));
            setCardEstado(holder, true);

            holder.btnConfirmar.setOnClickListener(v -> {
                if (listenerConfirmar != null) {
                    listenerConfirmar.onConfirmar(numeroPeriodo, uriTemp);
                }
            });

            holder.btnCambiar.setOnClickListener(v -> {
                listenerSubir.onSubir(numeroPeriodo);
            });

            // ─── Período desbloqueado sin archivo ──────────────
        } else if (numeroPeriodo == periodoDesbloqueado) {
            holder.tvEstado.setVisibility(View.GONE);
            holder.layoutPrevia.setVisibility(View.GONE);
            holder.btnSubir.setVisibility(View.VISIBLE);
            holder.btnSubir.setEnabled(true);
            holder.btnSubir.setOnClickListener(v -> listenerSubir.onSubir(numeroPeriodo));
            setCardEstado(holder, true);

            // ─── Período bloqueado ─────────────────────────────
        } else {
            holder.tvEstado.setVisibility(View.GONE);
            holder.layoutPrevia.setVisibility(View.GONE);
            holder.btnSubir.setVisibility(View.VISIBLE);
            holder.btnSubir.setEnabled(false);
            setCardEstado(holder, false);
        }
    }

    private void setCardEstado(PeriodoViewHolder holder, boolean activo) {
        if (activo) {
            holder.itemView.setAlpha(1.0f);
            holder.btnSubir.setBackgroundTintList(ColorStateList.valueOf(0xFF1497B9));
        } else {
            holder.itemView.setAlpha(0.35f);
            holder.btnSubir.setBackgroundTintList(ColorStateList.valueOf(0xFFAAAAAA));
        }
    }

    @Override
    public int getItemCount() {
        return periodos.size();
    }

    static class PeriodoViewHolder extends RecyclerView.ViewHolder {
        TextView    tvPeriodo, tvEstado, tvNombreArchivo;
        Button      btnSubir, btnConfirmar, btnCambiar;
        LinearLayout layoutPrevia;

        PeriodoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPeriodo       = itemView.findViewById(R.id.tvPeriodo);
            tvEstado        = itemView.findViewById(R.id.tvEstado);
            btnSubir        = itemView.findViewById(R.id.btnSubir);
            layoutPrevia    = itemView.findViewById(R.id.layoutPrevia);
            tvNombreArchivo = itemView.findViewById(R.id.tvNombreArchivo);
            btnConfirmar    = itemView.findViewById(R.id.btnConfirmar);
            btnCambiar      = itemView.findViewById(R.id.btnCambiar);
        }
    }
}
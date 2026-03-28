package com.example.solinx.ADAPTER;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solinx.DTO.DocumentoDTO;
import com.example.solinx.R;

import java.util.List;

public class PeriodoAdapter extends RecyclerView.Adapter<PeriodoAdapter.PeriodoViewHolder> {

    public interface OnSubirClickListener {
        void onSubir(int periodo);
    }

    private final List<DocumentoDTO> periodos;
    private final OnSubirClickListener listener;
    private final int periodoDesbloqueado;

    public PeriodoAdapter(List<DocumentoDTO> periodos,
                          int periodoDesbloqueado,
                          OnSubirClickListener listener) {
        this.periodos             = periodos;
        this.periodoDesbloqueado  = periodoDesbloqueado;
        this.listener             = listener;
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

        holder.tvPeriodo.setText("Periodo " + numeroPeriodo);

        if (doc != null) {
            holder.tvEstado.setText(doc.getNombreArchivo());
            holder.tvEstado.setVisibility(View.VISIBLE);
            holder.btnSubir.setVisibility(View.GONE);

            setCardEstado(holder, true);

        } else if (numeroPeriodo == periodoDesbloqueado) {
            holder.tvEstado.setVisibility(View.GONE);
            holder.btnSubir.setVisibility(View.VISIBLE);
            holder.btnSubir.setEnabled(true);
            holder.btnSubir.setOnClickListener(v -> listener.onSubir(numeroPeriodo));

            setCardEstado(holder, true);

        } else {
            holder.tvEstado.setVisibility(View.GONE);
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
        TextView tvPeriodo, tvEstado;
        Button   btnSubir;

        PeriodoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPeriodo = itemView.findViewById(R.id.tvPeriodo);
            tvEstado  = itemView.findViewById(R.id.tvEstado);
            btnSubir  = itemView.findViewById(R.id.btnSubir);
        }
    }
}
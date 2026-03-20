package com.example.solinx.ADAPTER;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

    public PeriodoAdapter(List<DocumentoDTO> periodos, OnSubirClickListener listener) {
        this.periodos = periodos;
        this.listener = listener;
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
        DocumentoDTO doc = periodos.get(position);

        holder.tvPeriodo.setText("Periodo " + numeroPeriodo);

        if (doc != null) {
            // Ya tiene documento
            holder.tvEstado.setText(doc.getNombreArchivo());
            holder.tvEstado.setVisibility(View.VISIBLE);
            holder.btnSubir.setVisibility(View.GONE);
        } else {
            // Sin documento
            holder.tvEstado.setVisibility(View.GONE);
            holder.btnSubir.setVisibility(View.VISIBLE);
            holder.btnSubir.setOnClickListener(v -> listener.onSubir(numeroPeriodo));
        }
    }

    @Override
    public int getItemCount() {
        return periodos.size();
    }

    static class PeriodoViewHolder extends RecyclerView.ViewHolder {
        TextView tvPeriodo, tvEstado;
        Button btnSubir;

        PeriodoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPeriodo = itemView.findViewById(R.id.tvPeriodo);
            tvEstado  = itemView.findViewById(R.id.tvEstado);
            btnSubir  = itemView.findViewById(R.id.btnSubir);
        }
    }
}

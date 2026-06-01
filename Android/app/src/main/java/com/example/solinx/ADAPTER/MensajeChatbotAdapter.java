package com.example.solinx.ADAPTER;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solinx.R;
import com.example.solinx.DTO.MensajeChatbot;

import java.util.List;

public class MensajeChatbotAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TIPO_USUARIO = 1;
    private static final int TIPO_BOT     = 2;
    private static final int TIPO_ERROR   = 3;

    private final List<MensajeChatbot> mensajes;

    public MensajeChatbotAdapter(List<MensajeChatbot> mensajes) {
        this.mensajes = mensajes;
    }

    public void agregarMensaje(MensajeChatbot m) {
        mensajes.add(m);
        notifyItemInserted(mensajes.size() - 1);
    }

    public void agregarMensajeError(String texto) {
        mensajes.add(new MensajeChatbot("error", texto));
        notifyItemInserted(mensajes.size() - 1);
    }

    public void limpiar() {
        int n = mensajes.size();
        mensajes.clear();
        notifyItemRangeRemoved(0, n);
    }

    @Override
    public int getItemViewType(int position) {
        MensajeChatbot m = mensajes.get(position);
        if ("error".equals(m.getRol())) return TIPO_ERROR;
        return m.esUsuario() ? TIPO_USUARIO : TIPO_BOT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        int layoutId;
        switch (viewType) {
            case TIPO_USUARIO: layoutId = R.layout.item_mensaje_usuario; break;
            case TIPO_ERROR:   layoutId = R.layout.item_mensaje_error; break;
            default:           layoutId = R.layout.item_mensaje_bot;
        }
        return new VH(inflater.inflate(layoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((VH) holder).txt.setText(mensajes.get(position).getContenido());
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView txt;
        VH(@NonNull View itemView) {
            super(itemView);
            txt = itemView.findViewById(R.id.txtMensaje);
        }
    }
}

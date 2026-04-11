package com.example.solinx.ADAPTER;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solinx.R;
import com.example.solinx.Solicitudes;

import java.util.List;

public class AceptacionesEmpresaAdapter extends RecyclerView.Adapter<AceptacionesEmpresaAdapter.ViewHolder> {

    private final List<Solicitudes> listaSolicitudes;
    private final Context context;
    private final OnClickListener listener;

    public interface OnClickListener {
        void onAprobar(Solicitudes s);
        void onRechazar(Solicitudes s);
        void onEnviarCorreo(Solicitudes s);
    }

    public AceptacionesEmpresaAdapter(Context context, List<Solicitudes> listaSolicitudes,
                                      OnClickListener listener) {
        this.context = context;
        this.listaSolicitudes = listaSolicitudes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.activity_card_aceptacion_empresa, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Solicitudes s = listaSolicitudes.get(position);

        holder.tvAceptacionId.setText("Aceptación #" + s.getIdSolicitud());
        holder.tvNombreAlumno.setText("Alumno: " + (s.getNombreEstudiante() != null ? s.getNombreEstudiante() : "N/A"));
        holder.tvNombreEmpresa.setText("Empresa: " + (s.getNombreEmpresa() != null ? s.getNombreEmpresa() : "N/A"));

        String fecha = s.getFechaAceptacion();
        holder.tvFechaAceptacion.setText("Fecha Aceptación: " + (fecha != null && !fecha.isEmpty() ? fecha : "N/A"));

        if (holder.btnAprobar != null) {
            holder.btnAprobar.setOnClickListener(v -> listener.onAprobar(s));
        }
        if (holder.btnRechazar != null) {
            holder.btnRechazar.setOnClickListener(v -> listener.onRechazar(s));
        }
        if (holder.btnEnviarCorreo != null) {
            holder.btnEnviarCorreo.setOnClickListener(v -> listener.onEnviarCorreo(s));
        }
    }

    @Override
    public int getItemCount() {
        return listaSolicitudes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvAceptacionId, tvNombreAlumno, tvNombreEmpresa, tvFechaAceptacion;
        TextView btnRechazar, btnAprobar, btnEnviarCorreo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAceptacionId    = itemView.findViewById(R.id.tv_aceptacion_id);
            tvNombreAlumno    = itemView.findViewById(R.id.tv_nombre_alumno);
            tvNombreEmpresa   = itemView.findViewById(R.id.tv_nombre_empresa);
            tvFechaAceptacion = itemView.findViewById(R.id.tv_fecha_aceptacion);
            btnRechazar       = itemView.findViewById(R.id.btn_rechazar);
            btnAprobar        = itemView.findViewById(R.id.btn_aprobar);
            btnEnviarCorreo   = itemView.findViewById(R.id.btn_enviar_correo);
        }
    }
}

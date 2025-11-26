package com.example.solinx.ADAPTER;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solinx.R;
import com.example.solinx.Solicitud;

import java.util.List;

public class AceptacionesEmpresaAdapter extends RecyclerView.Adapter<AceptacionesEmpresaAdapter.AceptacionViewHolder> {

    private List<Solicitud> listaSolicitudes;
    private Context context;

    public AceptacionesEmpresaAdapter(Context context, List<Solicitud> listaSolicitudes) {
        this.context = context;
        this.listaSolicitudes = listaSolicitudes;
    }

    @NonNull
    @Override
    public AceptacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_card_aceptacion_empresa, parent, false);
        return new AceptacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AceptacionViewHolder holder, int position) {
        Solicitud solicitud = listaSolicitudes.get(position);

        holder.tvAceptacionId.setText("Aceptación #" + solicitud.getId());
        holder.tvNombreAlumno.setText("Alumno: " + solicitud.getNombreAlumno());
        holder.tvNombreEmpresa.setText("Empresa: " + solicitud.getNombreEmpresa());

        holder.tvFechaAceptacion.setText("Fecha Aceptación: " + solicitud.getHorario());
    }

    @Override
    public int getItemCount() {
        return listaSolicitudes.size();
    }


    class AceptacionViewHolder extends RecyclerView.ViewHolder {

        TextView tvAceptacionId, tvNombreAlumno, tvNombreEmpresa, tvFechaAceptacion;
        TextView btnRechazar, btnAprobar;

        public AceptacionViewHolder(@NonNull View itemView) {
            super(itemView);

            tvAceptacionId = itemView.findViewById(R.id.tv_aceptacion_id);
            tvNombreAlumno = itemView.findViewById(R.id.tv_nombre_alumno);
            tvNombreEmpresa = itemView.findViewById(R.id.tv_nombre_empresa);
            tvFechaAceptacion = itemView.findViewById(R.id.tv_fecha_aceptacion);
            btnRechazar = itemView.findViewById(R.id.btn_rechazar);
            btnAprobar = itemView.findViewById(R.id.btn_aprobar);

            btnAprobar.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Solicitud solicitud = listaSolicitudes.get(position);
                Toast.makeText(context, "Aprobando aceptación: " + solicitud.getId(), Toast.LENGTH_SHORT).show();
            });

            btnRechazar.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Solicitud solicitud = listaSolicitudes.get(position);
                Toast.makeText(context, "Rechazando aceptación: " + solicitud.getId(), Toast.LENGTH_SHORT).show();
            });
        }
    }
}
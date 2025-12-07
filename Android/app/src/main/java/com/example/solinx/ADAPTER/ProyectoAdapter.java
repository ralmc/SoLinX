package com.example.solinx.ADAPTER;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solinx.AlumnoEnviarSolicitud;
import com.example.solinx.R;
import com.example.solinx.RESPONSE.ProyectoResponse;

import java.util.ArrayList;
import java.util.List;

public class ProyectoAdapter extends RecyclerView.Adapter<ProyectoAdapter.ProyectoViewHolder> {

    private Context context;
    private List<ProyectoResponse> listaProyectos;

    public ProyectoAdapter(Context context) {
        this.context = context;
        this.listaProyectos = new ArrayList<>();
    }

    @NonNull
    @Override
    public ProyectoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_proyecto, parent, false);
        return new ProyectoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProyectoViewHolder holder, int position) {
        ProyectoResponse proyecto = listaProyectos.get(position);

        holder.txtNombreEmpresa.setText(proyecto.getNombreEmpresa() != null ?
                proyecto.getNombreEmpresa() : proyecto.getNombreProyecto());
        holder.txtFechas.setText(proyecto.getFechasFormateadas());

        holder.txtRepresentante.setText("Carrera: " +
                (proyecto.getCarreraEnfocada() != null ? proyecto.getCarreraEnfocada() : "No especificada"));

        holder.txtVacantes.setText("Vacantes disponibles: " + proyecto.getVacantes());

        holder.imgLogoEmpresa.setImageResource(R.drawable.solinx_logo);

        holder.cardProyecto.setOnClickListener(v -> {
            Intent intent = new Intent(context, AlumnoEnviarSolicitud.class);

            // Pasar TODOS los datos del proyecto
            intent.putExtra("proyectoId", proyecto.getIdProyecto());
            intent.putExtra("nombreEmpresa", proyecto.getNombreEmpresa());
            intent.putExtra("nombreProyecto", proyecto.getNombreProyecto());
            intent.putExtra("fechaInicio", proyecto.getFechaInicio());
            intent.putExtra("fechaFin", proyecto.getFechaTermino());
            intent.putExtra("carreraEnfocada", proyecto.getCarreraEnfocada());
            intent.putExtra("telefono", proyecto.getTelefonoEmpresa());
            intent.putExtra("vacantes", proyecto.getVacantes());
            intent.putExtra("ubicacion", proyecto.getUbicacion());
            intent.putExtra("objetivo", proyecto.getObjetivo());
            intent.putExtra("imagenRef", proyecto.getImagenRef());
            intent.putExtra("idEmpresa", proyecto.getIdEmpresa());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaProyectos.size();
    }

    public void setProyectos(List<ProyectoResponse> proyectos) {
        this.listaProyectos = proyectos;
        notifyDataSetChanged();
    }

    public void addProyecto(ProyectoResponse proyecto) {
        this.listaProyectos.add(proyecto);
        notifyItemInserted(listaProyectos.size() - 1);
    }

    public void clearProyectos() {
        this.listaProyectos.clear();
        notifyDataSetChanged();
    }

    static class ProyectoViewHolder extends RecyclerView.ViewHolder {
        CardView cardProyecto;
        ImageView imgLogoEmpresa;
        TextView txtNombreEmpresa, txtFechas, txtRepresentante, txtVacantes, txtVerDetalles;

        public ProyectoViewHolder(@NonNull View itemView) {
            super(itemView);
            cardProyecto = itemView.findViewById(R.id.cardProyecto);
            imgLogoEmpresa = itemView.findViewById(R.id.imgLogoEmpresa);
            txtNombreEmpresa = itemView.findViewById(R.id.txtNombreEmpresa);
            txtFechas = itemView.findViewById(R.id.txtFechas);
            txtRepresentante = itemView.findViewById(R.id.txtRepresentante);
            txtVacantes = itemView.findViewById(R.id.txtVacantes);
            txtVerDetalles = itemView.findViewById(R.id.txtVerDetalles);
        }
    }
}

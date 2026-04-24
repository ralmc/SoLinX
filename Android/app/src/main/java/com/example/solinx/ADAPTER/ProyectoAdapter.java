package com.example.solinx.ADAPTER;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solinx.AlumnoEnviarSolicitud;
import com.example.solinx.R;
import com.example.solinx.RESPONSE.ProyectoResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProyectoAdapter extends RecyclerView.Adapter<ProyectoAdapter.ProyectoViewHolder> {

    private final Context context;
    private List<ProyectoResponse> listaProyectos;

    private Map<Integer, String> estadosSolicitudes = new HashMap<>();
    private boolean alumnoAceptado = false;
    private Integer idProyectoAceptado = null;

    public ProyectoAdapter(Context context) {
        this.context = context;
        this.listaProyectos = new ArrayList<>();
    }

    public void setEstadosSolicitudes(Map<Integer, String> estados, boolean aceptado, Integer idProyectoAceptado) {
        this.estadosSolicitudes = estados != null ? estados : new HashMap<>();
        this.alumnoAceptado = aceptado;
        this.idProyectoAceptado = idProyectoAceptado;
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

        // ─── Datos básicos ─────────────────────────────────
        holder.txtNombreProyecto.setText(proyecto.getNombreProyecto() != null ?
                proyecto.getNombreProyecto() : "Sin nombre");
        holder.txtNombreEmpresa.setText(proyecto.getNombreEmpresa() != null ?
                proyecto.getNombreEmpresa() : "Sin empresa");
        holder.txtFechas.setText(proyecto.getFechasFormateadas());
        holder.txtRepresentante.setText("Carrera: " +
                (proyecto.getCarreraEnfocada() != null ? proyecto.getCarreraEnfocada() : "No especificada"));
        holder.txtVacantes.setText("Vacantes disponibles: " + proyecto.getVacantes());

        // ─── Imagen con filtro gris si ya postulado/aceptado ─
        if (proyecto.getFotoEmpresa() != null && !proyecto.getFotoEmpresa().isEmpty()) {
            try {
                byte[] bytes = Base64.decode(proyecto.getFotoEmpresa(), Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (bmp != null) holder.imgLogoEmpresa.setImageBitmap(bmp);
                else holder.imgLogoEmpresa.setImageResource(R.drawable.solinx_logo);
            } catch (Exception e) {
                holder.imgLogoEmpresa.setImageResource(R.drawable.solinx_logo);
            }
        } else {
            holder.imgLogoEmpresa.setImageResource(R.drawable.solinx_logo);
        }

        String estadoSol = estadosSolicitudes.get(proyecto.getIdProyecto());
        boolean tieneEstado = estadoSol != null;

        android.util.Log.d("FILTRO", "id: " + proyecto.getIdProyecto() + " | estadoSol: " + estadoSol + " | alumnoAceptado: " + alumnoAceptado);

        // ─── Filtro gris ───────────────────────────────────
        if (tieneEstado || alumnoAceptado) {
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            holder.imgLogoEmpresa.setColorFilter(new ColorMatrixColorFilter(matrix));
        } else {
            holder.imgLogoEmpresa.clearColorFilter();
        }

        boolean esElAceptado = idProyectoAceptado != null
                && idProyectoAceptado.equals(proyecto.getIdProyecto());

        // ─── Etiqueta de estado ────────────────────────────
        if (esElAceptado) {
            holder.txtEstadoSolicitud.setVisibility(View.VISIBLE);
            holder.txtEstadoSolicitud.setText("✅ Aceptado");
            holder.txtEstadoSolicitud.setBackgroundResource(R.drawable.bg_badge_green);
        } else if (estadoSol != null) {
            holder.txtEstadoSolicitud.setVisibility(View.VISIBLE);
            switch (estadoSol.toLowerCase()) {
                case "rechazada":
                case "rechazada_empresa":
                case "rechazada_supervisor":
                    holder.txtEstadoSolicitud.setText("❌ No seleccionado");
                    holder.txtEstadoSolicitud.setBackgroundResource(R.drawable.bg_badge_red);
                    break;
                case "aprobada_supervisor":
                    holder.txtEstadoSolicitud.setText("🔄 En revisión");
                    holder.txtEstadoSolicitud.setBackgroundResource(R.drawable.bg_badge_blue);
                    break;
                default:
                    holder.txtEstadoSolicitud.setText("⏳ En proceso");
                    holder.txtEstadoSolicitud.setBackgroundResource(R.drawable.bg_badge_yellow);
                    break;
            }
        } else if (alumnoAceptado) {
            holder.txtEstadoSolicitud.setVisibility(View.VISIBLE);
            holder.txtEstadoSolicitud.setText("No disponible");
            holder.txtEstadoSolicitud.setBackgroundResource(R.drawable.bg_badge_grey);
        } else {
            holder.txtEstadoSolicitud.setVisibility(View.GONE);
        }

        // ─── Botón / click ─────────────────────────────────
        if (holder.txtVerDetalles != null) {
            if (alumnoAceptado) {
                holder.txtVerDetalles.setText("Ya fuiste aceptado ✅");
                holder.txtVerDetalles.setAlpha(0.5f);
            } else if ("aceptada".equalsIgnoreCase(estadoSol)) {
                holder.txtVerDetalles.setText("✅ Aceptado");
                holder.txtVerDetalles.setAlpha(0.5f);
            } else if (estadoSol != null && (estadoSol.toLowerCase().startsWith("rechazada"))) {
                holder.txtVerDetalles.setText("❌ No seleccionado");
                holder.txtVerDetalles.setAlpha(0.5f);
            } else if (estadoSol != null) {
                holder.txtVerDetalles.setText("⏳ En proceso");
                holder.txtVerDetalles.setAlpha(0.5f);
            } else {
                holder.txtVerDetalles.setText("Ver detalles");
                holder.txtVerDetalles.setAlpha(1f);
            }
        }

        holder.cardProyecto.setOnClickListener(v -> {
            if (alumnoAceptado) return;
            if (estadoSol != null) return;

            Intent intent = new Intent(context, AlumnoEnviarSolicitud.class);
            intent.putExtra("proyectoId",      proyecto.getIdProyecto());
            intent.putExtra("nombreEmpresa",   proyecto.getNombreEmpresa());
            intent.putExtra("nombreProyecto",  proyecto.getNombreProyecto());
            intent.putExtra("fechaInicio",     proyecto.getFechaInicio());
            intent.putExtra("fechaFin",        proyecto.getFechaTermino());
            intent.putExtra("carreraEnfocada", proyecto.getCarreraEnfocada());
            intent.putExtra("telefono",        proyecto.getTelefonoEmpresa());
            intent.putExtra("vacantes",        proyecto.getVacantes());
            intent.putExtra("ubicacion",       proyecto.getUbicacion());
            intent.putExtra("objetivo",        proyecto.getObjetivo());
            intent.putExtra("imagenRef",       proyecto.getImagenRef());
            intent.putExtra("imagenProyecto",  proyecto.getImagenProyecto());
            intent.putExtra("idEmpresa",       proyecto.getIdEmpresa());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return listaProyectos.size(); }

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
        TextView txtNombreEmpresa, txtFechas, txtRepresentante, txtVacantes, txtVerDetalles, txtNombreProyecto, txtEstadoSolicitud;

        public ProyectoViewHolder(@NonNull View itemView) {
            super(itemView);
            cardProyecto      = itemView.findViewById(R.id.cardProyecto);
            imgLogoEmpresa    = itemView.findViewById(R.id.imgLogoEmpresa);
            txtNombreEmpresa  = itemView.findViewById(R.id.txtNombreEmpresa);
            txtFechas         = itemView.findViewById(R.id.txtFechas);
            txtRepresentante  = itemView.findViewById(R.id.txtRepresentante);
            txtVacantes       = itemView.findViewById(R.id.txtVacantes);
            txtVerDetalles    = itemView.findViewById(R.id.txtVerDetalles);
            txtEstadoSolicitud = itemView.findViewById(R.id.txtEstadoSolicitud);
            txtNombreProyecto = itemView.findViewById(R.id.txtNombreProyecto);
        }
    }
}
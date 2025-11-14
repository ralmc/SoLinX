
package com.example.solinx;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
//VELAZQUEZ REYNOSO ADRIAN
public class SolicitudesAlumnoAdapter extends RecyclerView.Adapter<SolicitudesAlumnoAdapter.SolicitudViewHolder> {

    private List<Solicitud> listaSolicitudes;
    private Context context;

    public SolicitudesAlumnoAdapter(Context context, List<Solicitud> listaSolicitudes) {
        this.context = context;
        this.listaSolicitudes = listaSolicitudes;
    }

    @NonNull
    @Override
    public SolicitudViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_card_solicitud_alumno, parent, false);
        return new SolicitudViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SolicitudViewHolder holder, int position) {
        Solicitud solicitud = listaSolicitudes.get(position);

        holder.tvSolicitudId.setText("Solicitud #" + solicitud.getId());
        holder.tvNombreAlumno.setText("Alumno: " + solicitud.getNombreAlumno());
        holder.tvBoletaAlumno.setText("Boleta: " + solicitud.getBoleta());
        holder.tvNombreEmpresa.setText("Empresa: " + solicitud.getNombreEmpresa());

        holder.tvDetalleProyecto.setText("Tipo de servicio: " + solicitud.getTipoServicio());
        holder.tvDetalleObjetivos.setText("Carrera: " + solicitud.getCarrera());
        holder.tvDetalleHoras.setText("Elección de horario: " + solicitud.getHorario());

        holder.layoutDetallesExpandibles.setVisibility(View.GONE);
        holder.btnExpandirDetalles.setImageResource(R.drawable.ic_arrow_down);
    }

    @Override
    public int getItemCount() {
        return listaSolicitudes.size();
    }

    class SolicitudViewHolder extends RecyclerView.ViewHolder {

        TextView tvSolicitudId, tvNombreAlumno, tvBoletaAlumno, tvNombreEmpresa;
        TextView tvDetalleProyecto, tvDetalleObjetivos, tvDetalleHoras;
        TextView btnRechazar, btnAprobar;
        ImageButton btnExpandirDetalles;
        LinearLayout layoutDetallesExpandibles;

        public SolicitudViewHolder(@NonNull View itemView) {
            super(itemView);

            // Conecta los Views con sus IDs del XML
            tvSolicitudId = itemView.findViewById(R.id.tv_solicitud_id);
            tvNombreAlumno = itemView.findViewById(R.id.tv_nombre_alumno);
            tvBoletaAlumno = itemView.findViewById(R.id.tv_boleta_alumno);
            tvNombreEmpresa = itemView.findViewById(R.id.tv_nombre_empresa);

            tvDetalleProyecto = itemView.findViewById(R.id.tv_detalle_proyecto);
            tvDetalleObjetivos = itemView.findViewById(R.id.tv_detalle_objetivos);
            tvDetalleHoras = itemView.findViewById(R.id.tv_detalle_horas);

            btnRechazar = itemView.findViewById(R.id.btn_rechazar);
            btnAprobar = itemView.findViewById(R.id.btn_aprobar);
            btnExpandirDetalles = itemView.findViewById(R.id.btn_expandir_detalles);
            layoutDetallesExpandibles = itemView.findViewById(R.id.layout_detalles_expandibles);

            btnExpandirDetalles.setOnClickListener(v -> {
                if (layoutDetallesExpandibles.getVisibility() == View.GONE) {
                    layoutDetallesExpandibles.setVisibility(View.VISIBLE);
                    btnExpandirDetalles.setImageResource(R.drawable.ic_arrow_up);
                } else {
                    layoutDetallesExpandibles.setVisibility(View.GONE);
                    btnExpandirDetalles.setImageResource(R.drawable.ic_arrow_down);
                }
            });

            btnAprobar.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Solicitud solicitud = listaSolicitudes.get(position);
                Toast.makeText(context, "Aprobando solicitud: " + solicitud.getId(), Toast.LENGTH_SHORT).show();
            });


            btnRechazar.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Solicitud solicitud = listaSolicitudes.get(position);
                Toast.makeText(context, "Rechazando solicitud: " + solicitud.getId(), Toast.LENGTH_SHORT).show();
            });
        }
    }
}
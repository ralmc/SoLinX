package com.example.solinx.ADAPTER;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solinx.API.ApiClient;
import com.example.solinx.DTO.DocumentoDTO;
import com.example.solinx.R;
import com.example.solinx.Solicitudes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DocumentoSupervisorAdapter extends RecyclerView.Adapter<DocumentoSupervisorAdapter.ViewHolder> {

    private static final String BASE_URL = "http://10.0.2.2:8080/SoLinX/api";

    public static class DocumentoItem {
        public final Solicitudes solicitud;
        public final DocumentoDTO doc;

        public DocumentoItem(Solicitudes solicitud, DocumentoDTO doc) {
            this.solicitud = solicitud;
            this.doc       = doc;
        }
    }

    public interface OnClickListener {
        void onAprobar(DocumentoItem item);
        void onRechazar(DocumentoItem item);
    }

    private final Context context;
    private final List<DocumentoItem> items;
    private final OnClickListener listener;

    public DocumentoSupervisorAdapter(Context context, List<DocumentoItem> items, OnClickListener listener) {
        this.context  = context;
        this.items    = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_documento_supervisor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentoItem item = items.get(position);
        Solicitudes sol    = item.solicitud;
        DocumentoDTO doc   = item.doc;

        holder.tvNombreAlumno.setText(sol.getNombreEstudiante() != null ? sol.getNombreEstudiante() : "N/A");
        holder.tvCorreoAlumno.setText(sol.getCorreoEstudiante() != null ? sol.getCorreoEstudiante() : "N/A");
        holder.tvNombreProyecto.setText("Proyecto: " + (sol.getNombreProyecto() != null ? sol.getNombreProyecto() : "N/A"));
        holder.tvPeriodo.setText("Periodo " + doc.getPeriodo());
        holder.tvNombreArchivo.setText(doc.getNombreArchivo() != null ? doc.getNombreArchivo() : "documento.pdf");
        holder.tvFecha.setText(doc.getFechaSubida() != null ? doc.getFechaSubida() : "");

        // Estado badge
        String estado = doc.getEstadoDocumento() != null ? doc.getEstadoDocumento() : "pendiente";
        switch (estado) {
            case "aprobado":
                holder.tvEstadoBadge.setText("✓ Aprobado");
                holder.tvEstadoBadge.setBackgroundTintList(ColorStateList.valueOf(0xFF38A169));
                holder.btnAprobar.setVisibility(View.GONE);
                holder.btnRechazar.setVisibility(View.GONE);
                break;
            case "rechazado":
                holder.tvEstadoBadge.setText("✗ Rechazado");
                holder.tvEstadoBadge.setBackgroundTintList(ColorStateList.valueOf(0xFFE53E3E));
                holder.btnAprobar.setVisibility(View.VISIBLE);
                holder.btnRechazar.setVisibility(View.GONE);
                break;
            default:
                holder.tvEstadoBadge.setText("⏳ En revisión");
                holder.tvEstadoBadge.setBackgroundTintList(ColorStateList.valueOf(0xFFD69E2E));
                holder.btnAprobar.setVisibility(View.VISIBLE);
                holder.btnRechazar.setVisibility(View.VISIBLE);
                break;
        }

        holder.btnAprobar.setOnClickListener(v -> listener.onAprobar(item));
        holder.btnRechazar.setOnClickListener(v -> listener.onRechazar(item));

        // ─── Ver PDF ──────────────────────────────────────────────────────────
        holder.btnVerPdf.setOnClickListener(v -> {
            int boleta  = sol.getBoleta();
            int periodo = doc.getPeriodo();
            String url  = BASE_URL + "/documento/" + boleta + "/" + periodo + "/archivo";
            descargarYAbrirPdf(url, "periodo_" + periodo + "_" + boleta + ".pdf");
        });
    }

    private void descargarYAbrirPdf(String url, String nombreArchivo) {
        Toast.makeText(context, "Descargando PDF...", Toast.LENGTH_SHORT).show();

        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response response = client.newCall(request).execute();

                if (!response.isSuccessful() || response.body() == null) {
                    postError("Error al descargar el PDF (" + response.code() + ")");
                    return;
                }

                File outputFile = new File(context.getCacheDir(), nombreArchivo);
                try (InputStream input = response.body().byteStream();
                     FileOutputStream output = new FileOutputStream(outputFile)) {
                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = input.read(buffer)) != -1) output.write(buffer, 0, len);
                }

                Uri fileUri = FileProvider.getUriForFile(
                        context,
                        context.getPackageName() + ".provider",
                        outputFile);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(fileUri, "application/pdf");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    postError("No hay visor de PDF instalado");
                }

            } catch (Exception e) {
                postError("Error: " + e.getMessage());
            }
        }).start();
    }

    private void postError(String msg) {
        android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
        handler.post(() -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreAlumno, tvCorreoAlumno, tvNombreProyecto;
        TextView tvPeriodo, tvNombreArchivo, tvFecha, tvEstadoBadge;
        TextView btnAprobar, btnRechazar, btnVerPdf;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreAlumno   = itemView.findViewById(R.id.tv_nombre_alumno);
            tvCorreoAlumno   = itemView.findViewById(R.id.tv_correo_alumno);
            tvNombreProyecto = itemView.findViewById(R.id.tv_nombre_proyecto);
            tvPeriodo        = itemView.findViewById(R.id.tv_periodo);
            tvNombreArchivo  = itemView.findViewById(R.id.tv_nombre_archivo);
            tvFecha          = itemView.findViewById(R.id.tv_fecha);
            tvEstadoBadge    = itemView.findViewById(R.id.tv_estado_badge);
            btnAprobar       = itemView.findViewById(R.id.btn_aprobar);
            btnRechazar      = itemView.findViewById(R.id.btn_rechazar);
            btnVerPdf        = itemView.findViewById(R.id.btn_ver_pdf);
        }
    }
}
package com.example.solinx;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solinx.ADAPTER.PeriodoAdapter;
import com.example.solinx.API.ApiClient;
import com.example.solinx.API.ApiService;
import com.example.solinx.DTO.DocumentoDTO;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class AlumnoDocumentos extends Fragment {

    private static final String TAG = "AlumnoDocumentos";

    private RecyclerView recyclerPeriodos;
    private PeriodoAdapter periodoAdapter;
    private ApiService apiService;
    private Integer boleta;
    private int periodoSeleccionado = -1;

    private ActivityResultLauncher<String> pdfLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pdfLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(), uri -> {
                    if (uri != null && periodoSeleccionado != -1) {
                        subirPDF(uri, periodoSeleccionado);
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alumno_documentos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("SoLinXPrefs", MODE_PRIVATE);
        String boletaStr = prefs.getString("boleta", null);
        if (boletaStr != null && !boletaStr.equals("N/A")) {
            boleta = Integer.parseInt(boletaStr);
        }

        apiService = ApiClient.getClient().create(ApiService.class);
        recyclerPeriodos = view.findViewById(R.id.recyclerPeriodos);
        recyclerPeriodos.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<DocumentoDTO> periodosVacios = new ArrayList<>();
        for (int i = 0; i < 8; i++) periodosVacios.add(null);
        periodoAdapter = new PeriodoAdapter(periodosVacios, 1, periodo -> {
            periodoSeleccionado = periodo;
            pdfLauncher.launch("application/pdf");
        });
        recyclerPeriodos.setAdapter(periodoAdapter);

        cargarDocumentos();
    }

    private void cargarDocumentos() {
        if (boleta == null) {
            Log.e(TAG, "Boleta es null");
            return;
        }

        apiService.getDocumentos(boleta).enqueue(new Callback<List<DocumentoDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<DocumentoDTO>> call,
                                   @NonNull Response<List<DocumentoDTO>> response) {

                List<DocumentoDTO> periodos = new ArrayList<>();
                for (int i = 1; i <= 8; i++) periodos.add(null);

                if (response.isSuccessful() && response.body() != null) {
                    for (DocumentoDTO doc : response.body()) {
                        if (doc.getPeriodo() >= 1 && doc.getPeriodo() <= 8) {
                            periodos.set(doc.getPeriodo() - 1, doc);
                        }
                    }
                }

                int primerPeriodoDisponible = 1;
                for (int i = 0; i < 8; i++) {
                    if (periodos.get(i) != null) {
                        primerPeriodoDisponible = i + 2;
                    } else {
                        break;
                    }
                }
                final int periodoDesbloqueado = primerPeriodoDisponible;

                periodoAdapter = new PeriodoAdapter(periodos, periodoDesbloqueado, periodo -> {
                    if (periodo < periodoDesbloqueado && periodos.get(periodo - 1) != null) {
                        Toast.makeText(requireContext(),
                                "El Periodo " + periodo + " ya fue subido",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    periodoSeleccionado = periodo;
                    pdfLauncher.launch("application/pdf");
                });

                recyclerPeriodos.setAdapter(periodoAdapter);
            }

            @Override
            public void onFailure(@NonNull Call<List<DocumentoDTO>> call, @NonNull Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());

                List<DocumentoDTO> periodos = new ArrayList<>();
                for (int i = 1; i <= 8; i++) periodos.add(null);

                periodoAdapter = new PeriodoAdapter(periodos, 1, periodo -> {
                    periodoSeleccionado = periodo;
                    pdfLauncher.launch("application/pdf");
                });

                recyclerPeriodos.setAdapter(periodoAdapter);
                Toast.makeText(requireContext(),
                        "Error al cargar documentos: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void subirPDF(Uri uri, int periodo) {
        if (boleta == null) return;

        try {
            long tamañoMaximo = 1_500_000L;
            long tamañoArchivo = 0;

            Cursor cursorSize = requireContext().getContentResolver()
                    .query(uri, null, null, null, null);
            if (cursorSize != null && cursorSize.moveToFirst()) {
                int idx = cursorSize.getColumnIndex(OpenableColumns.SIZE);
                if (idx != -1) tamañoArchivo = cursorSize.getLong(idx);
                cursorSize.close();
            }

            if (tamañoArchivo > tamañoMaximo) {
                Toast.makeText(requireContext(),
                        "El PDF no puede superar 1.5 MB (tamaño actual: " +
                                String.format("%.2f", tamañoArchivo / 1_000_000.0) + " MB)",
                        Toast.LENGTH_LONG).show();
                return;
            }

            String nombreArchivo = obtenerNombreArchivo(uri);

            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();

            RequestBody requestBody = RequestBody.create(
                    MediaType.parse("application/pdf"), bytes);
            MultipartBody.Part archivoPart = MultipartBody.Part.createFormData(
                    "archivo", nombreArchivo, requestBody);

            apiService.subirDocumento(boleta, periodo, archivoPart)
                    .enqueue(new Callback<DocumentoDTO>() {
                        @Override
                        public void onResponse(@NonNull Call<DocumentoDTO> call,
                                               @NonNull Response<DocumentoDTO> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(requireContext(),
                                        "Periodo; " + periodo + " Subido correctamente",
                                        Toast.LENGTH_SHORT).show();
                                cargarDocumentos();
                            } else if (response.code() == 409) {
                                Toast.makeText(requireContext(),
                                        "Ya existe un documento para el Periodo " + periodo,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireContext(),
                                        "Error al subir: " + response.code(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<DocumentoDTO> call,
                                              @NonNull Throwable t) {
                            Toast.makeText(requireContext(),
                                    "Error de red: " + t.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            Log.e(TAG, "Error al leer el archivo: " + e.getMessage());
            Toast.makeText(requireContext(),
                    "Error al leer el archivo: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private String obtenerNombreArchivo(Uri uri) {
        String nombre = "documento.pdf";
        Cursor cursor = requireContext().getContentResolver()
                .query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (idx != -1) nombre = cursor.getString(idx);
            cursor.close();
        }
        return nombre;
    }
}
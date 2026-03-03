package com.example.solinx;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.solinx.API.ApiClient;
import com.example.solinx.API.ApiService;
import com.example.solinx.DTO.HorarioDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlumnoHorario extends Fragment {
    private static final String TAG = "AlumnoHorario";
    // Spinners
    private Spinner spLunInicio, spLunFinal;
    private Spinner spMarInicio, spMarFinal;
    private Spinner spMierInicio, spMierFinal;
    private Spinner spJueInicio, spJueFinal;
    private Spinner spVieInicio, spVieFinal;
    private Spinner spSabInicio, spSabFinal;
    private Spinner spDomInicio, spDomFinal;
    // Checkboxes
    private CheckBox cbLunes, cbMartes, cbMiercoles, cbJueves, cbViernes, cbSabado, cbDomingo;
    private TextView tvLun, tvMar, tvMier, tvJue, tvVie, tvSab, tvDom;
    private Button btnEnviarHorario;
    private int boleta;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.alumno_horario, container, false);
        if (getArguments() != null)
            boleta = getArguments().getInt("boleta");
        return v;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View lunesView = view.findViewById(R.id.diaLunes);
        tvLun = lunesView.findViewById(R.id.tvNombreDia);
        spLunInicio = lunesView.findViewById(R.id.spInicio);
        spLunFinal = lunesView.findViewById(R.id.spFinal);
        cbLunes = lunesView.findViewById(R.id.cbSinClase);
        View martesView = view.findViewById(R.id.diaMartes);
        tvMar = martesView.findViewById(R.id.tvNombreDia);
        spMarInicio = martesView.findViewById(R.id.spInicio);
        spMarFinal = martesView.findViewById(R.id.spFinal);
        cbMartes = martesView.findViewById(R.id.cbSinClase);
        View miercolesView = view.findViewById(R.id.diaMiercoles);
        tvMier = miercolesView.findViewById(R.id.tvNombreDia);
        spMierInicio = miercolesView.findViewById(R.id.spInicio);
        spMierFinal = miercolesView.findViewById(R.id.spFinal);
        cbMiercoles = miercolesView.findViewById(R.id.cbSinClase);
        View juevesView = view.findViewById(R.id.diaJueves);
        tvJue = juevesView.findViewById(R.id.tvNombreDia);
        spJueInicio = juevesView.findViewById(R.id.spInicio);
        spJueFinal = juevesView.findViewById(R.id.spFinal);
        cbJueves = juevesView.findViewById(R.id.cbSinClase);
        View viernesView = view.findViewById(R.id.diaViernes);
        tvVie = viernesView.findViewById(R.id.tvNombreDia);
        spVieInicio = viernesView.findViewById(R.id.spInicio);
        spVieFinal = viernesView.findViewById(R.id.spFinal);
        cbViernes = viernesView.findViewById(R.id.cbSinClase);
        View sabadoView = view.findViewById(R.id.diaSabado);
        tvSab = sabadoView.findViewById(R.id.tvNombreDia);
        spSabInicio = sabadoView.findViewById(R.id.spInicio);
        spSabFinal = sabadoView.findViewById(R.id.spFinal);
        cbSabado = sabadoView.findViewById(R.id.cbSinClase);
        View domingoView = view.findViewById(R.id.diaDomingo);
        tvDom = domingoView.findViewById(R.id.tvNombreDia);
        spDomInicio = domingoView.findViewById(R.id.spInicio);
        spDomFinal = domingoView.findViewById(R.id.spFinal);
        cbDomingo = domingoView.findViewById(R.id.cbSinClase);

        btnEnviarHorario = view.findViewById(R.id.btnEnviarHorario);

        List<String> horas = generarHoras();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                horas
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        tvLun.setText("Lunes");
        tvMar.setText("Martes");
        tvMier.setText("Miercoles");
        tvJue.setText("Jueves");
        tvVie.setText("Viernes");
        tvSab.setText("Sabado");
        tvDom.setText("Domingo");

        spLunInicio.setAdapter(adapter);  spLunFinal.setAdapter(adapter);
        spMarInicio.setAdapter(adapter);  spMarFinal.setAdapter(adapter);
        spMierInicio.setAdapter(adapter); spMierFinal.setAdapter(adapter);
        spJueInicio.setAdapter(adapter);  spJueFinal.setAdapter(adapter);
        spVieInicio.setAdapter(adapter);  spVieFinal.setAdapter(adapter);
        spSabInicio.setAdapter(adapter);  spSabFinal.setAdapter(adapter);
        spDomInicio.setAdapter(adapter);  spDomFinal.setAdapter(adapter);

        configurarCheckbox(cbLunes,     spLunInicio,  spLunFinal);
        configurarCheckbox(cbMartes,    spMarInicio,  spMarFinal);
        configurarCheckbox(cbMiercoles, spMierInicio, spMierFinal);
        configurarCheckbox(cbJueves,    spJueInicio,  spJueFinal);
        configurarCheckbox(cbViernes,   spVieInicio,  spVieFinal);
        configurarCheckbox(cbSabado,    spSabInicio,  spSabFinal);
        configurarCheckbox(cbDomingo,   spDomInicio,  spDomFinal);

        btnEnviarHorario.setOnClickListener(v -> enviarHorario());
    }

    private List<String> generarHoras() {
        List<String> horas = new ArrayList<>();
        for (int h = 0; h < 24; h++) {
            horas.add(String.format("%02d:00", h));
            horas.add(String.format("%02d:30", h));
        }
        return horas;
    }

    private void configurarCheckbox(CheckBox cb, Spinner spInicio, Spinner spFinal) {
        cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            spInicio.setEnabled(!isChecked);
            spFinal.setEnabled(!isChecked);
            spInicio.setAlpha(isChecked ? 0.3f : 1f);
            spFinal.setAlpha(isChecked ? 0.3f : 1f);
        });
    }

    private String getHora(Spinner sp, CheckBox cb) {
        if (cb.isChecked()) return null;
        String val = sp.getSelectedItem().toString();
        return val.equals("--") ? null : val;
    }

    private void enviarHorario() {
        if (boleta == 0) {
            Toast.makeText(requireContext(), "Boleta inválida", Toast.LENGTH_SHORT).show();
            return;
        }
        HorarioDTO dto = new HorarioDTO();
        dto.setLunInicio(getHora(spLunInicio, cbLunes));
        dto.setLunFinal(getHora(spLunFinal, cbLunes));
        dto.setMarInicio(getHora(spMarInicio, cbMartes));
        dto.setMarFinal(getHora(spMarFinal, cbMartes));
        dto.setMierInicio(getHora(spMierInicio, cbMiercoles));
        dto.setMierFinal(getHora(spMierFinal, cbMiercoles));
        dto.setJueInicio(getHora(spJueInicio, cbJueves));
        dto.setJueFinal(getHora(spJueFinal, cbJueves));
        dto.setVieInicio(getHora(spVieInicio, cbViernes));
        dto.setVieFinal(getHora(spVieFinal, cbViernes));
        dto.setSabInicio(getHora(spSabInicio, cbSabado));
        dto.setSabFinal(getHora(spSabFinal, cbSabado));
        dto.setDomInicio(getHora(spDomInicio, cbDomingo));
        dto.setDomFinal(getHora(spDomFinal, cbDomingo));

        int totalMinutos = 0;

        totalMinutos += calcularMinutosDia(dto.getLunInicio(), dto.getLunFinal());
        totalMinutos += calcularMinutosDia(dto.getMarInicio(), dto.getMarFinal());
        totalMinutos += calcularMinutosDia(dto.getMierInicio(), dto.getMierFinal());
        totalMinutos += calcularMinutosDia(dto.getJueInicio(), dto.getJueFinal());
        totalMinutos += calcularMinutosDia(dto.getVieInicio(), dto.getVieFinal());
        totalMinutos += calcularMinutosDia(dto.getSabInicio(), dto.getSabFinal());
        totalMinutos += calcularMinutosDia(dto.getDomInicio(), dto.getDomFinal());

        if (totalMinutos != 1200) {
            Toast.makeText(requireContext(),
                    "Debes registrar al menos 20 horas semanales",
                    Toast.LENGTH_LONG).show();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<HorarioDTO> call = apiService.crearHorario(boleta, dto);

        call.enqueue(new Callback<HorarioDTO>() {
            @Override
            public void onResponse(@NonNull Call<HorarioDTO> call,
                                   @NonNull Response<HorarioDTO> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Horario guardado correctamente");
                    Toast.makeText(requireContext(),
                            "¡Horario guardado correctamente!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    Log.e(TAG, "Error al guardar: " + response.code());
                    Toast.makeText(requireContext(),
                            "Error al guardar el horario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<HorarioDTO> call, @NonNull Throwable t) {
                Log.e(TAG, "Error de red: " + t.getMessage());
                Toast.makeText(requireContext(),
                        "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private int horaAMinutos(String hora) {
        if (hora == null) return 0;

        String[] partes = hora.split(":");
        int h = Integer.parseInt(partes[0]);
        int m = Integer.parseInt(partes[1]);

        return h * 60 + m;
    }

    private int calcularMinutosDia(String inicio, String fin) {
        if (inicio == null || fin == null) return 0;

        int minInicio = horaAMinutos(inicio);
        int minFin = horaAMinutos(fin);

        if (minFin <= minInicio) return 0;

        return minFin - minInicio;
    }


}
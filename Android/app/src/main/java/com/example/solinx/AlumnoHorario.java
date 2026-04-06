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
import com.example.solinx.DTO.RegistroAlumnoDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlumnoHorario extends Fragment {

    private static final String TAG       = "AlumnoHorario";
    private static final int    MIN_HORAS = 1200;

    private Spinner spLunInicio, spLunFinal, spMarInicio, spMarFinal,
            spMierInicio, spMierFinal, spJueInicio, spJueFinal,
            spVieInicio, spVieFinal, spSabInicio, spSabFinal,
            spDomInicio, spDomFinal;

    private CheckBox cbLunes, cbMartes, cbMiercoles, cbJueves,
            cbViernes, cbSabado, cbDomingo;

    private Button btnEnviarHorario;
    private ApiService apiService;
    private int boleta;
    private RegistroAlumnoDTO registroDto;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.alumno_horario, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = ApiClient.getClient().create(ApiService.class);

        Bundle args = getArguments();
        if (args != null) {
            boleta = args.getInt("boleta");
            String nombreUsuario = args.getString("nombreUsuario");
            if (nombreUsuario != null) {
                registroDto = new RegistroAlumnoDTO(
                        nombreUsuario, boleta,
                        args.getString("carrera"),
                        args.getString("escuela"),
                        args.getString("correo"),
                        args.getString("confirmarCorreo"),
                        args.getString("contrasena"),
                        args.getString("confirmarContrasena")
                );
            }
        }

        View[] vistas = {
                view.findViewById(R.id.diaLunes),    view.findViewById(R.id.diaMartes),
                view.findViewById(R.id.diaMiercoles), view.findViewById(R.id.diaJueves),
                view.findViewById(R.id.diaViernes),  view.findViewById(R.id.diaSabado),
                view.findViewById(R.id.diaDomingo)
        };

        String[] nombres = {"Lunes","Martes","Miércoles","Jueves","Viernes","Sábado","Domingo"};
        Spinner[] inicios = new Spinner[7];
        Spinner[] finales = new Spinner[7];
        CheckBox[] checks = new CheckBox[7];

        for (int i = 0; i < 7; i++) {
            ((TextView) vistas[i].findViewById(R.id.tvNombreDia)).setText(nombres[i]);
            inicios[i] = vistas[i].findViewById(R.id.spInicio);
            finales[i] = vistas[i].findViewById(R.id.spFinal);
            checks[i]  = vistas[i].findViewById(R.id.cbSinClase);
        }

        spLunInicio  = inicios[0]; spLunFinal  = finales[0]; cbLunes     = checks[0];
        spMarInicio  = inicios[1]; spMarFinal  = finales[1]; cbMartes    = checks[1];
        spMierInicio = inicios[2]; spMierFinal = finales[2]; cbMiercoles = checks[2];
        spJueInicio  = inicios[3]; spJueFinal  = finales[3]; cbJueves    = checks[3];
        spVieInicio  = inicios[4]; spVieFinal  = finales[4]; cbViernes   = checks[4];
        spSabInicio  = inicios[5]; spSabFinal  = finales[5]; cbSabado    = checks[5];
        spDomInicio  = inicios[6]; spDomFinal  = finales[6]; cbDomingo   = checks[6];

        btnEnviarHorario = view.findViewById(R.id.btnEnviarHorario);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, generarHoras());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        for (int i = 0; i < 7; i++) {
            inicios[i].setAdapter(adapter);
            finales[i].setAdapter(adapter);
            configurarCheckbox(checks[i], inicios[i], finales[i]);
        }

        btnEnviarHorario.setOnClickListener(v -> enviarHorario());
    }

    private void enviarHorario() {
        if (boleta == 0) {
            Toast.makeText(requireContext(), "Boleta inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        HorarioDTO horarioDto = armarHorarioDto();
        int total = calcularTotalMinutos(horarioDto);

        if (total < MIN_HORAS) {
            Toast.makeText(requireContext(),
                    "Mínimo 20h semanales. Tienes: " + total/60 + "h " + total%60 + "min",
                    Toast.LENGTH_LONG).show();
            return;
        }

        setLoading(true);

        if (registroDto != null) {
            registrarCuentaYHorario(horarioDto);
        } else {
            guardarHorario(horarioDto);
        }
    }

    private void registrarCuentaYHorario(HorarioDTO horarioDto) {
        apiService.registrarAlumno(registroDto).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call,
                                   @NonNull Response<String> response) {
                if (response.isSuccessful() && "Registro exitoso".equals(response.body())) {
                    Log.d(TAG, "Cuenta registrada — procediendo con horario");
                    guardarHorario(horarioDto);
                } else {
                    setLoading(false);
                    String error = "Error al registrar cuenta";
                    try {
                        if (response.errorBody() != null) error = response.errorBody().string();
                    } catch (Exception e) {
                        error = "Error: " + response.code();
                    }
                    Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                setLoading(false);
                Toast.makeText(requireContext(),
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void guardarHorario(HorarioDTO horarioDto) {
        apiService.crearHorario(boleta, horarioDto).enqueue(new Callback<HorarioDTO>() {
            @Override
            public void onResponse(@NonNull Call<HorarioDTO> call,
                                   @NonNull Response<HorarioDTO> response) {
                setLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(),
                            "¡Cuenta y horario guardados!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(requireActivity(), MainActivity.class));
                    requireActivity().finish();
                } else {
                    Toast.makeText(requireContext(),
                            "Error al guardar el horario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<HorarioDTO> call, @NonNull Throwable t) {
                setLoading(false);
                Toast.makeText(requireContext(),
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private HorarioDTO armarHorarioDto() {
        HorarioDTO dto = new HorarioDTO();
        dto.setLunInicio(getHora(spLunInicio, cbLunes));       dto.setLunFinal(getHora(spLunFinal, cbLunes));
        dto.setMarInicio(getHora(spMarInicio, cbMartes));      dto.setMarFinal(getHora(spMarFinal, cbMartes));
        dto.setMierInicio(getHora(spMierInicio, cbMiercoles)); dto.setMierFinal(getHora(spMierFinal, cbMiercoles));
        dto.setJueInicio(getHora(spJueInicio, cbJueves));      dto.setJueFinal(getHora(spJueFinal, cbJueves));
        dto.setVieInicio(getHora(spVieInicio, cbViernes));     dto.setVieFinal(getHora(spVieFinal, cbViernes));
        dto.setSabInicio(getHora(spSabInicio, cbSabado));      dto.setSabFinal(getHora(spSabFinal, cbSabado));
        dto.setDomInicio(getHora(spDomInicio, cbDomingo));     dto.setDomFinal(getHora(spDomFinal, cbDomingo));
        return dto;
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
        cb.setOnCheckedChangeListener((btn, checked) -> {
            spInicio.setEnabled(!checked); spFinal.setEnabled(!checked);
            spInicio.setAlpha(checked ? 0.3f : 1f); spFinal.setAlpha(checked ? 0.3f : 1f);
        });
    }

    private String getHora(Spinner sp, CheckBox cb) {
        if (cb.isChecked()) return null;
        String val = sp.getSelectedItem().toString();
        return val.equals("--") ? null : val + ":00";
    }

    private int calcularTotalMinutos(HorarioDTO dto) {
        return calcularMinutosDia(dto.getLunInicio(),  dto.getLunFinal())
                + calcularMinutosDia(dto.getMarInicio(),  dto.getMarFinal())
                + calcularMinutosDia(dto.getMierInicio(), dto.getMierFinal())
                + calcularMinutosDia(dto.getJueInicio(),  dto.getJueFinal())
                + calcularMinutosDia(dto.getVieInicio(),  dto.getVieFinal())
                + calcularMinutosDia(dto.getSabInicio(),  dto.getSabFinal())
                + calcularMinutosDia(dto.getDomInicio(),  dto.getDomFinal());
    }

    private int calcularMinutosDia(String inicio, String fin) {
        if (inicio == null || fin == null) return 0;
        int minInicio = horaAMinutos(inicio);
        int minFin    = horaAMinutos(fin);
        return minFin > minInicio ? minFin - minInicio : 0;
    }

    private int horaAMinutos(String hora) {
        String[] p = hora.split(":");
        return Integer.parseInt(p[0]) * 60 + Integer.parseInt(p[1]);
    }

    private void setLoading(boolean loading) {
        btnEnviarHorario.setEnabled(!loading);
        btnEnviarHorario.setText(loading ? "Guardando..." : "Enviar");
    }
}
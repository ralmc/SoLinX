package com.SoLinX.controller;

import com.SoLinX.dto.HorarioDto;
import com.SoLinX.model.Horario;
import com.SoLinX.model.HorarioEstudiante;
import com.SoLinX.service.HorarioService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/SoLinX/api")
@RestController
@AllArgsConstructor
public class HorarioController {

    private final HorarioService horarioService;

    @GetMapping("/horario/{boleta}")
    public ResponseEntity<HorarioDto> getHorario(@PathVariable("boleta") Integer boleta) {
        Horario h = horarioService.getHorarioByBoleta(boleta);
        if (h == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(HorarioDto.builder()
                .idHorario(h.getIdHorario())
                .lunInicio(h.getLunInicio()).lunFinal(h.getLunFinal())
                .marInicio(h.getMarInicio()).marFinal(h.getMarFinal())
                .mierInicio(h.getMierInicio()).mierFinal(h.getMierFinal())
                .jueInicio(h.getJueInicio()).jueFinal(h.getJueFinal())
                .vieInicio(h.getVieInicio()).vieFinal(h.getVieFinal())
                .sabInicio(h.getSabInicio()).sabFinal(h.getSabFinal())
                .domInicio(h.getDomInicio()).domFinal(h.getDomFinal())
                .build());
    }

    @PostMapping("/horario/{boleta}")
    public ResponseEntity<HorarioDto> crearHorario(
            @PathVariable("boleta") Integer boleta,
            @RequestBody HorarioDto horarioDto) {

        Horario horario = Horario.builder()
                .lunInicio(horarioDto.getLunInicio()).lunFinal(horarioDto.getLunFinal())
                .marInicio(horarioDto.getMarInicio()).marFinal(horarioDto.getMarFinal())
                .mierInicio(horarioDto.getMierInicio()).mierFinal(horarioDto.getMierFinal())
                .jueInicio(horarioDto.getJueInicio()).jueFinal(horarioDto.getJueFinal())
                .vieInicio(horarioDto.getVieInicio()).vieFinal(horarioDto.getVieFinal())
                .sabInicio(horarioDto.getSabInicio()).sabFinal(horarioDto.getSabFinal())
                .domInicio(horarioDto.getDomInicio()).domFinal(horarioDto.getDomFinal())
                .build();

        HorarioEstudiante resultado = horarioService.asignarHorario(boleta, horario);
        Horario h = resultado.getHorario();

        return ResponseEntity.ok(HorarioDto.builder()
                .idHorario(h.getIdHorario())
                .lunInicio(h.getLunInicio()).lunFinal(h.getLunFinal())
                .marInicio(h.getMarInicio()).marFinal(h.getMarFinal())
                .mierInicio(h.getMierInicio()).mierFinal(h.getMierFinal())
                .jueInicio(h.getJueInicio()).jueFinal(h.getJueFinal())
                .vieInicio(h.getVieInicio()).vieFinal(h.getVieFinal())
                .sabInicio(h.getSabInicio()).sabFinal(h.getSabFinal())
                .domInicio(h.getDomInicio()).domFinal(h.getDomFinal())
                .build());
    }
}
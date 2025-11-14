package com.SoLinX.controler;

import com.SoLinX.dto.ProyectoDto;
import com.SoLinX.model.Empresa;
import com.SoLinX.model.Proyecto;
import com.SoLinX.service.ProyectoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/SoLinX/api")
@RestController
@AllArgsConstructor
public class ProyectoController {

    private final ProyectoService proyectoService;

    @PostMapping("/proyecto")
    public ResponseEntity<ProyectoDto> save(@RequestBody ProyectoDto dto) {

        Proyecto proyecto = convertToEntity(dto);
        Proyecto guardado = proyectoService.save(proyecto);

        return ResponseEntity.ok(convertToDto(guardado));
    }

    @GetMapping("/proyecto")
    public ResponseEntity<List<ProyectoDto>> lista() {

        List<Proyecto> proyectos = proyectoService.getAll();

        if (proyectos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<ProyectoDto> dtos = proyectos.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/proyecto/{id}")
    public ResponseEntity<ProyectoDto> getById(@PathVariable Integer id) {
        Proyecto proyecto = proyectoService.getById(id);
        return ResponseEntity.ok(convertToDto(proyecto));
    }

    @PutMapping("/proyecto/{id}")
    public ResponseEntity<ProyectoDto> update(@PathVariable Integer id, @RequestBody ProyectoDto dto) {

        Proyecto proyecto = convertToEntity(dto);
        Proyecto actualizado = proyectoService.update(id, proyecto);

        return ResponseEntity.ok(convertToDto(actualizado));
    }

    @DeleteMapping("/proyecto/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        proyectoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private ProyectoDto convertToDto(Proyecto proyecto) {
        return ProyectoDto.builder()
                .idProyecto(proyecto.getIdProyecto())
                .nombreProyecto(proyecto.getNombreProyecto())
                .objetivo(proyecto.getObjetivo())
                .fechaInicio(proyecto.getFechaInicio())
                .vacantes(proyecto.getVacantes())
                .ubicacion(proyecto.getUbicacion())
                .justificacion(proyecto.getJustificacion())
                .fechaTermino(proyecto.getFechaTermino())
                .idEmpresa(proyecto.getIdEmpresa().getIdEmpresa())
                .build();
    }

    private Proyecto convertToEntity(ProyectoDto dto) {

        Empresa empresa = new Empresa();
        empresa.setIdEmpresa(dto.getIdEmpresa());

        return Proyecto.builder()
                .idProyecto(dto.getIdProyecto())
                .nombreProyecto(dto.getNombreProyecto())
                .objetivo(dto.getObjetivo())
                .fechaInicio(dto.getFechaInicio())
                .vacantes(dto.getVacantes())
                .ubicacion(dto.getUbicacion())
                .justificacion(dto.getJustificacion())
                .fechaTermino(dto.getFechaTermino())
                .idEmpresa(empresa)
                .build();
    }
}

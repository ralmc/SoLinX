package com.SoLinX.controler;

import com.SoLinX.dto.ProyectoDto;
import com.SoLinX.model.Empresa;
import com.SoLinX.model.Proyecto;
import com.SoLinX.service.ProyectoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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

    // Este trae TODOS (Cuidado con este, trae de todas las empresas)
    @GetMapping("/proyecto")
    public ResponseEntity<List<ProyectoDto>> lista() {
        List<Proyecto> proyectos = proyectoService.getAll();
        if (proyectos.isEmpty()) return ResponseEntity.noContent().build();

        List<ProyectoDto> dtos = proyectos.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // --- NUEVO ENDPOINT: Trae solo los de UNA empresa ---
    @GetMapping("/proyecto/empresa/{idEmpresa}")
    public ResponseEntity<List<ProyectoDto>> listarPorEmpresa(@PathVariable Integer idEmpresa) {

        List<Proyecto> proyectos = proyectoService.obtenerPorEmpresa(idEmpresa);

        if (proyectos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<ProyectoDto> dtos = proyectos.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
    // ---------------------------------------------------

    @GetMapping("/proyecto/{id}")
    public ResponseEntity<ProyectoDto> getById(@PathVariable Integer id) {
        Proyecto proyecto = proyectoService.getById(id);
        if (proyecto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToDto(proyecto));
    }

    @PutMapping("/proyecto/{id}")
    public ResponseEntity<ProyectoDto> update(@PathVariable Integer id, @RequestBody ProyectoDto dto) {
        Proyecto proyecto = convertToEntity(dto);
        Proyecto actualizado = proyectoService.update(id, proyecto);

        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToDto(actualizado));
    }

    @DeleteMapping("/proyecto/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        proyectoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --- MÉTODOS AUXILIARES ---

    private ProyectoDto convertToDto(Proyecto proyecto) {
        return ProyectoDto.builder()
                .idProyecto(proyecto.getIdProyecto())
                .carreraEnfocada(proyecto.getCarreraEnfocada())
                .nombreProyecto(proyecto.getNombreProyecto())
                .objetivo(proyecto.getObjetivo())
                .fechaInicio(proyecto.getFechaInicio())
                .vacantes(proyecto.getVacantes())
                .ubicacion(proyecto.getUbicacion())
                .fechaTermino(proyecto.getFechaTermino())
                .imagenRef(proyecto.getImagenRef())
                .idEmpresa(proyecto.getEmpresa() != null ? proyecto.getEmpresa().getIdEmpresa() : null)
                .nombreEmpresa(proyecto.getEmpresa() != null ? proyecto.getEmpresa().getNombreEmpresa() : "Sin Empresa")
                .build();
    }

    private Proyecto convertToEntity(ProyectoDto dto) {
        Empresa empresa = new Empresa();
        empresa.setIdEmpresa(dto.getIdEmpresa());

        Date fechaRegistro;
        if (dto.getFechaInicio() == null) {
            fechaRegistro = new Date();
        } else {
            fechaRegistro = dto.getFechaInicio();
        }

        String imagenFinal = dto.getImagenRef();
        if (imagenFinal == null || imagenFinal.trim().isEmpty()) {
            imagenFinal = "img_default_proyecto";
        }

        return Proyecto.builder()
                .idProyecto(dto.getIdProyecto())
                .carreraEnfocada(dto.getCarreraEnfocada())
                .nombreProyecto(dto.getNombreProyecto())
                .objetivo(dto.getObjetivo())
                .vacantes(dto.getVacantes())
                .ubicacion(dto.getUbicacion())
                .fechaInicio(fechaRegistro)
                .fechaTermino(dto.getFechaTermino())
                .imagenRef(imagenFinal)
                .empresa(empresa)
                .build();
    }
}
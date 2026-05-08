package com.SoLinX.controller;

import com.SoLinX.dto.ProyectoAlumnoResponseDto;
import com.SoLinX.dto.ProyectoDto;
import com.SoLinX.model.*;
import com.SoLinX.repository.EmpresaRepository;
import com.SoLinX.repository.PerfilRepository;
import com.SoLinX.repository.SolicitudRepository;
import com.SoLinX.repository.UsuarioRepository;
import com.SoLinX.service.ProyectoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequestMapping("/SoLinX/api")
@RestController
@AllArgsConstructor
public class ProyectoController {

    private final ProyectoService proyectoService;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final SolicitudRepository solicitudRepository;
    private final PerfilRepository perfilRepository;

    @PostMapping("/proyecto")
    public ResponseEntity<ProyectoDto> save(@RequestBody ProyectoDto dto) {
        Proyecto proyecto = convertToEntity(dto);
        proyecto.setEstadoProyecto("pendiente");
        Proyecto guardado = proyectoService.save(proyecto);
        return ResponseEntity.ok(convertToDto(guardado));
    }

    @GetMapping("/proyecto")
    public ResponseEntity<List<ProyectoDto>> lista(
            @RequestParam(required = false, defaultValue = "false") Boolean soloAprobados) {
        List<Proyecto> proyectos = proyectoService.getAll();
        if (proyectos.isEmpty()) return ResponseEntity.ok(new ArrayList<>());

        if (soloAprobados) {
            proyectos = proyectos.stream()
                    .filter(p -> "aprobado".equals(p.getEstadoProyecto()))
                    .collect(Collectors.toList());
        }

        List<ProyectoDto> dtos = proyectos.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/proyecto/empresa/{idEmpresa}")
    public ResponseEntity<List<ProyectoDto>> listarPorEmpresa(@PathVariable("idEmpresa") Integer idEmpresa) {
        List<Proyecto> proyectos = proyectoService.obtenerPorEmpresa(idEmpresa);
        if (proyectos.isEmpty()) return ResponseEntity.ok(new ArrayList<>());

        List<ProyectoDto> dtos = proyectos.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/proyecto/alumno/{boleta}")
    public ResponseEntity<ProyectoAlumnoResponseDto> listarParaAlumno(
            @PathVariable("boleta") Integer boleta) {
        try {
            List<Solicitud> aprobadas = solicitudRepository.findSolicitudAprobadaByBoleta(boleta);

            if (!aprobadas.isEmpty()) {
                Solicitud sol = aprobadas.get(0);
                Proyecto proyecto = sol.getProyecto();
                ProyectoDto proyectoDto = convertToDto(proyecto);

                String correoEmpresa = null;
                try {
                    if (proyecto.getEmpresa() != null) {
                        Usuario u = usuarioRepository.findByEmpresaId(proyecto.getEmpresa().getIdEmpresa()).orElse(null);
                        if (u != null) correoEmpresa = u.getCorreo();
                    }
                } catch (Exception e) {
                    System.err.println("Error obteniendo correo empresa: " + e.getMessage());
                }

                List<ProyectoDto> todosParaMostrar = proyectoService.getAll().stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList());

                return ResponseEntity.ok(ProyectoAlumnoResponseDto.builder()
                        .enProyecto(true)
                        .proyectoAsignado(proyectoDto)
                        .correoEmpresa(correoEmpresa)
                        .proyectos(todosParaMostrar)
                        .build());
            }

            List<Proyecto> todosProyectos = proyectoService.getAll();
            List<ProyectoDto> dtos = todosProyectos.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ProyectoAlumnoResponseDto.builder()
                    .enProyecto(false)
                    .proyectoAsignado(null)
                    .correoEmpresa(null)
                    .proyectos(dtos)
                    .build());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ProyectoAlumnoResponseDto.builder()
                    .enProyecto(false)
                    .proyectos(new ArrayList<>())
                    .build());
        }
    }

    @GetMapping("/proyecto/{id}")
    public ResponseEntity<ProyectoDto> getById(@PathVariable("id") Integer id) {
        Proyecto proyecto = proyectoService.getById(id);
        if (proyecto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(convertToDto(proyecto));
    }

    @PutMapping("/proyecto/{id}")
    public ResponseEntity<ProyectoDto> update(@PathVariable("id") Integer id, @RequestBody ProyectoDto dto) {
        Proyecto proyecto = convertToEntity(dto);
        Proyecto actualizado = proyectoService.update(id, proyecto);
        if (actualizado == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(convertToDto(actualizado));
    }

    @DeleteMapping("/proyecto/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        proyectoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/proyecto/{id}/imagen")
    public ResponseEntity<String> actualizarImagenProyecto(
            @PathVariable("id") Integer id,
            @RequestBody java.util.Map<String, String> body) {
        Proyecto proyecto = proyectoService.getById(id);
        if (proyecto == null) return ResponseEntity.notFound().build();

        proyecto.setImagenProyecto(body.get("imagenProyecto"));

        proyectoService.save(proyecto);
        return ResponseEntity.ok("Imagen del proyecto actualizada correctamente.");
    }

    @PutMapping("/proyecto/{id}/estado")
    public ResponseEntity<Void> actualizarEstado(
            @PathVariable("id") Integer id,
            @RequestParam("estado") String estado) {
        Proyecto proyecto = proyectoService.getById(id);
        if (proyecto == null) return ResponseEntity.notFound().build();
        proyecto.setEstadoProyecto(estado);
        proyectoService.save(proyecto);
        return ResponseEntity.ok().build();
    }

    private String obtenerTelefonoEmpresa(Integer idEmpresa) {
        try {
            Empresa empresa = empresaRepository.findById(idEmpresa).orElse(null);
            if (empresa != null && empresa.getTelefono() != null) {
                return empresa.getTelefono();
            }
            Usuario usuario = usuarioRepository.findByEmpresaId(idEmpresa).orElse(null);
            if (usuario != null && usuario.getTelefono() != null) {
                return usuario.getTelefono();
            }
        } catch (Exception e) {
            log.warn("Error obteniendo teléfono de empresa {}: {}", idEmpresa, e.getMessage());
        }
        return "No disponible";
    }

    private String obtenerFotoEmpresa(Integer idEmpresa) {
        try {
            Usuario u = usuarioRepository.findByEmpresaId(idEmpresa).orElse(null);
            if (u == null) return null;
            return perfilRepository.findByIdUsuario(u.getIdUsuario())
                    .map(Perfil::getFoto)
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    private ProyectoDto convertToDto(Proyecto proyecto) {
        Integer idEmpresa = proyecto.getEmpresa() != null ? proyecto.getEmpresa().getIdEmpresa() : null;

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
                .imagenProyecto(proyecto.getImagenProyecto())
                .idEmpresa(idEmpresa)
                .fotoEmpresa(obtenerFotoEmpresa(idEmpresa))
                .estadoProyecto(proyecto.getEstadoProyecto())
                .nombreEmpresa(proyecto.getEmpresa() != null ? proyecto.getEmpresa().getNombreEmpresa() : "Sin Empresa")
                .telefonoEmpresa(idEmpresa != null ? obtenerTelefonoEmpresa(idEmpresa) : "No disponible")
                .build();
    }

    private Proyecto convertToEntity(ProyectoDto dto) {
        Empresa empresa = new Empresa();
        empresa.setIdEmpresa(dto.getIdEmpresa());

        Date fechaRegistro = dto.getFechaInicio() == null ? new Date() : dto.getFechaInicio();

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
                .imagenProyecto(dto.getImagenProyecto())
                .empresa(empresa)
                .estadoProyecto(dto.getEstadoProyecto() != null ? dto.getEstadoProyecto() : "pendiente")
                .build();
    }
}
package com.SoLinX.controller;

import com.SoLinX.dto.ProyectoAlumnoResponseDto;
import com.SoLinX.dto.ProyectoDto;
import com.SoLinX.model.Empresa;
import com.SoLinX.model.Proyecto;
import com.SoLinX.model.Solicitud;
import com.SoLinX.model.Usuario;
import com.SoLinX.repository.EmpresaRepository;
import com.SoLinX.repository.SolicitudRepository;
import com.SoLinX.repository.UsuarioRepository;
import com.SoLinX.service.ProyectoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/SoLinX/api")
@RestController
@AllArgsConstructor
public class ProyectoController {

    private final ProyectoService proyectoService;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final SolicitudRepository solicitudRepository;


    @PostMapping("/proyecto")
    public ResponseEntity<ProyectoDto> save(@RequestBody ProyectoDto dto) {
        Proyecto proyecto = convertToEntity(dto);
        Proyecto guardado = proyectoService.save(proyecto);
        return ResponseEntity.ok(convertToDto(guardado));
    }

    @GetMapping("/proyecto")
    public ResponseEntity<List<ProyectoDto>> lista() {
        List<Proyecto> proyectos = proyectoService.getAll();
        if (proyectos.isEmpty()) return ResponseEntity.ok(new ArrayList<>());

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

    /**
     * NUEVO: Devuelve los proyectos que el alumno debe ver en su pantalla.
     *
     * 1. Si ya tiene una solicitud en estado 'aprobada' → devuelve enProyecto=true
     *    con la info del proyecto al que pertenece y el correo de la empresa.
     * 2. Si no → devuelve la lista de proyectos DISPONIBLES, excluyendo los que
     *    el alumno ya tiene en estados 'enviada', 'aprobada_supervisor', 'aceptada'.
     */
    @GetMapping("/proyecto/alumno/{boleta}")
    public ResponseEntity<ProyectoAlumnoResponseDto> listarParaAlumno(
            @PathVariable("boleta") Integer boleta) {
        try {
            // 1. Ver si el alumno ya fue aprobado finalmente en algún proyecto
            List<Solicitud> aprobadas = solicitudRepository.findSolicitudAprobadaByBoleta(boleta);

            if (!aprobadas.isEmpty()) {
                Solicitud sol = aprobadas.get(0);
                Proyecto proyecto = sol.getProyecto();
                ProyectoDto proyectoDto = convertToDto(proyecto);

                // Obtener correo de la empresa
                String correoEmpresa = null;
                try {
                    if (proyecto.getEmpresa() != null) {
                        Usuario u = usuarioRepository.findByEmpresaId(proyecto.getEmpresa().getIdEmpresa());
                        if (u != null) correoEmpresa = u.getCorreo();
                    }
                } catch (Exception e) {
                    System.err.println("Error obteniendo correo empresa: " + e.getMessage());
                }

                return ResponseEntity.ok(ProyectoAlumnoResponseDto.builder()
                        .enProyecto(true)
                        .proyectoAsignado(proyectoDto)
                        .correoEmpresa(correoEmpresa)
                        .proyectos(new ArrayList<>())
                        .build());
            }

            // 2. Obtener IDs de proyectos que el alumno ya tiene en proceso
            List<Integer> idsEnProceso = solicitudRepository.findIdProyectosEnProcesoPorBoleta(boleta);

            // 3. Obtener todos los proyectos y filtrar
            List<Proyecto> todosProyectos = proyectoService.getAll();
            List<ProyectoDto> dtos = todosProyectos.stream()
                    .filter(p -> !idsEnProceso.contains(p.getIdProyecto()))
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

        proyecto.setImagenRef(body.get("imagenProyecto"));

        proyectoService.save(proyecto);
        return ResponseEntity.ok("Imagen del proyecto actualizada correctamente.");
    }

    private String obtenerTelefonoEmpresa(Integer idEmpresa) {
        try {
            Empresa empresa = empresaRepository.findById(idEmpresa).orElse(null);
            if (empresa != null && empresa.getTelefono() != null) {
                return empresa.getTelefono();
            }
            Usuario usuario = usuarioRepository.findByEmpresaId(idEmpresa);
            if (usuario != null && usuario.getTelefono() != null) {
                return usuario.getTelefono();
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo teléfono: " + e.getMessage());
        }
        return "No disponible";
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
                .idEmpresa(idEmpresa)
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
                .empresa(empresa)
                .build();
    }
}
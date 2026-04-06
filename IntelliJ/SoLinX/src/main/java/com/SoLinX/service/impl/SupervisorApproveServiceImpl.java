package com.SoLinX.service.impl;

import com.SoLinX.dto.*;
import com.SoLinX.model.*;
import com.SoLinX.repository.*;
import com.SoLinX.service.SupervisorApproveService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class SupervisorApproveServiceImpl implements SupervisorApproveService {

    private final UsuarioRepository           usuarioRepository;
    private final SupervisorRepository        supervisorRepository;
    private final UsuarioSupervisorRepository usuarioSupervisorRepository;
    private final EmpresaRepository           empresaRepository;
    private final SolicitudRepository         solicitudRepository;
    private final EstudianteRepository        estudianteRepository;
    private final UsuarioEstudianteRepository usuarioEstudianteRepository;
    private final ProyectoRepository          proyectoRepository;

    @Override
    public SupervisorResponseDto getSupervisorData(Integer idUsuario) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
            if (usuarioOpt.isEmpty()) return error("Usuario no encontrado");

            Usuario usuario = usuarioOpt.get();
            UsuarioSupervisor usuarioSupervisor = usuarioSupervisorRepository.findByIdUsuario(idUsuario);
            if (usuarioSupervisor == null) return error("Supervisor no encontrado");

            Optional<Supervisor> supervisorOpt = supervisorRepository.findById(usuarioSupervisor.getIdSupervisor());
            if (supervisorOpt.isEmpty()) return error("Datos de supervisor no encontrados");

            Supervisor supervisor = supervisorOpt.get();
            Empresa empresa = supervisor.getEmpresa();

            SupervisorDto supervisorDTO = SupervisorDto.builder()
                    .idSupervisor(supervisor.getIdSupervisor())
                    .idUsuario(usuario.getIdUsuario())
                    .nombre(usuario.getNombre())
                    .correo(usuario.getCorreo())
                    .telefono(usuario.getTelefono())
                    .area(supervisor.getArea())
                    .idEmpresa(empresa != null ? empresa.getIdEmpresa() : null)
                    .nombreEmpresa(empresa != null ? empresa.getNombreEmpresa() : "Empresa no encontrada")
                    .build();

            return SupervisorResponseDto.builder()
                    .success(true)
                    .message("Datos obtenidos correctamente")
                    .supervisor(supervisorDTO)
                    .build();

        } catch (Exception e) {
            return error("Error al obtener datos: " + e.getMessage());
        }
    }

    @Override
    public SolicitudesResponseDto getSolicitudesEnviadas(Integer idSupervisor) {
        try {
            List<SolicitudDto> dtos = convertirLista(
                    solicitudRepository.findSolicitudesEnviadasBySupervisor(idSupervisor));
            return SolicitudesResponseDto.builder()
                    .success(true).message("Solicitudes obtenidas correctamente")
                    .solicitudes(dtos).build();
        } catch (Exception e) {
            return SolicitudesResponseDto.builder()
                    .success(false).message("Error: " + e.getMessage()).build();
        }
    }

    @Override
    public SolicitudesResponseDto getSolicitudesAceptadas(Integer idEmpresa) {
        try {
            List<SolicitudDto> dtos = convertirLista(
                    solicitudRepository.findSolicitudesAceptadasByEmpresa(idEmpresa));
            return SolicitudesResponseDto.builder()
                    .success(true).message("Aceptaciones obtenidas correctamente")
                    .solicitudes(dtos).build();
        } catch (Exception e) {
            return SolicitudesResponseDto.builder()
                    .success(false).message("Error: " + e.getMessage()).build();
        }
    }

    @Override
    public AprobacionResponseDto actualizarSolicitud(Integer idSolicitud, String nuevoEstado) {
        try {
            Optional<Solicitud> solicitudOpt = solicitudRepository.findById(idSolicitud);
            if (solicitudOpt.isEmpty()) return AprobacionResponseDto.builder()
                    .success(false).message("Solicitud no encontrada").build();

            Solicitud solicitud = solicitudOpt.get();
            solicitud.setEstadoSolicitud(nuevoEstado);
            solicitudRepository.save(solicitud);

            String mensaje = switch (nuevoEstado) {
                case "aceptada"  -> "Solicitud aceptada correctamente";
                case "aprobada"  -> "Solicitud aprobada correctamente";
                case "rechazada" -> "Solicitud rechazada";
                default          -> "Solicitud actualizada correctamente";
            };

            return AprobacionResponseDto.builder().success(true).message(mensaje).build();

        } catch (Exception e) {
            return AprobacionResponseDto.builder()
                    .success(false).message("Error al actualizar: " + e.getMessage()).build();
        }
    }

    // ─── Helpers ───────────────────────────────────────────────────────────────
    private List<SolicitudDto> convertirLista(List<Solicitud> solicitudes) {
        List<SolicitudDto> dtos = new ArrayList<>();
        for (Solicitud sol : solicitudes) {
            SolicitudDto dto = convertirSolicitudADTO(sol);
            if (dto != null) dtos.add(dto);
        }
        return dtos;
    }

    private SolicitudDto convertirSolicitudADTO(Solicitud solicitud) {
        try {
            Estudiante estudiante = solicitud.getEstudiante();
            if (estudiante == null) { log.warn("Estudiante no encontrado en solicitud ID: {}", solicitud.getIdSolicitud()); return null; }

            UsuarioEstudiante ue = usuarioEstudianteRepository.findByBoleta(estudiante.getBoleta()).orElse(null);
            if (ue == null) { log.warn("UsuarioEstudiante no encontrado para boleta: {}", estudiante.getBoleta()); return null; }

            Usuario usuario = usuarioRepository.findById(ue.getIdUsuario()).orElse(null);
            if (usuario == null) { log.warn("Usuario no encontrado para idUsuario: {}", ue.getIdUsuario()); return null; }

            Proyecto proyecto = solicitud.getProyecto();
            if (proyecto == null) { log.warn("Proyecto no encontrado en solicitud ID: {}", solicitud.getIdSolicitud()); return null; }

            Empresa empresa = proyecto.getEmpresa();

            return SolicitudDto.builder()
                    .idSolicitud(solicitud.getIdSolicitud())
                    .fechaSolicitud(solicitud.getFechaSolicitud().toString())
                    .estadoSolicitud(solicitud.getEstadoSolicitud())
                    .boleta(estudiante.getBoleta())
                    .nombreEstudiante(usuario.getNombre())
                    .carrera(estudiante.getCarrera())
                    .escuela(estudiante.getEscuela())
                    .idProyecto(proyecto.getIdProyecto())
                    .nombreProyecto(proyecto.getNombreProyecto())
                    .nombreEmpresa(empresa != null ? empresa.getNombreEmpresa() : "Empresa no encontrada")
                    .build();

        } catch (Exception e) {
            log.error("Error en convertirSolicitudADTO: {}", e.getMessage());
            return null;
        }
    }

    private SupervisorResponseDto error(String mensaje) {
        return SupervisorResponseDto.builder().success(false).message(mensaje).supervisor(null).build();
    }
}
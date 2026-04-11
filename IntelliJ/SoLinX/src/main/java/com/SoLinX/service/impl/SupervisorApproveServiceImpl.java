package com.SoLinX.service.impl;

import com.SoLinX.dto.*;
import com.SoLinX.model.*;
import com.SoLinX.repository.*;
import com.SoLinX.service.NotificacionService;
import com.SoLinX.service.SupervisorApproveService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
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
    private final NotificacionService         notificacionService;

    private static final SimpleDateFormat FORMATO_FECHA =
            new SimpleDateFormat("dd/MM/yyyy HH:mm");

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
                    .success(false).message("Error: " + e.getMessage())
                    .solicitudes(new ArrayList<>()).build();
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
                    .success(false).message("Error: " + e.getMessage())
                    .solicitudes(new ArrayList<>()).build();
        }
    }

    @Override
    public AprobacionResponseDto actualizarSolicitud(Integer idSolicitud, String nuevoEstado) {
        try {
            Optional<Solicitud> solicitudOpt = solicitudRepository.findById(idSolicitud);
            if (solicitudOpt.isEmpty()) return AprobacionResponseDto.builder()
                    .success(false).message("Solicitud no encontrada").build();

            Solicitud solicitud = solicitudOpt.get();

            // Normalizar valores que llegan del Android según el estado actual
            String estadoActual = solicitud.getEstadoSolicitud();
            String estadoFinal = nuevoEstado;

            if ("aprobada".equalsIgnoreCase(nuevoEstado)) {
                if ("enviada".equalsIgnoreCase(estadoActual)) {
                    estadoFinal = "aprobada_supervisor";
                }
            } else if ("rechazada".equalsIgnoreCase(nuevoEstado)) {
                if ("enviada".equalsIgnoreCase(estadoActual)) {
                    estadoFinal = "rechazada_supervisor";
                }
            }

            solicitud.setEstadoSolicitud(estadoFinal);
            solicitudRepository.save(solicitud);

            // Obtener datos del alumno para notificar
            String nombreProyecto = solicitud.getProyecto() != null
                    ? solicitud.getProyecto().getNombreProyecto()
                    : "proyecto";
            Integer idUsuarioAlumno = null;
            try {
                idUsuarioAlumno = solicitud.getEstudiante().getUsuarioEstudiante().getIdUsuario();
            } catch (Exception e) {
                log.warn("No se pudo obtener idUsuario del alumno: {}", e.getMessage());
            }

            String mensaje;
            String tituloNotif = null;
            String mensajeNotif = null;

            switch (estadoFinal) {
                case "aprobada_supervisor":
                    mensaje = "Solicitud aprobada. Ahora la empresa podrá revisarla.";
                    tituloNotif = "Solicitud aprobada por el supervisor";
                    mensajeNotif = "Tu solicitud al proyecto \"" + nombreProyecto +
                            "\" fue aprobada por el supervisor. Ahora la empresa la revisará.";
                    break;
                case "rechazada_supervisor":
                    mensaje = "Solicitud rechazada por el supervisor.";
                    tituloNotif = "Solicitud rechazada";
                    mensajeNotif = "Tu solicitud al proyecto \"" + nombreProyecto +
                            "\" fue rechazada por el supervisor.";
                    break;
                case "aprobada":
                    mensaje = "¡Visto bueno final otorgado! El alumno queda confirmado.";
                    tituloNotif = "¡Felicidades! Confirmación final 🎉";
                    mensajeNotif = "Tu participación en el proyecto \"" + nombreProyecto +
                            "\" ha sido confirmada oficialmente por el supervisor.";

                    // Cuando se da visto bueno final, descontar vacante y rechazar las demás
                    try {
                        Proyecto proyecto = solicitud.getProyecto();
                        if (proyecto != null) {
                            proyecto.setVacantes(Math.max(0, proyecto.getVacantes() - 1));
                            proyectoRepository.save(proyecto);
                        }

                        Integer boleta = solicitud.getEstudiante().getBoleta();
                        List<Solicitud> pendientes = solicitudRepository
                                .findSolicitudesPendientesByBoletaExcluding(boleta, idSolicitud);
                        for (Solicitud pendiente : pendientes) {
                            pendiente.setEstadoSolicitud("rechazada_supervisor");
                        }
                        solicitudRepository.saveAll(pendientes);
                    } catch (Exception e) {
                        log.error("Error al procesar paso final: {}", e.getMessage());
                    }
                    break;
                case "rechazada":
                    mensaje = "Solicitud rechazada en el visto bueno final.";
                    tituloNotif = "Solicitud rechazada";
                    mensajeNotif = "El supervisor rechazó el visto bueno final de tu solicitud al proyecto \""
                            + nombreProyecto + "\".";
                    break;
                default:
                    mensaje = "Solicitud actualizada.";
                    break;
            }

            if (idUsuarioAlumno != null && tituloNotif != null) {
                try {
                    notificacionService.crear(idUsuarioAlumno, tituloNotif, mensajeNotif);
                } catch (Exception e) {
                    log.error("Error al enviar notificación: {}", e.getMessage());
                }
            }

            return AprobacionResponseDto.builder()
                    .success(true)
                    .message(mensaje)
                    .build();

        } catch (Exception e) {
            log.error("Error general en actualizarSolicitud: ", e);
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

            // Usando un enfoque más seguro sin usar Optional directamente en un findBy que devuelve entidad
            UsuarioEstudiante ue = usuarioEstudianteRepository.findByBoleta(estudiante.getBoleta()).orElse(null);
            if (ue == null) { log.warn("UsuarioEstudiante no encontrado para boleta: {}", estudiante.getBoleta()); return null; }

            Usuario usuario = usuarioRepository.findById(ue.getIdUsuario()).orElse(null);
            if (usuario == null) { log.warn("Usuario no encontrado para idUsuario: {}", ue.getIdUsuario()); return null; }

            Proyecto proyecto = solicitud.getProyecto();
            if (proyecto == null) { log.warn("Proyecto no encontrado en solicitud ID: {}", solicitud.getIdSolicitud()); return null; }

            Empresa empresa = proyecto.getEmpresa();
            String nombreEmpresa = empresa != null ? empresa.getNombreEmpresa() : "Empresa no encontrada";

            // Correo de la empresa (Lógica tuya)
            String correoEmpresa = null;
            if (empresa != null) {
                try {
                    Usuario usuarioEmpresa = usuarioRepository.findByEmpresaId(empresa.getIdEmpresa()).orElse(null);
                    if (usuarioEmpresa != null) {
                        correoEmpresa = usuarioEmpresa.getCorreo();
                    }
                } catch (Exception e) {
                    log.warn("Error obteniendo correo empresa: {}", e.getMessage());
                }
            }

            String fechaSolicitudStr = solicitud.getFechaSolicitud() != null
                    ? FORMATO_FECHA.format(solicitud.getFechaSolicitud()) : null;
            String fechaAceptacionStr = solicitud.getFechaAceptacion() != null
                    ? FORMATO_FECHA.format(solicitud.getFechaAceptacion()) : null;

            return SolicitudDto.builder()
                    .idSolicitud(solicitud.getIdSolicitud())
                    .fechaSolicitud(fechaSolicitudStr)
                    .fechaAceptacion(fechaAceptacionStr)
                    .estadoSolicitud(solicitud.getEstadoSolicitud())
                    .boleta(estudiante.getBoleta())
                    .nombreEstudiante(usuario.getNombre())
                    .correoEstudiante(usuario.getCorreo())
                    .carrera(estudiante.getCarrera())
                    .escuela(estudiante.getEscuela())
                    .idProyecto(proyecto.getIdProyecto())
                    .nombreProyecto(proyecto.getNombreProyecto())
                    .nombreEmpresa(nombreEmpresa)
                    .correoEmpresa(correoEmpresa)
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
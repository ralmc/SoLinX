package com.SoLinX.service.impl;

import com.SoLinX.dto.*;
import com.SoLinX.model.*;
import com.SoLinX.repository.*;
import com.SoLinX.service.NotificacionService;
import com.SoLinX.service.SupervisorApproveService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SupervisorApproveServiceImpl implements SupervisorApproveService {

    private final UsuarioRepository usuarioRepository;
    private final SupervisorRepository supervisorRepository;
    private final UsuarioSupervisorRepository usuarioSupervisorRepository;
    private final EmpresaRepository empresaRepository;
    private final SolicitudRepository solicitudRepository;
    private final EstudianteRepository estudianteRepository;
    private final UsuarioEstudianteRepository usuarioEstudianteRepository;
    private final ProyectoRepository proyectoRepository;
    private final NotificacionService notificacionService;

    private static final SimpleDateFormat FORMATO_FECHA =
            new SimpleDateFormat("dd/MM/yyyy HH:mm");

    @Override
    public SupervisorResponseDto getSupervisorData(Integer idUsuario) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
            if (usuarioOpt.isEmpty()) {
                return SupervisorResponseDto.builder()
                        .success(false)
                        .message("Usuario no encontrado")
                        .supervisor(null)
                        .build();
            }

            Usuario usuario = usuarioOpt.get();

            UsuarioSupervisor usuarioSupervisor = usuarioSupervisorRepository.findByIdUsuario(idUsuario);
            if (usuarioSupervisor == null) {
                return SupervisorResponseDto.builder()
                        .success(false)
                        .message("Supervisor no encontrado")
                        .supervisor(null)
                        .build();
            }

            Optional<Supervisor> supervisorOpt = supervisorRepository.findById(usuarioSupervisor.getIdSupervisor());
            if (supervisorOpt.isEmpty()) {
                return SupervisorResponseDto.builder()
                        .success(false)
                        .message("Datos de supervisor no encontrados")
                        .supervisor(null)
                        .build();
            }

            Supervisor supervisor = supervisorOpt.get();

            Empresa empresa = supervisor.getEmpresa();
            String nombreEmpresa = empresa != null ? empresa.getNombreEmpresa() : "Empresa no encontrada";
            Integer idEmpresa = empresa != null ? empresa.getIdEmpresa() : null;

            SupervisorDto supervisorDTO = SupervisorDto.builder()
                    .idSupervisor(supervisor.getIdSupervisor())
                    .idUsuario(usuario.getIdUsuario())
                    .nombre(usuario.getNombre())
                    .correo(usuario.getCorreo())
                    .telefono(usuario.getTelefono())
                    .area(supervisor.getArea())
                    .idEmpresa(idEmpresa)
                    .nombreEmpresa(nombreEmpresa)
                    .build();

            return SupervisorResponseDto.builder()
                    .success(true)
                    .message("Datos obtenidos correctamente")
                    .supervisor(supervisorDTO)
                    .build();

        } catch (Exception e) {
            return SupervisorResponseDto.builder()
                    .success(false)
                    .message("Error al obtener datos: " + e.getMessage())
                    .supervisor(null)
                    .build();
        }
    }

    @Override
    public SolicitudesResponseDto getSolicitudesEnviadas(Integer idSupervisor) {
        try {
            List<Solicitud> solicitudes = solicitudRepository.findSolicitudesEnviadasBySupervisor(idSupervisor);

            List<SolicitudDto> solicitudesDTO = new ArrayList<>();
            for (Solicitud sol : solicitudes) {
                SolicitudDto dto = convertirSolicitudADTO(sol);
                if (dto != null) {
                    solicitudesDTO.add(dto);
                }
            }

            return SolicitudesResponseDto.builder()
                    .success(true)
                    .message("Solicitudes obtenidas correctamente")
                    .solicitudes(solicitudesDTO)
                    .build();

        } catch (Exception e) {
            return SolicitudesResponseDto.builder()
                    .success(false)
                    .message("Error al obtener solicitudes: " + e.getMessage())
                    .solicitudes(new ArrayList<>())
                    .build();
        }
    }

    @Override
    public SolicitudesResponseDto getSolicitudesAceptadas(Integer idEmpresa) {
        try {
            List<Solicitud> solicitudes = solicitudRepository.findSolicitudesAceptadasByEmpresa(idEmpresa);

            List<SolicitudDto> solicitudesDTO = new ArrayList<>();
            for (Solicitud sol : solicitudes) {
                SolicitudDto dto = convertirSolicitudADTO(sol);
                if (dto != null) {
                    solicitudesDTO.add(dto);
                }
            }

            return SolicitudesResponseDto.builder()
                    .success(true)
                    .message("Aceptaciones obtenidas correctamente")
                    .solicitudes(solicitudesDTO)
                    .build();

        } catch (Exception e) {
            return SolicitudesResponseDto.builder()
                    .success(false)
                    .message("Error al obtener aceptaciones: " + e.getMessage())
                    .solicitudes(new ArrayList<>())
                    .build();
        }
    }

    /**
     * Actualiza el estado de una solicitud desde el flujo del supervisor.
     *
     * Estados que acepta el supervisor:
     *   - "aprobada_supervisor"  → Paso 1: supervisor aprueba, pasa a empresa
     *   - "rechazada_supervisor" → Paso 1: supervisor rechaza
     *   - "aprobada"             → Paso 4: supervisor da visto bueno final
     *   - "rechazada"            → Paso 4: supervisor rechaza el visto bueno final
     *
     * En cada caso se notifica al alumno.
     */
    @Override
    public AprobacionResponseDto actualizarSolicitud(Integer idSolicitud, String nuevoEstado) {
        try {
            Optional<Solicitud> solicitudOpt = solicitudRepository.findById(idSolicitud);

            if (solicitudOpt.isEmpty()) {
                return AprobacionResponseDto.builder()
                        .success(false)
                        .message("Solicitud no encontrada")
                        .build();
            }

            Solicitud solicitud = solicitudOpt.get();

            // Normalizar valores que llegan del Android (aprobada/rechazada) según el estado actual
            String estadoActual = solicitud.getEstadoSolicitud();
            String estadoFinal = nuevoEstado;

            // Si viene "aprobada" o "rechazada" genéricas, determinar cuál es según el paso en el que está
            if ("aprobada".equalsIgnoreCase(nuevoEstado)) {
                if ("enviada".equalsIgnoreCase(estadoActual)) {
                    // Paso 1: supervisor está aprobando por primera vez
                    estadoFinal = "aprobada_supervisor";
                }
                // Si ya estaba en 'aceptada' (empresa admitió), queda 'aprobada' (visto bueno final)
            } else if ("rechazada".equalsIgnoreCase(nuevoEstado)) {
                if ("enviada".equalsIgnoreCase(estadoActual)) {
                    estadoFinal = "rechazada_supervisor";
                }
                // Si estaba en 'aceptada', queda 'rechazada' (rechazo final)
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
                System.out.println("No se pudo obtener idUsuario del alumno: " + e.getMessage());
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

                    // Cuando se da visto bueno final, descontar vacante y rechazar las demás pendientes del alumno
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
                        System.out.println("Error al procesar paso final: " + e.getMessage());
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
                    System.out.println("Error al enviar notificación: " + e.getMessage());
                }
            }

            return AprobacionResponseDto.builder()
                    .success(true)
                    .message(mensaje)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return AprobacionResponseDto.builder()
                    .success(false)
                    .message("Error al actualizar: " + e.getMessage())
                    .build();
        }
    }

    private SolicitudDto convertirSolicitudADTO(Solicitud solicitud) {
        try {
            Estudiante estudiante = solicitud.getEstudiante();
            if (estudiante == null) return null;

            UsuarioEstudiante usuarioEstudiante = usuarioEstudianteRepository.findByBoleta(estudiante.getBoleta());
            if (usuarioEstudiante == null) return null;

            Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioEstudiante.getIdUsuario());
            if (usuarioOpt.isEmpty()) return null;
            Usuario usuario = usuarioOpt.get();

            Proyecto proyecto = solicitud.getProyecto();
            if (proyecto == null) return null;

            Empresa empresa = proyecto.getEmpresa();
            String nombreEmpresa = empresa != null ? empresa.getNombreEmpresa() : "Empresa no encontrada";

            // Correo de la empresa
            String correoEmpresa = null;
            if (empresa != null) {
                try {
                    Usuario usuarioEmpresa = usuarioRepository.findByEmpresaId(empresa.getIdEmpresa());
                    if (usuarioEmpresa != null) {
                        correoEmpresa = usuarioEmpresa.getCorreo();
                    }
                } catch (Exception e) {
                    System.out.println("Error obteniendo correo empresa: " + e.getMessage());
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
            System.out.println("Error en convertirSolicitudADTO: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}

package com.SoLinX.service.impl;

import com.SoLinX.dto.*;
import com.SoLinX.model.*;
import com.SoLinX.repository.*;
import com.SoLinX.service.SupervisorApproveService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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

    @Override
    public SupervisorResponseDto getSupervisorData(Integer idUsuario) {
        try {
            // Buscar usuario
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
            if (usuarioOpt.isEmpty()) {
                return SupervisorResponseDto.builder()
                        .success(false)
                        .message("Usuario no encontrado")
                        .supervisor(null)
                        .build();
            }

            Usuario usuario = usuarioOpt.get();

            // Buscar relación UsuarioSupervisor
            UsuarioSupervisor usuarioSupervisor = usuarioSupervisorRepository.findByIdUsuario(idUsuario);
            if (usuarioSupervisor == null) {
                return SupervisorResponseDto.builder()
                        .success(false)
                        .message("Supervisor no encontrado")
                        .supervisor(null)
                        .build();
            }

            // Buscar Supervisor
            Optional<Supervisor> supervisorOpt = supervisorRepository.findById(usuarioSupervisor.getIdSupervisor());
            if (supervisorOpt.isEmpty()) {
                return SupervisorResponseDto.builder()
                        .success(false)
                        .message("Datos de supervisor no encontrados")
                        .supervisor(null)
                        .build();
            }

            Supervisor supervisor = supervisorOpt.get();

            // Usar la relación @ManyToOne
            Empresa empresa = supervisor.getEmpresa();
            String nombreEmpresa = empresa != null ? empresa.getNombreEmpresa() : "Empresa no encontrada";
            Integer idEmpresa = empresa != null ? empresa.getIdEmpresa() : null;

            // Construir DTO
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
            // Obtener solicitudes con estado 'enviada'
            List<Solicitud> solicitudes = solicitudRepository.findSolicitudesEnviadasBySupervisor(idSupervisor);

            // Convertir a DTOs
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
                    .solicitudes(null)
                    .build();
        }
    }

    @Override
    public SolicitudesResponseDto getSolicitudesAceptadas(Integer idEmpresa) {
        try {
            // Obtener solicitudes con estado 'aceptada'
            List<Solicitud> solicitudes = solicitudRepository.findSolicitudesAceptadasByEmpresa(idEmpresa);

            // Convertir a DTOs
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
                    .solicitudes(null)
                    .build();
        }
    }

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
            solicitud.setEstadoSolicitud(nuevoEstado);
            solicitudRepository.save(solicitud);

            String mensaje;
            switch (nuevoEstado) {
                case "aceptada":
                    mensaje = "Solicitud aceptada correctamente";
                    break;
                case "aprobada":
                    mensaje = "Solicitud aprobada correctamente";
                    break;
                case "rechazada":
                    mensaje = "Solicitud rechazada";
                    break;
                default:
                    mensaje = "Solicitud actualizada correctamente";
                    break;
            }

            return AprobacionResponseDto.builder()
                    .success(true)
                    .message(mensaje)
                    .build();

        } catch (Exception e) {
            return AprobacionResponseDto.builder()
                    .success(false)
                    .message("Error al actualizar: " + e.getMessage())
                    .build();
        }
    }

    // ✅ MÉTODO AUXILIAR CORREGIDO
    private SolicitudDto convertirSolicitudADTO(Solicitud solicitud) {
        try {
            // ✅ Obtener Estudiante desde la relación @ManyToOne
            Estudiante estudiante = solicitud.getEstudiante();
            if (estudiante == null) {
                System.out.println("❌ Estudiante no encontrado en solicitud ID: " + solicitud.getIdSolicitud());
                return null;
            }

            // ✅ Buscar Usuario del Estudiante usando la boleta
            UsuarioEstudiante usuarioEstudiante = usuarioEstudianteRepository.findByBoleta(estudiante.getBoleta());
            if (usuarioEstudiante == null) {
                System.out.println("❌ UsuarioEstudiante no encontrado para boleta: " + estudiante.getBoleta());
                return null;
            }

            Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioEstudiante.getIdUsuario());
            if (usuarioOpt.isEmpty()) {
                System.out.println("❌ Usuario no encontrado para idUsuario: " + usuarioEstudiante.getIdUsuario());
                return null;
            }
            Usuario usuario = usuarioOpt.get();

            // ✅ Obtener Proyecto desde la relación @ManyToOne
            Proyecto proyecto = solicitud.getProyecto();
            if (proyecto == null) {
                System.out.println("❌ Proyecto no encontrado en solicitud ID: " + solicitud.getIdSolicitud());
                return null;
            }

            // ✅ Obtener Empresa desde la relación @ManyToOne del Proyecto
            Empresa empresa = proyecto.getEmpresa();
            String nombreEmpresa = empresa != null ? empresa.getNombreEmpresa() : "Empresa no encontrada";

            // Construir DTO
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
                    .nombreEmpresa(nombreEmpresa)
                    .build();

        } catch (Exception e) {
            System.out.println("❌ Error en convertirSolicitudADTO: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
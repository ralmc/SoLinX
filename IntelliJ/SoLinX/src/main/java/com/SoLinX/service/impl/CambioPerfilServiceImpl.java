package com.SoLinX.service.impl;

import com.SoLinX.dto.CambioPerfilDto;
import com.SoLinX.model.*;
import com.SoLinX.repository.*;
import com.SoLinX.service.CambioPerfilService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class CambioPerfilServiceImpl implements CambioPerfilService {

    private final CambioPerfilRepository cambioPerfilRepository;
    private final UsuarioRepository      usuarioRepository;
    private final EstudianteRepository   estudianteRepository;
    private final EmpresaRepository      empresaRepository;

    @Override
    public CambioPerfilDto solicitar(Integer idUsuario, String rol,
                                     String campo, String valorAnterior, String valorNuevo) {
        Usuario usuario = Usuario.builder().idUsuario(idUsuario).build();
        CambioPerfil cambio = CambioPerfil.builder()
                .usuario(usuario)
                .rol(rol)
                .campo(campo)
                .valorAnterior(valorAnterior)
                .valorNuevo(valorNuevo)
                .estado("pendiente")
                .fechaSolicitud(new Date())
                .build();
        return toDto(cambioPerfilRepository.save(cambio));
    }

    @Override
    public List<CambioPerfilDto> obtenerPendientes() {
        return cambioPerfilRepository
                .findByEstadoOrderByFechaSolicitudDesc("pendiente")
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<CambioPerfilDto> obtenerPorUsuario(Integer idUsuario) {
        return cambioPerfilRepository
                .findByUsuario_IdUsuarioOrderByFechaSolicitudDesc(idUsuario)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CambioPerfilDto aprobar(Integer idCambio) {
        CambioPerfil cambio = cambioPerfilRepository.findById(idCambio)
                .orElseThrow(() -> new RuntimeException("Cambio no encontrado: " + idCambio));

        cambio.setEstado("aprobado");
        cambio.setFechaResolucion(new Date());
        cambioPerfilRepository.save(cambio);

        // ─── Aplicar cambio en la BD ─────────────────────────
        Integer idUsuario = cambio.getUsuario().getIdUsuario();
        String campo      = cambio.getCampo();
        String valor      = cambio.getValorNuevo();
        String rol        = cambio.getRol();

        try {
            // Campos de Usuario
            if ("nombre".equalsIgnoreCase(campo)) {
                usuarioRepository.findById(idUsuario).ifPresent(u -> {
                    u.setNombre(valor);
                    usuarioRepository.save(u);
                });
            }
            // Campos de Estudiante
            else if ("carrera".equalsIgnoreCase(campo) && "estudiante".equalsIgnoreCase(rol)) {
                estudianteRepository.findByUsuarioEstudiante_IdUsuario(idUsuario).ifPresent(e -> {
                    e.setCarrera(valor);
                    estudianteRepository.save(e);
                });
            }
            else if ("escuela".equalsIgnoreCase(campo) && "estudiante".equalsIgnoreCase(rol)) {
                estudianteRepository.findByUsuarioEstudiante_IdUsuario(idUsuario).ifPresent(e -> {
                    e.setEscuela(valor);
                    estudianteRepository.save(e);
                });
            }
            // Campos de Empresa
            else if ("nombreEmpresa".equalsIgnoreCase(campo) && "empresa".equalsIgnoreCase(rol)) {
                empresaRepository.findByUsuarioEmpresa_IdUsuario(idUsuario).ifPresent(e -> {
                    e.setNombreEmpresa(valor);
                    empresaRepository.save(e);
                });
            }
            else if ("telefono".equalsIgnoreCase(campo) && "empresa".equalsIgnoreCase(rol)) {
                empresaRepository.findByUsuarioEmpresa_IdUsuario(idUsuario).ifPresent(e -> {
                    e.setTelefono(valor);
                    empresaRepository.save(e);
                });
            }
        } catch (Exception ex) {
            log.error("Error aplicando cambio de perfil idCambio={}: {}", idCambio, ex.getMessage());
        }

        return toDto(cambio);
    }

    @Override
    public CambioPerfilDto rechazar(Integer idCambio) {
        CambioPerfil cambio = cambioPerfilRepository.findById(idCambio)
                .orElseThrow(() -> new RuntimeException("Cambio no encontrado: " + idCambio));
        cambio.setEstado("rechazado");
        cambio.setFechaResolucion(new Date());
        return toDto(cambioPerfilRepository.save(cambio));
    }

    private CambioPerfilDto toDto(CambioPerfil c) {
        return CambioPerfilDto.builder()
                .idCambio(c.getIdCambio())
                .idUsuario(c.getUsuario().getIdUsuario())
                .nombreUsuario(c.getUsuario().getNombre())
                .rol(c.getRol())
                .campo(c.getCampo())
                .valorAnterior(c.getValorAnterior())
                .valorNuevo(c.getValorNuevo())
                .estado(c.getEstado())
                .fechaSolicitud(c.getFechaSolicitud() != null
                        ? new Timestamp(c.getFechaSolicitud().getTime()).toString() : null)
                .fechaResolucion(c.getFechaResolucion() != null
                        ? new Timestamp(c.getFechaResolucion().getTime()).toString() : null)
                .build();
    }
}
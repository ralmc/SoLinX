package com.SoLinX.service.impl;

import com.SoLinX.dto.NotificacionDto;
import com.SoLinX.model.Notificacion;
import com.SoLinX.model.Usuario;
import com.SoLinX.repository.NotificacionRepository;
import com.SoLinX.service.NotificacionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository notificacionRepository;

    @Override
    public List<NotificacionDto> obtenerPorUsuario(Integer idUsuario) {
        return notificacionRepository
                .findByUsuario_IdUsuarioOrderByFechaCreacionDesc(idUsuario)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public NotificacionDto crear(Integer idUsuario, String titulo, String mensaje) {
        Usuario usuario = Usuario.builder().idUsuario(idUsuario).build();

        Notificacion n = Notificacion.builder()
                .titulo(titulo)
                .mensaje(mensaje)
                .fechaCreacion(new Date())
                .leida(false)
                .usuario(usuario)
                .build();

        return toDto(notificacionRepository.save(n));
    }

    @Override
    public void marcarComoLeida(Integer idNotificacion) {
        notificacionRepository.findById(idNotificacion).ifPresent(n -> {
            n.setLeida(true);
            notificacionRepository.save(n);
        });
    }

    @Override
    public void marcarTodasComoLeidas(Integer idUsuario) {
        notificacionRepository.marcarTodasComoLeidas(idUsuario);
    }

    @Override
    public Integer contarNoLeidas(Integer idUsuario) {
        return notificacionRepository.contarNoLeidas(idUsuario);
    }

    private NotificacionDto toDto(Notificacion n) {
        return NotificacionDto.builder()
                .idNotificacion(n.getIdNotificacion())
                .titulo(n.getTitulo())
                .mensaje(n.getMensaje())
                .fechaCreacion(n.getFechaCreacion() != null ?
                        new Timestamp(n.getFechaCreacion().getTime()).toString() : null)
                .leida(n.getLeida())
                .idUsuario(n.getUsuario().getIdUsuario())
                .build();
    }
}

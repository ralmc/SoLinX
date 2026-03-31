package com.SoLinX.service;

import com.SoLinX.dto.NotificacionDto;
import java.util.List;

public interface NotificacionService {
    List<NotificacionDto> obtenerPorUsuario(Integer idUsuario);
    NotificacionDto crear(Integer idUsuario, String titulo, String mensaje);
    void marcarComoLeida(Integer idNotificacion);
    void marcarTodasComoLeidas(Integer idUsuario);
    Integer contarNoLeidas(Integer idUsuario);
}

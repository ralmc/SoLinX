package com.SoLinX.service;

import com.SoLinX.dto.CambioPerfilDto;
import java.util.List;

public interface CambioPerfilService {
    CambioPerfilDto solicitar(Integer idUsuario, String rol, String campo, String valorAnterior, String valorNuevo);
    List<CambioPerfilDto> obtenerPendientes();
    List<CambioPerfilDto> obtenerPorUsuario(Integer idUsuario);
    CambioPerfilDto aprobar(Integer idCambio);
    CambioPerfilDto rechazar(Integer idCambio);
}
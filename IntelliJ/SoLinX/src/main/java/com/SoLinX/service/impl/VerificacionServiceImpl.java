package com.SoLinX.service.impl;

import com.SoLinX.model.TokenVerificacion;
import com.SoLinX.model.Usuario;
import com.SoLinX.repository.TokenVerificacionRepository;
import com.SoLinX.repository.UsuarioRepository;
import com.SoLinX.service.MailService;
import com.SoLinX.service.VerificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificacionServiceImpl implements VerificacionService {

    private final TokenVerificacionRepository tokenRepo;
    private final UsuarioRepository usuarioRepo;
    private final MailService mailService;

    @Value("${app.verificacion.dias-expiracion:1}")
    private int diasExpiracion;

    @Override
    @Transactional
    public String verificarToken(String token) {
        Optional<TokenVerificacion> opt = tokenRepo.findByToken(token);

        if (opt.isEmpty()) return "Token inválido o no encontrado.";

        TokenVerificacion tv = opt.get();

        if (tv.getUsado()) return "Este enlace ya fue utilizado. Tu cuenta ya está verificada.";

        if (tv.getFechaExpira().isBefore(LocalDateTime.now()))
            return "El enlace ha expirado. Inicia sesión de nuevo para recibir otro.";

        tv.setUsado(true);
        tokenRepo.save(tv);

        Usuario usuario = tv.getUsuario();
        usuario.setVerificado(true);
        usuarioRepo.save(usuario);

        log.info("Usuario verificado: {}", usuario.getCorreo());
        return "¡Cuenta verificada! Ya puedes iniciar sesión en SoLinX.";
    }

    @Override
    @Transactional
    public void generarYEnviarToken(Usuario usuario) {
        Optional<TokenVerificacion> activo = tokenRepo
                .findByUsuarioAndUsadoFalseAndFechaExpiraAfter(usuario, LocalDateTime.now());

        if (activo.isPresent()) {
            log.info("Token activo ya existe para: {}", usuario.getCorreo());
            return;
        }

        String uuid = UUID.randomUUID().toString();
        tokenRepo.save(TokenVerificacion.builder()
                .token(uuid)
                .fechaExpira(LocalDateTime.now().plusDays(diasExpiracion))
                .usado(false)
                .usuario(usuario)
                .build());

        mailService.enviarCorreoVerificacion(usuario.getCorreo(), usuario.getNombre(), uuid);
        log.info("Token enviado a: {}", usuario.getCorreo());
    }
}
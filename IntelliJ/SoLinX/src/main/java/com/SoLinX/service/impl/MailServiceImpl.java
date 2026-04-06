package com.SoLinX.service.impl;

import com.SoLinX.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public void enviarCorreoVerificacion(String destinatario, String nombre, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("ralm.company9@gmail.com");
            helper.setTo(destinatario);
            helper.setSubject("SoLinX – Verifica tu cuenta");

            String link = baseUrl + "/SoLinX/api/auth/verificar?token=" + token;

            String html = """
                <div style="font-family:Arial,sans-serif;max-width:600px;margin:auto;
                            padding:24px;border:1px solid #e0e0e0;border-radius:8px;">
                    <h2 style="color:#1565C0;">¡Bienvenido a SoLinX, %s!</h2>
                    <p>Gracias por registrarte. Haz clic en el botón para activar tu cuenta:</p>
                    <a href="%s"
                       style="display:inline-block;padding:12px 24px;background:#1565C0;
                              color:#fff;text-decoration:none;border-radius:6px;font-weight:bold;">
                        Verificar mi cuenta
                    </a>
                    <p style="margin-top:20px;color:#757575;font-size:13px;">
                        Este enlace expira en 24 horas.<br/>
                        Si no creaste esta cuenta, ignora este correo.
                    </p>
                </div>
                """.formatted(nombre, link);

            helper.setText(html, true);
            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Error al enviar correo: " + e.getMessage(), e);
        }
    }
}
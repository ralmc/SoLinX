package com.SoLinX.service;

import com.SoLinX.model.Usuario;
import com.SoLinX.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class PasswordMigrationService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostConstruct
    public void migrarPasswords() {

        List<Usuario> usuarios = usuarioRepository.findAll();

        for (Usuario usuario : usuarios) {

            String pass = usuario.getUserPassword();

            if (!esHashBCrypt(pass)) {

                String hash = passwordEncoder.encode(pass);
                usuario.setUserPassword(hash);

                usuarioRepository.save(usuario);

                System.out.println("✔ Migrado: " + usuario.getCorreo());
            }
        }
    }

    private boolean esHashBCrypt(String password) {
        return password != null &&
                (password.startsWith("$2a$") || password.startsWith("$2b$"));
    }
}
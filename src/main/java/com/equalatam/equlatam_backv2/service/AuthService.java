package com.equalatam.equlatam_backv2.service;

import com.equalatam.equlatam_backv2.dto.request.LoginRequest;
import com.equalatam.equlatam_backv2.dto.response.LoginResponse;
import com.equalatam.equlatam_backv2.entity.User;
import com.equalatam.equlatam_backv2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.equalatam.equlatam_backv2.repository.UserRepository;


import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public LoginResponse login(LoginRequest r) {

        // 1. Validar usuario
        User user = repo.findByUsername(r.username())
                .orElseThrow(() -> new RuntimeException("Usuario no existe"));

        // 2. Validar contraseña
        if (!encoder.matches(r.password(), user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // 3. Obtener roles
        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet());

        // 4. Obtener permisos
        Set<String> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> permission.getName())
                .collect(Collectors.toSet());

        String mensaje = "Iniciaste sesión con el rol " + roles;

        return new LoginResponse(
                user.getUsername(),
                roles,
                permissions,
                mensaje
        );
    }
}


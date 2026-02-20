package com.equalatam.equlatam_backv2.service;


import com.equalatam.equlatam_backv2.dto.request.LoginRequest;
import com.equalatam.equlatam_backv2.dto.response.LoginResponse;
import com.equalatam.equlatam_backv2.entity.User;
import com.equalatam.equlatam_backv2.exception.InvalidCredentialsException;
import com.equalatam.equlatam_backv2.exception.ResourceNotFoundException;
import com.equalatam.equlatam_backv2.repository.UserRepository;
import com.equalatam.equlatam_backv2.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest r) {

        // 1. Buscar usuario
        User user = userRepository.findByUsername(r.username())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // 2. Validar contraseña
        if (!encoder.matches(r.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Credenciales incorrectas");
        }

        // 3. Generar JWT real
        String token = jwtService.generateToken(user);

        // 4. Mapear roles y permisos para la respuesta
        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet());

        Set<String> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(p -> p.getName())
                .collect(Collectors.toSet());

        return new LoginResponse(user.getUsername(), roles, permissions, token);
    }
}

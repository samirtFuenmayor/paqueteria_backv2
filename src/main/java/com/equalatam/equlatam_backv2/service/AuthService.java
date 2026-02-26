package com.equalatam.equlatam_backv2.service;

import com.equalatam.equlatam_backv2.dto.request.CambiarPasswordRequest;
import com.equalatam.equlatam_backv2.dto.request.LoginRequest;
import com.equalatam.equlatam_backv2.dto.request.ResetPasswordRequest;
import com.equalatam.equlatam_backv2.dto.response.LoginResponse;
import com.equalatam.equlatam_backv2.entity.User;
import com.equalatam.equlatam_backv2.exception.InvalidCredentialsException;
import com.equalatam.equlatam_backv2.exception.ResourceNotFoundException;
import com.equalatam.equlatam_backv2.repository.UserRepository;
import com.equalatam.equlatam_backv2.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    // ─── LOGIN ────────────────────────────────────────────────────────────────
    public LoginResponse login(LoginRequest r) {

        User user = userRepository.findByUsername(r.username())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!encoder.matches(r.password(), user.getPassword()))
            throw new InvalidCredentialsException("Credenciales incorrectas");

        String token = jwtService.generateToken(user);

        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet());

        Set<String> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(p -> p.getName())
                .collect(Collectors.toSet());

        return new LoginResponse(
                user.getUsername(),
                roles,
                permissions,
                token,
                user.isMustChangePassword()
        );
    }

    // ─── CAMBIAR CONTRASEÑA (cliente con mustChangePassword = true) ───────────
    // El cliente usa su token JWT y manda su password actual + nueva
    // Solo funciona si mustChangePassword = true (admin creó la cuenta)
    // Si se registró solo → no puede cambiar contraseña por aquí
    @Transactional
    public void cambiarPassword(String username, CambiarPasswordRequest req) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Solo puede cambiar si mustChangePassword = true
        if (!user.isMustChangePassword()) {
            throw new IllegalArgumentException(
                    "No tienes permiso para cambiar tu contraseña. " +
                            "Contacta al administrador si necesitas un reset.");
        }

        // Validar que la contraseña actual sea correcta
        if (!encoder.matches(req.passwordActual(), user.getPassword())) {
            throw new InvalidCredentialsException("La contraseña actual es incorrecta");
        }

        // No puede usar la misma contraseña
        if (encoder.matches(req.passwordNueva(), user.getPassword())) {
            throw new IllegalArgumentException(
                    "La nueva contraseña no puede ser igual a la actual");
        }

        user.setPassword(encoder.encode(req.passwordNueva()));
        user.setMustChangePassword(false);  // ya no necesita cambiarla
        userRepository.save(user);
    }

    // ─── RESET DE CONTRASEÑA POR ADMIN ───────────────────────────────────────
    // Admin resetea la contraseña de cualquier usuario
    // → mustChangePassword = true para que el usuario la cambie al siguiente login
    @Transactional
    public void resetPassword(UUID userId, ResetPasswordRequest req) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado: " + userId));

        user.setPassword(encoder.encode(req.passwordNueva()));
        user.setMustChangePassword(true);   // fuerza cambio en siguiente login
        userRepository.save(user);
    }
}
package com.equalatam.equlatam_backv2.controller;

import com.equalatam.equlatam_backv2.dto.request.CambiarPasswordRequest;
import com.equalatam.equlatam_backv2.dto.request.LoginRequest;
import com.equalatam.equlatam_backv2.dto.request.ResetPasswordRequest;
import com.equalatam.equlatam_backv2.dto.response.LoginResponse;
import com.equalatam.equlatam_backv2.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    // ─── LOGIN ────────────────────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest r) {
        return ResponseEntity.ok(service.login(r));
    }

    // ─── CAMBIAR CONTRASEÑA (cliente con mustChangePassword = true) ───────────
    // Requiere JWT — el cliente usa su propio token
    // PATCH /api/auth/cambiar-password
    // Body: { "passwordActual": "...", "passwordNueva": "..." }
    @PatchMapping("/cambiar-password")
    public ResponseEntity<Map<String, String>> cambiarPassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CambiarPasswordRequest req) {

        service.cambiarPassword(userDetails.getUsername(), req);
        return ResponseEntity.ok(Map.of(
                "message", "Contraseña actualizada correctamente"));
    }

    // ─── RESET DE CONTRASEÑA POR ADMIN ───────────────────────────────────────
    // Requiere JWT de ADMIN
    // PATCH /api/auth/reset-password/{userId}
    // Body: { "passwordNueva": "..." }
    @PatchMapping("/reset-password/{userId}")
    public ResponseEntity<Map<String, String>> resetPassword(
            @PathVariable UUID userId,
            @Valid @RequestBody ResetPasswordRequest req) {

        service.resetPassword(userId, req);
        return ResponseEntity.ok(Map.of(
                "message", "Contraseña reseteada. El usuario deberá cambiarla al iniciar sesión."));
    }
}
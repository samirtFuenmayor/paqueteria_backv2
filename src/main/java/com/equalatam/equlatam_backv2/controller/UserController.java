package com.equalatam.equlatam_backv2.controller;

import com.equalatam.equlatam_backv2.dto.request.UserCreateRequest;
import com.equalatam.equlatam_backv2.dto.request.UserUpdateRequest;
import com.equalatam.equlatam_backv2.dto.response.UserResponse;
import com.equalatam.equlatam_backv2.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserCreateRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(r));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponse> getByUsername(@PathVariable String username) {
        return ResponseEntity.ok(service.getByUsername(username));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PutMapping("/{userId}/roles")
    public ResponseEntity<UserResponse> assignRoles(@PathVariable UUID userId,
                                                    @RequestBody Set<UUID> roleIds) {
        return ResponseEntity.ok(service.assignRoles(userId, roleIds));
    }

    // ─── Asignar sucursal al usuario ──────────────────────────────────────────
    @PutMapping("/{userId}/sucursal")
    public ResponseEntity<UserResponse> assignSucursal(@PathVariable UUID userId,
                                                       @RequestBody Map<String, UUID> body) {
        return ResponseEntity.ok(service.assignSucursal(userId, body.get("sucursalId")));
    }
}
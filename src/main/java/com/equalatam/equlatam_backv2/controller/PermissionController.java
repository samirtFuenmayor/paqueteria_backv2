package com.equalatam.equlatam_backv2.controller;

import com.equalatam.equlatam_backv2.dto.request.PermissionCreateRequest;
import com.equalatam.equlatam_backv2.entity.Permission;
import com.equalatam.equlatam_backv2.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Permission> create(@Valid @RequestBody PermissionCreateRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(permissionService.create(r.name()));
    }

    @GetMapping
    public ResponseEntity<List<Permission>> list() {
        return ResponseEntity.ok(permissionService.findAll());
    }
}

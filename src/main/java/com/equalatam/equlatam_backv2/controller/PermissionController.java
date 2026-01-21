package com.equalatam.equlatam_backv2.controller;

import com.equalatam.equlatam_backv2.entity.Permission;
import com.equalatam.equlatam_backv2.repository.PermissionRepository;
import com.equalatam.equlatam_backv2.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(
                permissionService.create(body.get("name"))
        );
    }

    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(permissionService.findAll());
    }
}

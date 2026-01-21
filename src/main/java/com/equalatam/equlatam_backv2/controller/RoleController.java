package com.equalatam.equlatam_backv2.controller;

import com.equalatam.equlatam_backv2.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.equalatam.equlatam_backv2.service.RoleService;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(roleService.create(body.get("name")));
    }

    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(roleService.findAll());
    }

    @PutMapping("/{roleId}/permissions")
    public ResponseEntity<?> assignPermissions(
            @PathVariable UUID roleId,
            @RequestBody Set<UUID> permissionIds) {

        return ResponseEntity.ok(
                roleService.assignPermissions(roleId, permissionIds)
        );
    }

}

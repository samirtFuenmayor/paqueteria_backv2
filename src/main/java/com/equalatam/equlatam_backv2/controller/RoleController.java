package com.equalatam.equlatam_backv2.controller;

import com.equalatam.equlatam_backv2.dto.request.RoleCreateRequest;
import com.equalatam.equlatam_backv2.entity.Role;
import com.equalatam.equlatam_backv2.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
   // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Role> create(@Valid @RequestBody RoleCreateRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roleService.create(r.name()));
    }

    @GetMapping
    public ResponseEntity<List<Role>> list() {
        return ResponseEntity.ok(roleService.findAll());
    }

    @PutMapping("/{roleId}/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Role> assignPermissions(@PathVariable UUID roleId,
                                                  @RequestBody Set<UUID> permissionIds) {
        return ResponseEntity.ok(roleService.assignPermissions(roleId, permissionIds));
    }
}
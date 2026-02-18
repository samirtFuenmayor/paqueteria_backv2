package com.equalatam.equlatam_backv2.controller;

import com.equalatam.equlatam_backv2.dto.request.UserCreateRequest;
import com.equalatam.equlatam_backv2.entity.User;
import com.equalatam.equlatam_backv2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    public User create(@RequestBody UserCreateRequest r) {
        return service.create(r);
    }

    @GetMapping
    public List<User> getAll() {
        return service.getAll();
    }

    @GetMapping("/username/{username}")
    public User getByUsername(@PathVariable String username) {
        return service.getByUsername(username);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable UUID id,
                       @RequestBody UserCreateRequest r) {
        return service.update(id, r);
    }

    @PutMapping("/{userId}/roles")
    public ResponseEntity<?> assignRoles(
            @PathVariable UUID userId,
            @RequestBody Set<UUID> roleIds) {

        return ResponseEntity.ok(
                service.assignRoles(userId, roleIds)
        );
    }
}

package com.equalatam.equlatam_backv2.controller;

import com.equalatam.equlatam_backv2.dto.request.LoginRequest;
import com.equalatam.equlatam_backv2.entity.User;
import com.equalatam.equlatam_backv2.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/login")
    public User login(@RequestBody LoginRequest r) {
        return service.login(r);
    }
}

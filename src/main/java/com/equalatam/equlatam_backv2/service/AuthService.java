package com.equalatam.equlatam_backv2.service;

import com.equalatam.equlatam_backv2.dto.request.LoginRequest;
import com.equalatam.equlatam_backv2.entity.User;
import com.equalatam.equlatam_backv2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public User login(LoginRequest r) {
        User u = repo.findByUsername(r.username())
                .orElseThrow();

        if (!encoder.matches(r.password(), u.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }
        return u;
    }
}

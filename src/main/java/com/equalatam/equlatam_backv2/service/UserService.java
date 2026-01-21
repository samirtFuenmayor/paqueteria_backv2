package com.equalatam.equlatam_backv2.service;

import com.equalatam.equlatam_backv2.dto.request.UserCreateRequest;
import com.equalatam.equlatam_backv2.entity.Role;
import com.equalatam.equlatam_backv2.entity.User;
import com.equalatam.equlatam_backv2.repository.RoleRepository;
import com.equalatam.equlatam_backv2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;

    public User create(UserCreateRequest req) {
        User u = new User();
        u.setUsername(req.username());
        u.setPassword(encoder.encode(req.password()));
        return userRepository.save(u);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User update(UUID id, UserCreateRequest req) {
        User u = userRepository.findById(id).orElseThrow();
        u.setUsername(req.username());
        u.setPassword(encoder.encode(req.password()));
        return userRepository.save(u);
    }

    @Transactional
    public User assignRoles(UUID userId, Set<UUID> roleIds) {

        User user = userRepository.findById(userId)
                .orElseThrow();

        Set<Role> roles = new HashSet<>(
                roleRepository.findAllById(roleIds)
        );

        user.setRoles(roles);
        return user;
    }

}

package com.equalatam.equlatam_backv2.service;

import com.equalatam.equlatam_backv2.entity.Permission;
import com.equalatam.equlatam_backv2.entity.Role;
import com.equalatam.equlatam_backv2.repository.PermissionRepository;
import com.equalatam.equlatam_backv2.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public Role create(String name) {
        Role r = new Role();
        r.setName(name);
        return roleRepository.save(r);
    }

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Transactional
    public Role assignPermissions(UUID roleId, Set<UUID> permissionIds) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow();

        Set<Permission> permissions = new HashSet<>(
                permissionRepository.findAllById(permissionIds)
        );

        role.setPermissions(permissions);
        return role;
    }


}

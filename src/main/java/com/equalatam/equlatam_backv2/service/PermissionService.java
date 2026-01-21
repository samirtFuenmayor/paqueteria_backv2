package com.equalatam.equlatam_backv2.service;

import com.equalatam.equlatam_backv2.entity.Permission;
import com.equalatam.equlatam_backv2.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public Permission create(String name) {
        Permission p = new Permission();
        p.setName(name);
        return permissionRepository.save(p);
    }

    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }
}

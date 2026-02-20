package com.equalatam.equlatam_backv2.service;

import com.equalatam.equlatam_backv2.dto.request.UserCreateRequest;
import com.equalatam.equlatam_backv2.dto.response.UserResponse;
import com.equalatam.equlatam_backv2.entity.Role;
import com.equalatam.equlatam_backv2.entity.User;
import com.equalatam.equlatam_backv2.exception.ResourceNotFoundException;
import com.equalatam.equlatam_backv2.repository.RoleRepository;
import com.equalatam.equlatam_backv2.repository.UserRepository;
import com.equalatam.equlatam_backv2.sucursales.entity.Sucursal;
import com.equalatam.equlatam_backv2.sucursales.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;
    private final SucursalRepository sucursalRepository;  // ← NUEVO

    // ─── Crear ────────────────────────────────────────────────────────────────
    public UserResponse create(UserCreateRequest req) {
        User u = new User();
        mapRequestToUser(req, u);
        u.setPassword(encoder.encode(req.password()));
        return UserResponse.from(userRepository.save(u));
    }

    // ─── Listar todos ─────────────────────────────────────────────────────────
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    // ─── Buscar por ID ────────────────────────────────────────────────────────
    public UserResponse getById(UUID id) {
        return UserResponse.from(getUserOrThrow(id));
    }

    // ─── Buscar por username ──────────────────────────────────────────────────
    public UserResponse getByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
        return UserResponse.from(user);
    }

    // ─── Actualizar ───────────────────────────────────────────────────────────
    public UserResponse update(UUID id, UserCreateRequest req) {
        User u = getUserOrThrow(id);
        mapRequestToUser(req, u);
        if (req.password() != null && !req.password().isBlank()) {
            u.setPassword(encoder.encode(req.password()));
        }
        return UserResponse.from(userRepository.save(u));
    }

    // ─── Asignar roles ────────────────────────────────────────────────────────
    @Transactional
    public UserResponse assignRoles(UUID userId, Set<UUID> roleIds) {
        User user = getUserOrThrow(userId);
        Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
        user.setRoles(roles);
        return UserResponse.from(user);
    }

    // ─── Asignar sucursal ─────────────────────────────────────────────────────
    @Transactional
    public UserResponse assignSucursal(UUID userId, UUID sucursalId) {
        User user = getUserOrThrow(userId);
        Sucursal sucursal = sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada: " + sucursalId));
        user.setSucursal(sucursal);
        return UserResponse.from(userRepository.save(user));
    }

    // ─── Helper: mapea request → entidad ──────────────────────────────────────
    private void mapRequestToUser(UserCreateRequest req, User u) {
        u.setNombre(req.nombre());
        u.setApellido(req.apellido());
        u.setUsername(req.username());
        u.setCorreo(req.correo());
        u.setTelefono(req.telefono());
        u.setNacionalidad(req.nacionalidad());
        u.setProvincia(req.provincia());
        u.setCiudad(req.ciudad());
        u.setDireccion(req.direccion());
        u.setFechaNacimiento(req.fechaNacimiento());

        // ── Asignar sucursal si viene en el request ────────────────────────
        if (req.sucursalId() != null) {
            Sucursal sucursal = sucursalRepository.findById(req.sucursalId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Sucursal no encontrada: " + req.sucursalId()));
            u.setSucursal(sucursal);
        }
    }

    private User getUserOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));
    }
}
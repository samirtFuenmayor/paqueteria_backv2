package com.equalatam.equlatam_backv2.dto.response;
import com.equalatam.equlatam_backv2.entity.User;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record UserResponse(
        UUID id,
        String nombre,
        String apellido,
        String username,
        String correo,
        String telefono,
        String nacionalidad,
        String provincia,
        String ciudad,
        String direccion,
        LocalDate fechaNacimiento,
        UUID sucursalId,
        Set<String> roles
) {
    // Factory method: convierte entidad → DTO (nunca expone password)
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getNombre(),
                user.getApellido(),
                user.getUsername(),
                user.getCorreo(),
                user.getTelefono(),
                user.getNacionalidad(),
                user.getProvincia(),
                user.getCiudad(),
                user.getDireccion(),
                user.getFechaNacimiento(),
                user.getSucursal() != null ? user.getSucursal().getId() : null,
                user.getRoles().stream()
                        .map(r -> r.getName())
                        .collect(Collectors.toSet())
        );
    }
}

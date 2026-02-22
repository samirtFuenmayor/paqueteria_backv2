package com.equalatam.equlatam_backv2.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.UUID;

public record UserUpdateRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        String apellido,

        @NotBlank(message = "El username es obligatorio")
        @Size(min = 3, max = 50)
        String username,

        @NotBlank(message = "El correo es obligatorio")
        @Email
        String correo,

        String telefono,
        String nacionalidad,
        String provincia,
        String ciudad,
        String direccion,

        LocalDate fechaNacimiento,

        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String password,

        UUID sucursalId
) {}
package com.equalatam.equlatam_backv2.cliente.dto.request;

import com.equalatam.equlatam_backv2.cliente.entity.TipoIdentificacion;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

public record ClienteRequest(

        @NotNull(message = "El tipo de identificación es obligatorio")
        TipoIdentificacion tipoIdentificacion,

        @NotBlank(message = "El número de identificación es obligatorio")
        String numeroIdentificacion,

        @NotBlank(message = "Los nombres son obligatorios")
        String nombres,

        @NotBlank(message = "Los apellidos son obligatorios")
        String apellidos,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        String email,

        String telefono,

        LocalDate fechaNacimiento,

        @NotBlank(message = "El país es obligatorio")
        String pais,

        String provincia,
        String ciudad,
        String direccion,

        // Sucursal exterior donde recibirá sus paquetes (MIA, YYZ, etc.)
        UUID sucursalId,

        String observaciones
) {}

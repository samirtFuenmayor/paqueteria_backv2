package com.equalatam.equlatam_backv2.sucursales.dto.request;

import com.equalatam.equlatam_backv2.sucursales.entity.TipoSucursal;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SucursalRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El código es obligatorio")
        @Size(max = 20, message = "El código no puede tener más de 20 caracteres")
        String codigo,

        @NotNull(message = "El tipo de sucursal es obligatorio")
        TipoSucursal tipo,

        @NotBlank(message = "El país es obligatorio")
        String pais,

        @NotBlank(message = "La ciudad es obligatoria")
        String ciudad,

        @NotBlank(message = "La dirección es obligatoria")
        String direccion,

        String telefono,

        @Email(message = "El email no tiene un formato válido")
        String email,

        String responsable,

        @NotBlank(message = "El prefijo de casillero es obligatorio")
        @Size(max = 10, message = "El prefijo no puede tener más de 10 caracteres")
        String prefijoCasillero
) {}


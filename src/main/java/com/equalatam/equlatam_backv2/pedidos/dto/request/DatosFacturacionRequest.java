package com.equalatam.equlatam_backv2.pedidos.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DatosFacturacionRequest(
        Boolean usarDatosCliente,   // true = copiar del cliente automáticamente
        String razonSocial,
        @NotBlank String rucCedula,
        String direccionFacturacion,
        String emailFacturacion,
        String telefonoFacturacion
) {}
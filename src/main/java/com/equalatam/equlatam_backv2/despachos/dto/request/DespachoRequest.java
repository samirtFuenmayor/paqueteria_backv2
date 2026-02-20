package com.equalatam.equlatam_backv2.despachos.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record DespachoRequest(

        @NotNull(message = "La sucursal origen es obligatoria")
        UUID sucursalOrigenId,

        @NotNull(message = "La sucursal destino es obligatoria")
        UUID sucursalDestinoId,

        // Información del transporte
        String aerolinea,
        String numeroVuelo,
        String guiaAerea,
        String numeroContenedor,
        String tipoTransporte,  // AEREO, MARITIMO, TERRESTRE

        LocalDateTime fechaSalidaProgramada,
        LocalDateTime fechaLlegadaProgramada,

        String observaciones
) {}

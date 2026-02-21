package com.equalatam.equlatam_backv2.guias.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record GuiaRequest(

        @NotNull(message = "El pedido es obligatorio")
        UUID pedidoId,

        // Remitente (quien envía desde el exterior)
        @NotBlank(message = "El nombre del remitente es obligatorio")
        String remitenteNombre,
        String remitenteDireccion,
        String remitenteTelefono,
        String remitenteEmail,
        String remitentePais,

        // Contenido
        @NotBlank(message = "La descripción del contenido es obligatoria")
        String descripcionContenido,

        @Positive(message = "El peso debe ser mayor a 0")
        Double pesoDeclarado,

        Double valorDeclarado,
        Integer cantidadPiezas,
        Double largo,
        Double ancho,
        Double alto,

        // Despacho (opcional, se asigna después)
        String numeroDespacho,
        String aerolinea,
        String numeroVuelo,
        String guiaAerea,

        String observaciones
) {}

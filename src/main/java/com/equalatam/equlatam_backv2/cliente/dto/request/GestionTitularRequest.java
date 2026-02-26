package com.equalatam.equlatam_backv2.cliente.dto.request;

import com.equalatam.equlatam_backv2.cliente.entity.Parentesco;
import java.util.UUID;

public record GestionTitularRequest(
        UUID       titularId,   // null = desvincular, UUID = vincular al titular
        Parentesco parentesco

        // requerido solo si titularId != null
) {

}


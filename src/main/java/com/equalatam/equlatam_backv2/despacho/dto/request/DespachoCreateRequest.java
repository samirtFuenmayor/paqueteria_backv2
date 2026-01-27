package com.equalatam.equlatam_backv2.despacho.dto.request;

import java.util.UUID;

public record DespachoCreateRequest(
        UUID sucursalId,
        UUID rutaId
) {}
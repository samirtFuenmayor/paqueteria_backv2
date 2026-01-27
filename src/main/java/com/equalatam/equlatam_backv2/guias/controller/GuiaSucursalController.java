package com.equalatam.equlatam_backv2.guias.controller;

import com.equalatam.equlatam_backv2.guias.service.GuiaSucursalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/guias-sucursales")
@RequiredArgsConstructor
public class GuiaSucursalController {

    private final GuiaSucursalService service;

    @PostMapping("/ingresar")
    public void ingresar(
            @RequestParam UUID guiaId,
            @RequestParam UUID sucursalId
    ) {
        service.ingresarAGuiaASucursal(guiaId, sucursalId);
    }
}

package com.equalatam.equlatam_backv2.financiero.controller;

import com.equalatam.equlatam_backv2.entity.User;
import com.equalatam.equlatam_backv2.financiero.dto.NotaAjusteRequest;
import com.equalatam.equlatam_backv2.financiero.entity.NotaAjuste;
import com.equalatam.equlatam_backv2.financiero.service.NotaAjusteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/financiero/notas")
@RequiredArgsConstructor
public class NotaAjusteController {

    private final NotaAjusteService service;

    // GET /api/financiero/notas/factura/{facturaId}
    @GetMapping("/factura/{facturaId}")
    public ResponseEntity<List<NotaAjuste>> porFactura(@PathVariable UUID facturaId) {
        return ResponseEntity.ok(service.listarPorFactura(facturaId));
    }

    // GET /api/financiero/notas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<NotaAjuste> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    // POST /api/financiero/notas
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<NotaAjuste> emitir(@RequestBody NotaAjusteRequest req,
                                             @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.emitir(req, user));
    }

    // POST /api/financiero/notas/{id}/anular
    @PostMapping("/{id}/anular")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotaAjuste> anular(@PathVariable UUID id) {
        return ResponseEntity.ok(service.anular(id));
    }
}
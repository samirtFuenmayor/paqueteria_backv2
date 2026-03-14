// ─── PagoController.java ──────────────────────────────────────────────────────
package com.equalatam.equlatam_backv2.financiero.controller;

import com.equalatam.equlatam_backv2.entity.User;
import com.equalatam.equlatam_backv2.financiero.dto.PagoRequest;
import com.equalatam.equlatam_backv2.financiero.entity.Pago;
import com.equalatam.equlatam_backv2.financiero.service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/financiero/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService service;

    // GET /api/financiero/pagos/pendientes
    @GetMapping("/pendientes")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<List<Pago>> pendientes() {
        return ResponseEntity.ok(service.listarPendientes());
    }

    // GET /api/financiero/pagos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Pago> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    // GET /api/financiero/pagos/factura/{facturaId}
    @GetMapping("/factura/{facturaId}")
    public ResponseEntity<List<Pago>> porFactura(@PathVariable UUID facturaId) {
        return ResponseEntity.ok(service.listarPorFactura(facturaId));
    }

    // GET /api/financiero/pagos/cliente/{clienteId}
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Pago>> porCliente(@PathVariable UUID clienteId) {
        return ResponseEntity.ok(service.listarPorCliente(clienteId));
    }

    // POST /api/financiero/pagos
    @PostMapping
    public ResponseEntity<Pago> registrar(@RequestBody PagoRequest req,
                                          @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.registrar(req, user));
    }

    // POST /api/financiero/pagos/{id}/confirmar
    @PostMapping("/{id}/confirmar")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<Pago> confirmar(@PathVariable UUID id,
                                          @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.confirmar(id, user));
    }

    // POST /api/financiero/pagos/{id}/rechazar
    @PostMapping("/{id}/rechazar")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<Pago> rechazar(@PathVariable UUID id,
                                         @RequestBody Map<String, String> body,
                                         @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.rechazar(id, body.get("motivo"), user));
    }
}
// ─── FacturaController.java ───────────────────────────────────────────────────
package com.equalatam.equlatam_backv2.financiero.controller;

import com.equalatam.equlatam_backv2.entity.User;
import com.equalatam.equlatam_backv2.financiero.dto.FacturaRequest;
import com.equalatam.equlatam_backv2.financiero.entity.Factura;
import com.equalatam.equlatam_backv2.financiero.service.FacturaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/financiero/facturas")
@RequiredArgsConstructor
public class FacturaController {

    private final FacturaService service;

    // GET /api/financiero/facturas/pendientes
    @GetMapping("/pendientes")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<List<Factura>> pendientes() {
        return ResponseEntity.ok(service.listarPendientes());
    }

    // GET /api/financiero/facturas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Factura> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    // GET /api/financiero/facturas/cliente/{clienteId}
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Factura>> porCliente(@PathVariable UUID clienteId) {
        return ResponseEntity.ok(service.listarPorCliente(clienteId));
    }

    // GET /api/financiero/facturas/pedido/{pedidoId}
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<Factura>> porPedido(@PathVariable UUID pedidoId) {
        return ResponseEntity.ok(service.listarPorPedido(pedidoId));
    }

    // GET /api/financiero/facturas/rango?desde=2026-01-01&hasta=2026-03-31
    @GetMapping("/rango")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<List<Factura>> porRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(service.listarPorRango(desde, hasta));
    }

    // POST /api/financiero/facturas
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<Factura> crear(@RequestBody FacturaRequest req,
                                         @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.crear(req, user));
    }

    // POST /api/financiero/facturas/{id}/emitir
    @PostMapping("/{id}/emitir")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<Factura> emitir(@PathVariable UUID id,
                                          @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.emitir(id, user));
    }

    // POST /api/financiero/facturas/{id}/anular
    @PostMapping("/{id}/anular")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Factura> anular(@PathVariable UUID id,
                                          @RequestBody Map<String, String> body,
                                          @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.anular(id, body.get("motivo"), user));
    }

    // GET /api/financiero/facturas/cliente/{clienteId}/deuda
    @GetMapping("/cliente/{clienteId}/deuda")
    public ResponseEntity<Map<String, Double>> deudaCliente(@PathVariable UUID clienteId) {
        return ResponseEntity.ok(Map.of("deuda", service.getDeudaCliente(clienteId)));
    }
}
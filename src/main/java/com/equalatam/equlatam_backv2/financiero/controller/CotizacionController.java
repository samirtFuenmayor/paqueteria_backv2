package com.equalatam.equlatam_backv2.financiero.controller;

import com.equalatam.equlatam_backv2.entity.User;
import com.equalatam.equlatam_backv2.financiero.dto.CotizacionRequest;
import com.equalatam.equlatam_backv2.financiero.entity.Cotizacion;
import com.equalatam.equlatam_backv2.financiero.service.CotizacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/financiero/cotizaciones")
@RequiredArgsConstructor
public class CotizacionController {

    private final CotizacionService service;

    // GET /api/financiero/cotizaciones
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<List<Cotizacion>> listarTodas() {
        return ResponseEntity.ok(service.listarPendientes());
    }

    // GET /api/financiero/cotizaciones/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Cotizacion> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    // GET /api/financiero/cotizaciones/cliente/{clienteId}
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Cotizacion>> porCliente(@PathVariable UUID clienteId) {
        return ResponseEntity.ok(service.listarPorCliente(clienteId));
    }

    // GET /api/financiero/cotizaciones/pedido/{pedidoId}
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<Cotizacion>> porPedido(@PathVariable UUID pedidoId) {
        return ResponseEntity.ok(service.listarPorPedido(pedidoId));
    }

    // POST /api/financiero/cotizaciones
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<Cotizacion> crear(@RequestBody CotizacionRequest req,
                                            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.crear(req, user));
    }

    // POST /api/financiero/cotizaciones/{id}/aprobar
    @PostMapping("/{id}/aprobar")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<Cotizacion> aprobar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.aprobar(id));
    }

    // POST /api/financiero/cotizaciones/{id}/cancelar
    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<Cotizacion> cancelar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.cancelar(id));
    }
}
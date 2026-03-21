package com.equalatam.equlatam_backv2.financiero.controller;

import com.equalatam.equlatam_backv2.entity.User;
import com.equalatam.equlatam_backv2.financiero.dto.AprobarCotizacionRequest;
import com.equalatam.equlatam_backv2.financiero.dto.CotizacionRequest;
import com.equalatam.equlatam_backv2.financiero.dto.CotizacionResponse;
import com.equalatam.equlatam_backv2.financiero.service.CotizacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/financiero/cotizaciones")
@RequiredArgsConstructor
public class CotizacionController {

    private final CotizacionService service;

    @GetMapping
    public ResponseEntity<List<CotizacionResponse>> listarTodas() {
        return ResponseEntity.ok(service.listarPendientes().stream()
                .map(service::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CotizacionResponse> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(service.toResponse(service.obtener(id)));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<CotizacionResponse>> porCliente(@PathVariable UUID clienteId) {
        return ResponseEntity.ok(service.listarPorCliente(clienteId).stream()
                .map(service::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<CotizacionResponse>> porPedido(@PathVariable UUID pedidoId) {
        return ResponseEntity.ok(service.listarPorPedido(pedidoId).stream()
                .map(service::toResponse).collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<CotizacionResponse> crear(@RequestBody CotizacionRequest req,
                                                    @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.toResponse(service.crear(req, user)));
    }

    @PostMapping("/{id}/aprobar")
    public ResponseEntity<CotizacionResponse> aprobar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.toResponse(service.aprobar(id)));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<CotizacionResponse> cancelar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.toResponse(service.cancelar(id)));
    }

    @PostMapping("/{id}/aprobar-cliente")
    public ResponseEntity<CotizacionResponse> aprobarPorCliente(
            @PathVariable UUID id,
            @RequestBody AprobarCotizacionRequest req) {
        return ResponseEntity.ok(service.toResponse(service.aprobarPorCliente(id, req)));
    }
}
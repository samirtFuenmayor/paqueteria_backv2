package com.equalatam.equlatam_backv2.financiero.controller;

import com.equalatam.equlatam_backv2.financiero.dto.FacturaRequest;
import com.equalatam.equlatam_backv2.financiero.dto.FacturaResponse;
import com.equalatam.equlatam_backv2.financiero.service.FacturaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/financiero/facturas")
@RequiredArgsConstructor
public class FacturaController {

    private final FacturaService service;

    @GetMapping("/pendientes")
    public ResponseEntity<List<FacturaResponse>> pendientes() {
        return ResponseEntity.ok(service.listarPendientes().stream()
                .map(service::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacturaResponse> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(service.toResponse(service.obtener(id)));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<FacturaResponse>> porCliente(@PathVariable UUID clienteId) {
        return ResponseEntity.ok(service.listarPorCliente(clienteId).stream()
                .map(service::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<FacturaResponse>> porPedido(@PathVariable UUID pedidoId) {
        return ResponseEntity.ok(service.listarPorPedido(pedidoId).stream()
                .map(service::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/rango")
    public ResponseEntity<List<FacturaResponse>> porRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(service.listarPorRango(desde, hasta).stream()
                .map(service::toResponse).collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<FacturaResponse> crear(@RequestBody FacturaRequest req) {
        return ResponseEntity.ok(service.toResponse(service.crear(req, null)));
    }

    @PostMapping("/{id}/emitir")
    public ResponseEntity<FacturaResponse> emitir(@PathVariable UUID id) {
        return ResponseEntity.ok(service.toResponse(service.emitir(id, null)));
    }

    @PostMapping("/{id}/anular")
    public ResponseEntity<FacturaResponse> anular(@PathVariable UUID id,
                                                  @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(service.toResponse(
                service.anular(id, body.get("motivo"), null)));
    }

    @GetMapping("/cliente/{clienteId}/deuda")
    public ResponseEntity<Map<String, Double>> deudaCliente(@PathVariable UUID clienteId) {
        return ResponseEntity.ok(Map.of("deuda", service.getDeudaCliente(clienteId)));
    }
}
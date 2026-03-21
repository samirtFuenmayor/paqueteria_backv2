package com.equalatam.equlatam_backv2.financiero.controller;

import com.equalatam.equlatam_backv2.entity.User;
import com.equalatam.equlatam_backv2.financiero.dto.PagoRequest;
import com.equalatam.equlatam_backv2.financiero.dto.PagoResponse;
import com.equalatam.equlatam_backv2.financiero.service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/financiero/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService service;

    @GetMapping("/pendientes")
    public ResponseEntity<List<PagoResponse>> pendientes() {
        return ResponseEntity.ok(service.listarPendientes().stream()
                .map(service::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoResponse> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(service.toResponse(service.obtener(id)));
    }

    @GetMapping("/factura/{facturaId}")
    public ResponseEntity<List<PagoResponse>> porFactura(@PathVariable UUID facturaId) {
        return ResponseEntity.ok(service.listarPorFactura(facturaId).stream()
                .map(service::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PagoResponse>> porCliente(@PathVariable UUID clienteId) {
        return ResponseEntity.ok(service.listarPorCliente(clienteId).stream()
                .map(service::toResponse).collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<PagoResponse> registrar(@RequestBody PagoRequest req,
                                                  @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.toResponse(service.registrar(req, user)));
    }

    @PostMapping("/{id}/confirmar")
    public ResponseEntity<PagoResponse> confirmar(@PathVariable UUID id,
                                                  @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.toResponse(service.confirmar(id, user)));
    }

    @PostMapping("/{id}/rechazar")
    public ResponseEntity<PagoResponse> rechazar(@PathVariable UUID id,
                                                 @RequestBody Map<String, String> body,
                                                 @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.toResponse(
                service.rechazar(id, body.get("motivo"), user)));
    }
}
package com.equalatam.equlatam_backv2.notificaciones.controller;


import com.equalatam.equlatam_backv2.notificaciones.dto.response.NotificacionesResponse;
import com.equalatam.equlatam_backv2.notificaciones.service.NotificacionesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionesController {

    private final NotificacionesService notificacionService;

    // ─── Historial de un cliente ──────────────────────────────────────────────
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<NotificacionesResponse>> findByCliente(
            @PathVariable UUID clienteId) {
        return ResponseEntity.ok(notificacionService.findByCliente(clienteId));
    }

    // ─── Historial de un pedido ───────────────────────────────────────────────
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<NotificacionesResponse>> findByPedido(
            @PathVariable UUID pedidoId) {
        return ResponseEntity.ok(notificacionService.findByPedido(pedidoId));
    }

    // ─── Reenviar notificación fallida ────────────────────────────────────────
    @PostMapping("/{id}/reenviar")
    public ResponseEntity<NotificacionesResponse> reenviar(@PathVariable UUID id) {
        return ResponseEntity.ok(notificacionService.reenviar(id));
    }
}

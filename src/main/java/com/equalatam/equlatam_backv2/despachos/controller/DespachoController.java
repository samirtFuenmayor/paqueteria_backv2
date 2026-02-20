package com.equalatam.equlatam_backv2.despachos.controller;

import com.equalatam.equlatam_backv2.despachos.dto.request.DespachoRequest;
import com.equalatam.equlatam_backv2.despachos.dto.response.DespachoResponse;
import com.equalatam.equlatam_backv2.despachos.entity.EstadoDespacho;
import com.equalatam.equlatam_backv2.despachos.service.DespachoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/despachos")
@RequiredArgsConstructor
public class DespachoController {

    private final DespachoService despachoService;

    // ─── Crear despacho ───────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<DespachoResponse> create(
            @Valid @RequestBody DespachoRequest req,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(despachoService.create(req, username));
    }

    // ─── Listar todos ─────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<DespachoResponse>> findAll() {
        return ResponseEntity.ok(despachoService.findAll());
    }

    // ─── Buscar por ID ────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<DespachoResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(despachoService.findById(id));
    }

    // ─── Buscar por número ────────────────────────────────────────────────────
    @GetMapping("/numero/{numero}")
    public ResponseEntity<DespachoResponse> findByNumero(@PathVariable String numero) {
        return ResponseEntity.ok(despachoService.findByNumero(numero));
    }

    // ─── Por estado ───────────────────────────────────────────────────────────
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<DespachoResponse>> findByEstado(@PathVariable EstadoDespacho estado) {
        return ResponseEntity.ok(despachoService.findByEstado(estado));
    }

    // ─── Despachos abiertos en una sucursal (para agregar pedidos) ────────────
    @GetMapping("/abiertos/sucursal/{sucursalId}")
    public ResponseEntity<List<DespachoResponse>> findAbiertos(@PathVariable UUID sucursalId) {
        return ResponseEntity.ok(despachoService.findAbiertosEnSucursal(sucursalId));
    }

    // ─── Despachos en tránsito hacia una sucursal ─────────────────────────────
    @GetMapping("/en-transito/hacia/{sucursalId}")
    public ResponseEntity<List<DespachoResponse>> findEnTransito(@PathVariable UUID sucursalId) {
        return ResponseEntity.ok(despachoService.findEnTransitoHacia(sucursalId));
    }

    // ─── Agregar pedidos al despacho ──────────────────────────────────────────
    @PostMapping("/{id}/pedidos")
    public ResponseEntity<DespachoResponse> agregarPedidos(
            @PathVariable UUID id,
            @RequestBody Set<UUID> pedidoIds) {
        return ResponseEntity.ok(despachoService.agregarPedidos(id, pedidoIds));
    }

    // ─── Quitar pedido del despacho ───────────────────────────────────────────
    @DeleteMapping("/{id}/pedidos/{pedidoId}")
    public ResponseEntity<DespachoResponse> quitarPedido(
            @PathVariable UUID id,
            @PathVariable UUID pedidoId) {
        return ResponseEntity.ok(despachoService.quitarPedido(id, pedidoId));
    }

    // ─── Cambiar estado ───────────────────────────────────────────────────────
    @PatchMapping("/{id}/estado")
    public ResponseEntity<DespachoResponse> cambiarEstado(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        EstadoDespacho estado = EstadoDespacho.valueOf(body.get("estado"));
        String observacion = body.get("observacion");
        return ResponseEntity.ok(despachoService.cambiarEstado(id, estado, observacion));
    }

    // ─── Actualizar info de transporte ────────────────────────────────────────
    @PutMapping("/{id}/transporte")
    public ResponseEntity<DespachoResponse> actualizarTransporte(
            @PathVariable UUID id,
            @RequestBody DespachoRequest req) {
        return ResponseEntity.ok(despachoService.actualizarTransporte(id, req));
    }
}
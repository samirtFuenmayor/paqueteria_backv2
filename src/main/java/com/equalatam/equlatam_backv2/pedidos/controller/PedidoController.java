package com.equalatam.equlatam_backv2.pedidos.controller;

import com.equalatam.equlatam_backv2.pedidos.dto.request.PedidoRequest;
import com.equalatam.equlatam_backv2.pedidos.dto.response.PedidoResponse;
import com.equalatam.equlatam_backv2.pedidos.entity.EstadoPedido;
import com.equalatam.equlatam_backv2.pedidos.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoResponse> create(
            @Valid @RequestBody PedidoRequest req,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidoService.create(req, username));
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponse>> findAll() {
        return ResponseEntity.ok(pedidoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(pedidoService.findById(id));
    }

    @GetMapping("/numero/{numeroPedido}")
    public ResponseEntity<PedidoResponse> findByNumero(@PathVariable String numeroPedido) {
        return ResponseEntity.ok(pedidoService.findByNumeroPedido(numeroPedido));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoResponse>> findByCliente(@PathVariable UUID clienteId) {
        return ResponseEntity.ok(pedidoService.findByCliente(clienteId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PedidoResponse>> findByEstado(@PathVariable EstadoPedido estado) {
        return ResponseEntity.ok(pedidoService.findByEstado(estado));
    }

    @GetMapping("/sucursal-origen/{sucursalId}")
    public ResponseEntity<List<PedidoResponse>> findBySucursalOrigen(@PathVariable UUID sucursalId) {
        return ResponseEntity.ok(pedidoService.findBySucursalOrigen(sucursalId));
    }

    @GetMapping("/sucursal-destino/{sucursalId}")
    public ResponseEntity<List<PedidoResponse>> findBySucursalDestino(@PathVariable UUID sucursalId) {
        return ResponseEntity.ok(pedidoService.findBySucursalDestino(sucursalId));
    }

    @GetMapping("/listos-para-despachar/{sucursalOrigenId}")
    public ResponseEntity<List<PedidoResponse>> findListosParaDespachar(
            @PathVariable UUID sucursalOrigenId) {
        return ResponseEntity.ok(pedidoService.findListosParaDespachar(sucursalOrigenId));
    }

    @GetMapping("/disponibles/{sucursalDestinoId}")
    public ResponseEntity<List<PedidoResponse>> findDisponibles(
            @PathVariable UUID sucursalDestinoId) {
        return ResponseEntity.ok(pedidoService.findDisponiblesEnSucursal(sucursalDestinoId));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<PedidoResponse>> buscar(@RequestParam String q) {
        return ResponseEntity.ok(pedidoService.buscar(q));
    }

    @GetMapping("/dashboard/conteos")
    public ResponseEntity<Map<String, Long>> conteosPorEstado() {
        return ResponseEntity.ok(pedidoService.conteosPorEstado());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponse> update(@PathVariable UUID id,
                                                 @Valid @RequestBody PedidoRequest req) {
        return ResponseEntity.ok(pedidoService.update(id, req));
    }

    // ─── Cambiar estado + genera tracking automático ──────────────────────────
    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoResponse> cambiarEstado(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {

        EstadoPedido estado = EstadoPedido.valueOf(body.get("estado"));
        String observacion = body.get("observacion");
        UUID sucursalId = body.get("sucursalId") != null ?
                UUID.fromString(body.get("sucursalId")) : null;
        String username = userDetails != null ? userDetails.getUsername() : null;

        return ResponseEntity.ok(
                pedidoService.cambiarEstado(id, estado, observacion, username, sucursalId));
    }
}
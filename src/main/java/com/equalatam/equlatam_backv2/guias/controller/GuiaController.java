package com.equalatam.equlatam_backv2.guias.controller;


import com.equalatam.equlatam_backv2.guias.dto.request.GuiaRequest;
import com.equalatam.equlatam_backv2.guias.dto.response.GuiaResponse;
import com.equalatam.equlatam_backv2.guias.entity.EstadoGuia;
import com.equalatam.equlatam_backv2.guias.service.GuiaService;
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
@RequestMapping("/api/guias")
@RequiredArgsConstructor
public class GuiaController {

    private final GuiaService guiaService;

    // ─── Generar guía desde pedido ────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<GuiaResponse> generar(
            @Valid @RequestBody GuiaRequest req,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(guiaService.generar(req, username));
    }

    // ─── Listar todas ─────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<GuiaResponse>> findAll() {
        return ResponseEntity.ok(guiaService.findAll());
    }

    // ─── Por ID ───────────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<GuiaResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(guiaService.findById(id));
    }

    // ─── Por número de guía ───────────────────────────────────────────────────
    @GetMapping("/numero/{numero}")
    public ResponseEntity<GuiaResponse> findByNumero(@PathVariable String numero) {
        return ResponseEntity.ok(guiaService.findByNumero(numero));
    }

    // ─── Por pedido ───────────────────────────────────────────────────────────
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<GuiaResponse> findByPedido(@PathVariable UUID pedidoId) {
        return ResponseEntity.ok(guiaService.findByPedido(pedidoId));
    }

    // ─── Por estado ───────────────────────────────────────────────────────────
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<GuiaResponse>> findByEstado(@PathVariable EstadoGuia estado) {
        return ResponseEntity.ok(guiaService.findByEstado(estado));
    }

    // ─── Por cliente ──────────────────────────────────────────────────────────
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<GuiaResponse>> findByCliente(@PathVariable UUID clienteId) {
        return ResponseEntity.ok(guiaService.findByCliente(clienteId));
    }

    // ─── Por despacho ─────────────────────────────────────────────────────────
    @GetMapping("/despacho/{numeroDespacho}")
    public ResponseEntity<List<GuiaResponse>> findByDespacho(@PathVariable String numeroDespacho) {
        return ResponseEntity.ok(guiaService.findByDespacho(numeroDespacho));
    }

    // ─── Asignar a despacho ───────────────────────────────────────────────────
    @PatchMapping("/{id}/despacho")
    public ResponseEntity<GuiaResponse> asignarDespacho(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(guiaService.asignarDespacho(
                id,
                body.get("numeroDespacho"),
                body.get("aerolinea"),
                body.get("numeroVuelo"),
                body.get("guiaAerea")
        ));
    }

    // ─── Cambiar estado ───────────────────────────────────────────────────────
    @PatchMapping("/{id}/estado")
    public ResponseEntity<GuiaResponse> cambiarEstado(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        EstadoGuia estado = EstadoGuia.valueOf(body.get("estado"));
        return ResponseEntity.ok(guiaService.cambiarEstado(id, estado));
    }

    // ─── Anular guía ──────────────────────────────────────────────────────────
    @PatchMapping("/{id}/anular")
    public ResponseEntity<GuiaResponse> anular(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(guiaService.anular(id, body.get("motivo")));
    }
}

package com.equalatam.equlatam_backv2.cliente.controller;

import com.equalatam.equlatam_backv2.cliente.dto.request.ClienteRegistroRequest;
import com.equalatam.equlatam_backv2.cliente.dto.request.ClienteRequest;
import com.equalatam.equlatam_backv2.cliente.dto.request.GestionTitularRequest;
import com.equalatam.equlatam_backv2.cliente.dto.response.ClienteResponse;
import com.equalatam.equlatam_backv2.cliente.entity.EstadoCliente;
import com.equalatam.equlatam_backv2.cliente.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    // ─── PÚBLICO — auto-registro sin JWT ─────────────────────────────────────
    @PostMapping("/api/auth/registro-cliente")
    public ResponseEntity<ClienteResponse> registrar(
            @Valid @RequestBody ClienteRegistroRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clienteService.registrar(req));
    }

    // ─── ADMIN ────────────────────────────────────────────────────────────────
    @PostMapping("/api/clientes")
    public ResponseEntity<ClienteResponse> create(
            @Valid @RequestBody ClienteRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clienteService.create(req));
    }

    @GetMapping("/api/clientes")
    public ResponseEntity<List<ClienteResponse>> findAll() {
        return ResponseEntity.ok(clienteService.findAll());
    }

    @GetMapping("/api/clientes/todos")
    public ResponseEntity<List<ClienteResponse>> findAllIncluyendoInactivos() {
        return ResponseEntity.ok(clienteService.findAllIncluyendoInactivos());
    }

    @GetMapping("/api/clientes/{id}")
    public ResponseEntity<ClienteResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(clienteService.findById(id));
    }

    @GetMapping("/api/clientes/identificacion/{numero}")
    public ResponseEntity<ClienteResponse> findByIdentificacion(@PathVariable String numero) {
        return ResponseEntity.ok(clienteService.findByIdentificacion(numero));
    }

    @GetMapping("/api/clientes/casillero/{casillero}")
    public ResponseEntity<ClienteResponse> findByCasillero(@PathVariable String casillero) {
        return ResponseEntity.ok(clienteService.findByCasillero(casillero));
    }

    @GetMapping("/api/clientes/sucursal/{sucursalId}")
    public ResponseEntity<List<ClienteResponse>> findBySucursal(@PathVariable UUID sucursalId) {
        return ResponseEntity.ok(clienteService.findBySucursal(sucursalId));
    }

    @GetMapping("/api/clientes/buscar")
    public ResponseEntity<List<ClienteResponse>> buscar(@RequestParam String q) {
        return ResponseEntity.ok(clienteService.buscar(q));
    }

    // ─── Afiliados de un titular ──────────────────────────────────────────────
    @GetMapping("/api/clientes/{id}/afiliados")
    public ResponseEntity<List<ClienteResponse>> findAfiliados(@PathVariable UUID id) {
        return ResponseEntity.ok(clienteService.findAfiliados(id));
    }

    @PutMapping("/api/clientes/{id}")
    public ResponseEntity<ClienteResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody ClienteRequest req) {
        return ResponseEntity.ok(clienteService.update(id, req));
    }

    @PatchMapping("/api/clientes/{id}/estado")
    public ResponseEntity<ClienteResponse> cambiarEstado(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        EstadoCliente estado = EstadoCliente.valueOf(body.get("estado"));
        return ResponseEntity.ok(clienteService.cambiarEstado(id, estado));
    }

    @PatchMapping("/api/clientes/{id}/sucursal")
    public ResponseEntity<ClienteResponse> asignarSucursal(
            @PathVariable UUID id,
            @RequestBody Map<String, UUID> body) {
        return ResponseEntity.ok(
                clienteService.asignarSucursal(id, body.get("sucursalId")));
    }

    // ─── Vincular / desvincular titular ──────────────────────────────────────
    /**
     * Vincular:    { "titularId": "uuid", "parentesco": "HIJO" }
     * Desvincular: { "titularId": null }
     */
    @PatchMapping("/api/clientes/{id}/titular")
    public ResponseEntity<ClienteResponse> gestionarTitular(
            @PathVariable UUID id,
            @RequestBody GestionTitularRequest body) {
        return ResponseEntity.ok(
                clienteService.gestionarTitular(id, body.titularId(), body.parentesco()));
    }
}
package com.equalatam.equlatam_backv2.cliente.controller;

import com.equalatam.equlatam_backv2.cliente.dto.request.ClienteRequest;
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
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    // ─── Crear ────────────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<ClienteResponse> create(@Valid @RequestBody ClienteRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.create(req));
    }

    // ─── Listar activos ───────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<ClienteResponse>> findAll() {
        return ResponseEntity.ok(clienteService.findAll());
    }

    // ─── Listar todos incluyendo inactivos ────────────────────────────────────
    @GetMapping("/todos")
    public ResponseEntity<List<ClienteResponse>> findAllIncluyendoInactivos() {
        return ResponseEntity.ok(clienteService.findAllIncluyendoInactivos());
    }

    // ─── Buscar por ID ────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(clienteService.findById(id));
    }

    // ─── Buscar por número de identificación (cédula/ruc/pasaporte) ──────────
    @GetMapping("/identificacion/{numero}")
    public ResponseEntity<ClienteResponse> findByIdentificacion(@PathVariable String numero) {
        return ResponseEntity.ok(clienteService.findByIdentificacion(numero));
    }

    // ─── Buscar por casillero ─────────────────────────────────────────────────
    @GetMapping("/casillero/{casillero}")
    public ResponseEntity<ClienteResponse> findByCasillero(@PathVariable String casillero) {
        return ResponseEntity.ok(clienteService.findByCasillero(casillero));
    }

    // ─── Clientes por sucursal ────────────────────────────────────────────────
    @GetMapping("/sucursal/{sucursalId}")
    public ResponseEntity<List<ClienteResponse>> findBySucursal(@PathVariable UUID sucursalId) {
        return ResponseEntity.ok(clienteService.findBySucursal(sucursalId));
    }

    // ─── Buscador general ─────────────────────────────────────────────────────
    @GetMapping("/buscar")
    public ResponseEntity<List<ClienteResponse>> buscar(@RequestParam String q) {
        return ResponseEntity.ok(clienteService.buscar(q));
    }

    // ─── Actualizar ───────────────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> update(@PathVariable UUID id,
                                                  @Valid @RequestBody ClienteRequest req) {
        return ResponseEntity.ok(clienteService.update(id, req));
    }

    // ─── Cambiar estado ───────────────────────────────────────────────────────
    @PatchMapping("/{id}/estado")
    public ResponseEntity<ClienteResponse> cambiarEstado(@PathVariable UUID id,
                                                         @RequestBody Map<String, String> body) {
        EstadoCliente estado = EstadoCliente.valueOf(body.get("estado"));
        return ResponseEntity.ok(clienteService.cambiarEstado(id, estado));
    }

    // ─── Asignar sucursal y generar casillero ─────────────────────────────────
    @PatchMapping("/{id}/sucursal")
    public ResponseEntity<ClienteResponse> asignarSucursal(@PathVariable UUID id,
                                                           @RequestBody Map<String, UUID> body) {
        return ResponseEntity.ok(clienteService.asignarSucursal(id, body.get("sucursalId")));
    }
}

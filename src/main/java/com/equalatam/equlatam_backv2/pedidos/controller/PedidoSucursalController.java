package com.equalatam.equlatam_backv2.pedidos.controller;

import com.equalatam.equlatam_backv2.cliente.dto.response.ClienteResponse;
import com.equalatam.equlatam_backv2.pedidos.dto.request.PedidoRequest;
import com.equalatam.equlatam_backv2.pedidos.dto.response.PedidoResponse;
import com.equalatam.equlatam_backv2.pedidos.service.PedidoSucursalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pedidos/sucursal")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'AGENTE')")
public class PedidoSucursalController {

    private final PedidoSucursalService pedidoSucursalService;

    // ─── Buscar cliente por cédula ────────────────────────────────────────────
    @GetMapping("/cliente")
    public ResponseEntity<ClienteResponse> buscarCliente(
            @RequestParam String cedula) {
        return ResponseEntity.ok(pedidoSucursalService.buscarClientePorCedula(cedula));
    }

    // ─── Registrar pedido presencial ──────────────────────────────────────────
    @PostMapping
    public ResponseEntity<PedidoResponse> registrarPedidoPresencial(
            @Valid @RequestBody PedidoRequest req,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidoSucursalService.registrarPedidoPresencial(req, username));
    }

    // ─── Pedidos registrados en la sucursal del agente ────────────────────────
    @GetMapping("/mis-pedidos")
    public ResponseEntity<List<PedidoResponse>> pedidosDeSucursal(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                pedidoSucursalService.pedidosDeSucursalAgente(userDetails.getUsername()));
    }
}
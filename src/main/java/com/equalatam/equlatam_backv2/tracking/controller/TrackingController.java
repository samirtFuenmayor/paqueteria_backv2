package com.equalatam.equlatam_backv2.tracking.controller;

import com.equalatam.equlatam_backv2.tracking.dto.response.TrackingEventoResponse;
import com.equalatam.equlatam_backv2.tracking.dto.response.TrackingResumenResponse;
import com.equalatam.equlatam_backv2.tracking.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final TrackingService trackingService;

    // ─── ENDPOINTS PÚBLICOS (clientes sin JWT) ────────────────────────────────

    // Cliente busca su pedido por número
    @GetMapping("/public/pedido/{numeroPedido}")
    public ResponseEntity<TrackingResumenResponse> trackingPublicoPorNumero(
            @PathVariable String numeroPedido) {
        return ResponseEntity.ok(trackingService.getHistorialPublico(numeroPedido));
    }

    // Cliente busca por tracking externo (Amazon, FedEx, etc.)
    @GetMapping("/public/tracking-externo/{trackingExterno}")
    public ResponseEntity<TrackingResumenResponse> trackingPublicoPorTracking(
            @PathVariable String trackingExterno) {
        return ResponseEntity.ok(trackingService.getHistorialPorTracking(trackingExterno));
    }

    // ─── ENDPOINTS PARA EMPLEADOS (requieren JWT) ─────────────────────────────

    // Historial completo con notas internas
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<TrackingResumenResponse> historialCompleto(
            @PathVariable UUID pedidoId) {
        return ResponseEntity.ok(trackingService.getHistorialCompleto(pedidoId));
    }

    // Todos los pedidos de un cliente con su historial
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<TrackingResumenResponse>> trackingPorCliente(
            @PathVariable UUID clienteId) {
        return ResponseEntity.ok(trackingService.getTrackingPorCliente(clienteId));
    }

    // Eventos de una sucursal
    @GetMapping("/sucursal/{sucursalId}")
    public ResponseEntity<List<TrackingEventoResponse>> eventosPorSucursal(
            @PathVariable UUID sucursalId) {
        return ResponseEntity.ok(trackingService.getEventosPorSucursal(sucursalId));
    }

    // Eventos de un despacho
    @GetMapping("/despacho/{numeroDespacho}")
    public ResponseEntity<List<TrackingEventoResponse>> eventosPorDespacho(
            @PathVariable String numeroDespacho) {
        return ResponseEntity.ok(trackingService.getEventosPorDespacho(numeroDespacho));
    }

    // Registrar evento manual (empleado agrega nota o evento especial)
    @PostMapping("/pedido/{pedidoId}/evento")
    public ResponseEntity<TrackingEventoResponse> registrarEventoManual(
            @PathVariable UUID pedidoId,
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal UserDetails userDetails) {

        String descripcion = (String) body.get("descripcion");
        UUID sucursalId = body.get("sucursalId") != null ?
                UUID.fromString((String) body.get("sucursalId")) : null;
        String ubicacion = (String) body.get("ubicacionDetalle");
        String notaInterna = (String) body.get("notaInterna");
        boolean visible = body.get("visibleParaCliente") == null ||
                (boolean) body.get("visibleParaCliente");
        String username = userDetails != null ? userDetails.getUsername() : null;

        return ResponseEntity.ok(trackingService.registrarEventoManual(
                pedidoId, descripcion, sucursalId, ubicacion,
                notaInterna, visible, username));
    }
}

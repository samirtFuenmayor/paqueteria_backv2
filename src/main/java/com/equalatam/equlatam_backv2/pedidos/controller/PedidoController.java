package com.equalatam.equlatam_backv2.pedidos.controller;

import com.equalatam.equlatam_backv2.pedidos.dto.request.ComprobanteRequest;
import com.equalatam.equlatam_backv2.pedidos.dto.request.PedidoRequest;
import com.equalatam.equlatam_backv2.pedidos.dto.response.PedidoResponse;
import com.equalatam.equlatam_backv2.pedidos.dto.response.PedidoResumenResponse;
import com.equalatam.equlatam_backv2.pedidos.entity.EstadoPedido;
import com.equalatam.equlatam_backv2.pedidos.entity.Pedido;
import com.equalatam.equlatam_backv2.pedidos.entity.SubcategoriaProducto;
import com.equalatam.equlatam_backv2.pedidos.entity.TipoProducto;
import com.equalatam.equlatam_backv2.pedidos.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @GetMapping("/cliente/{clienteId}/resumen")
    public ResponseEntity<List<PedidoResumenResponse>> resumenCliente(
            @PathVariable UUID clienteId) {
        return ResponseEntity.ok(pedidoService.resumenPorCliente(clienteId));
    }

    @GetMapping("/admin/pendientes-facturar")
    public ResponseEntity<List<PedidoResumenResponse>> pendientesFacturar() {
        return ResponseEntity.ok(pedidoService.pedidosPendientesFacturar());
    }

    // ─── Admin marca qué items llegaron ──────────────────────────────────────────
    @PatchMapping("/{pedidoId}/items/{itemId}/llegada")
    public ResponseEntity<PedidoResponse> marcarItemLlegado(
            @PathVariable UUID pedidoId,
            @PathVariable UUID itemId,
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal UserDetails userDetails) {

        Boolean llego = (Boolean) body.get("llego");
        String observacion = (String) body.get("observacion");
        String username = userDetails != null ? userDetails.getUsername() : null;

        return ResponseEntity.ok(
                pedidoService.marcarItemLlegado(pedidoId, itemId, llego, observacion, username));
    }

    // ─── Admin marca recepción completa y notifica al cliente ─────────────────────
    @PostMapping("/{pedidoId}/confirmar-recepcion")
    public ResponseEntity<PedidoResponse> confirmarRecepcion(
            @PathVariable UUID pedidoId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(pedidoService.confirmarRecepcion(pedidoId, username));
    }

    // ─── Cliente decide: despachar lo que llegó o esperar ────────────────────────
    @PostMapping("/{pedidoId}/decision-despacho")
    public ResponseEntity<PedidoResponse> decisionDespacho(
            @PathVariable UUID pedidoId,
            @RequestBody Map<String, String> body) {

        boolean despacharParcial = Boolean.parseBoolean(body.get("despacharParcial"));
        return ResponseEntity.ok(pedidoService.decisionDespacho(pedidoId, despacharParcial));
    }

    // ─── Devuelve subcategorías disponibles para un TipoProducto ──────────────
    @GetMapping("/subcategorias/{tipoProducto}")
    public ResponseEntity<List<String>> subcategoriasPorTipo(
            @PathVariable TipoProducto tipoProducto) {
        List<String> subs = Arrays.stream(SubcategoriaProducto.values())
                .filter(s -> s.perteneceA(tipoProducto))
                .map(Enum::name)
                .collect(Collectors.toList());
        return ResponseEntity.ok(subs);
    }

    // ─── Paso 2: cliente/agente sube comprobante ──────────────────────────────────
    @PatchMapping("/{id}/comprobante")
    public ResponseEntity<PedidoResponse> subirComprobante(
            @PathVariable UUID id,
            @Valid @RequestBody ComprobanteRequest req,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(pedidoService.subirComprobante(id, req, username));
    }

    // ─── Paso 3: admin verifica el pago ──────────────────────────────────────────
    @PatchMapping("/{id}/verificar-pago")
    public ResponseEntity<PedidoResponse> verificarPago(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {

        boolean aprobado = Boolean.parseBoolean(body.get("aprobado"));
        String motivo = body.get("motivoRechazo");
        String username = userDetails != null ? userDetails.getUsername() : null;

        return ResponseEntity.ok(
                pedidoService.verificarPago(id, aprobado, motivo, username));
    }

    // ─── Admin obtiene el comprobante para verificar ──────────────────────────────
    @GetMapping("/{id}/comprobante")
    public ResponseEntity<Map<String, String>> obtenerComprobante(
            @PathVariable UUID id) {
        Pedido pedido = pedidoService.getPedidoOrThrow(id);

        if (pedido.getComprobanteBase64() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of(
                "comprobanteBase64", pedido.getComprobanteBase64()));
    }
}
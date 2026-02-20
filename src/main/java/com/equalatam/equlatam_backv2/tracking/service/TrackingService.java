package com.equalatam.equlatam_backv2.tracking.service;

import com.equalatam.equlatam_backv2.exception.ResourceNotFoundException;
import com.equalatam.equlatam_backv2.pedidos.entity.EstadoPedido;
import com.equalatam.equlatam_backv2.pedidos.entity.Pedido;
import com.equalatam.equlatam_backv2.pedidos.repository.PedidoRepository;
import com.equalatam.equlatam_backv2.repository.UserRepository;
import com.equalatam.equlatam_backv2.sucursales.repository.SucursalRepository;
import com.equalatam.equlatam_backv2.tracking.dto.response.TrackingEventoResponse;
import com.equalatam.equlatam_backv2.tracking.dto.response.TrackingResumenResponse;
import com.equalatam.equlatam_backv2.tracking.entity.TrackingEvento;
import com.equalatam.equlatam_backv2.tracking.repository.TrackingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrackingService {

    private final TrackingRepository trackingRepository;
    private final PedidoRepository pedidoRepository;
    private final SucursalRepository sucursalRepository;
    private final UserRepository userRepository;

    // ─── Registrar evento automáticamente (llamado desde PedidoService) ───────
    @Transactional
    public void registrarEvento(Pedido pedido, EstadoPedido estado,
                                String descripcion, String username,
                                UUID sucursalId, String numeroDespacho,
                                boolean visibleParaCliente, String notaInterna) {
        TrackingEvento evento = new TrackingEvento();
        evento.setPedido(pedido);
        evento.setEstado(estado);
        evento.setDescripcion(descripcion);
        evento.setVisibleParaCliente(visibleParaCliente);
        evento.setNotaInterna(notaInterna);
        evento.setNumeroDespacho(numeroDespacho);

        if (sucursalId != null) {
            sucursalRepository.findById(sucursalId).ifPresent(evento::setSucursal);
        }

        if (username != null) {
            userRepository.findByUsername(username).ifPresent(evento::setRegistradoPor);
        }

        trackingRepository.save(evento);
    }

    // ─── Registrar evento manual por empleado ─────────────────────────────────
    @Transactional
    public TrackingEventoResponse registrarEventoManual(UUID pedidoId, String descripcion,
                                                        UUID sucursalId, String ubicacionDetalle,
                                                        String notaInterna, boolean visibleParaCliente,
                                                        String username) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pedido no encontrado: " + pedidoId));

        TrackingEvento evento = new TrackingEvento();
        evento.setPedido(pedido);
        evento.setEstado(pedido.getEstado());   // Estado actual del pedido
        evento.setDescripcion(descripcion);
        evento.setUbicacionDetalle(ubicacionDetalle);
        evento.setVisibleParaCliente(visibleParaCliente);
        evento.setNotaInterna(notaInterna);

        if (sucursalId != null) {
            sucursalRepository.findById(sucursalId).ifPresent(evento::setSucursal);
        }

        if (username != null) {
            userRepository.findByUsername(username).ifPresent(evento::setRegistradoPor);
        }

        return TrackingEventoResponse.from(trackingRepository.save(evento));
    }

    // ─── Ver historial completo (empleados) ───────────────────────────────────
    public TrackingResumenResponse getHistorialCompleto(UUID pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pedido no encontrado: " + pedidoId));

        List<TrackingEventoResponse> historial = trackingRepository
                .findByPedidoIdOrderByFechaEventoDesc(pedidoId)
                .stream()
                .map(TrackingEventoResponse::from)
                .collect(Collectors.toList());

        return TrackingResumenResponse.from(pedido, historial);
    }

    // ─── Ver historial público (clientes) por número de pedido ───────────────
    public TrackingResumenResponse getHistorialPublico(String numeroPedido) {
        Pedido pedido = pedidoRepository.findByNumeroPedido(numeroPedido)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pedido no encontrado: " + numeroPedido));

        List<TrackingEventoResponse> historial = trackingRepository
                .findByPedidoIdAndVisibleParaClienteTrueOrderByFechaEventoDesc(pedido.getId())
                .stream()
                .map(TrackingEventoResponse::fromPublic)
                .collect(Collectors.toList());

        return TrackingResumenResponse.from(pedido, historial);
    }

    // ─── Ver historial público por casillero + tracking externo ──────────────
    public TrackingResumenResponse getHistorialPorTracking(String trackingExterno) {
        Pedido pedido = pedidoRepository.findAll().stream()
                .filter(p -> trackingExterno.equals(p.getTrackingExterno()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pedido no encontrado con tracking: " + trackingExterno));

        List<TrackingEventoResponse> historial = trackingRepository
                .findByPedidoIdAndVisibleParaClienteTrueOrderByFechaEventoDesc(pedido.getId())
                .stream()
                .map(TrackingEventoResponse::fromPublic)
                .collect(Collectors.toList());

        return TrackingResumenResponse.from(pedido, historial);
    }

    // ─── Ver todos los eventos de una sucursal ────────────────────────────────
    public List<TrackingEventoResponse> getEventosPorSucursal(UUID sucursalId) {
        return trackingRepository.findBySucursalIdOrderByFechaEventoDesc(sucursalId)
                .stream()
                .map(TrackingEventoResponse::from)
                .collect(Collectors.toList());
    }

    // ─── Ver todos los eventos de un despacho ─────────────────────────────────
    public List<TrackingEventoResponse> getEventosPorDespacho(String numeroDespacho) {
        return trackingRepository.findByNumeroDespachoOrderByFechaEventoDesc(numeroDespacho)
                .stream()
                .map(TrackingEventoResponse::from)
                .collect(Collectors.toList());
    }

    // ─── Tracking de todos los pedidos de un cliente ─────────────────────────
    public List<TrackingResumenResponse> getTrackingPorCliente(UUID clienteId) {
        return pedidoRepository.findByClienteId(clienteId)
                .stream()
                .map(pedido -> {
                    List<TrackingEventoResponse> historial = trackingRepository
                            .findByPedidoIdAndVisibleParaClienteTrueOrderByFechaEventoDesc(pedido.getId())
                            .stream()
                            .map(TrackingEventoResponse::fromPublic)
                            .collect(Collectors.toList());
                    return TrackingResumenResponse.from(pedido, historial);
                })
                .collect(Collectors.toList());
    }
}
package com.equalatam.equlatam_backv2.tracking.repository;

import com.equalatam.equlatam_backv2.pedidos.entity.EstadoPedido;
import com.equalatam.equlatam_backv2.tracking.entity.TrackingEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TrackingRepository extends JpaRepository<TrackingEvento, UUID> {

    // Historial completo de un pedido (más reciente primero)
    List<TrackingEvento> findByPedidoIdOrderByFechaEventoDesc(UUID pedidoId);

    // Solo eventos visibles para el cliente
    List<TrackingEvento> findByPedidoIdAndVisibleParaClienteTrueOrderByFechaEventoDesc(UUID pedidoId);

    // Último evento de un pedido
    @Query("SELECT t FROM TrackingEvento t WHERE t.pedido.id = :pedidoId " +
            "ORDER BY t.fechaEvento DESC LIMIT 1")
    TrackingEvento findUltimoEventoByPedidoId(@Param("pedidoId") UUID pedidoId);

    // Eventos por estado
    List<TrackingEvento> findByEstado(EstadoPedido estado);

    // Eventos por sucursal
    List<TrackingEvento> findBySucursalIdOrderByFechaEventoDesc(UUID sucursalId);

    // Eventos por despacho
    List<TrackingEvento> findByNumeroDespachoOrderByFechaEventoDesc(String numeroDespacho);
}
